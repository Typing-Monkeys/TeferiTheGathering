import requests
import json
from time import sleep
from os.path import exists
import inquirer
from pprint import pprint
from os import remove

from rich.console import Console
from rich.table import Table
from rich.progress import track
from rich.progress import Progress
from rich.prompt import Prompt
from rich.live import Live
from rich.layout import Layout


# --- Global VARS
CARD_FILE = "all_cards.json"
LAST_INDEX_FILE = ".lastindex"

API_URL = "https://www.afterlifegdr.com/test/mtg/testjson.php"

CMDS_MAP = ['Trova le carte esistenti',
            'Esplora le carte trovate',
            'Rimuovi tutti i file generati da questo script',
            'Cerca nelle carte',
            ]


# --- Output Formatted Consoles
console = Console()
success_console = Console(stderr=True, style="bold green")
info_console = Console(stderr=True, style="bold")
error_console = Console(stderr=True, style="bold red")


# --- Aux Functions
def get_card(card_id: int) -> dict:
    response = requests.get(
        API_URL,
        params={'cardid': card_id},
        timeout=3
    )

    if response.status_code != 200:
        error_console.print("Errore nella chiamata API !")
        return None

    return response.json()


def generate_card_table(cards: list, print_table=True) -> Table:
    table = Table(title="Carte Trovate", show_lines=True)

    table.add_column("ID", style="cyan", no_wrap=True)
    table.add_column("Nome", style="magenta")
    table.add_column("Keyword Abilities", style="red")
    table.add_column("Subtipes", style="green")
    table.add_column("Text Abilities", style="white")
    
    for card in cards:
        table.add_row(
            str(card['card']['cardID']),
            card['card']['cardName'],
            ", ".join(card['face1']['keywordAbilities']),
            ", ".join(card['face1']['subtypes']),
            " | ".join(card['face1']['abilities'])
        )
    
    if print_table:
        console.print(table)
        info_console.print(f"Totale Carte: {len(cards)}")

    return table


def check_cards_file(print_message=True):
    if not exists(CARD_FILE):
        if print_message:
            error_console.print("Devi prima trovare le carte !")
        
        return False

    return True


def open_cards_file(print_message=True):
    if not exists(CARD_FILE):
            if print_message:
                error_console.print("Devi prima trovare le carte !")
            
            return None
    
    with open(CARD_FILE, "r") as conf:
        if print_message:    
            info_console.print(" Riapro la lista di carte già trovate")
        
        cards = json.loads(conf.read())
    
    return cards


# --- Commands Functions
def query_all_cards(start=0, end=3000):
    cards = []

    if isinstance(end, str):
        if end != '':
            end = int(end)
        else:
            end = 3000

    if isinstance(start, str):
        if start != '':
            start = int(start)
        else:
            start = 0

    if exists(LAST_INDEX_FILE) and start == 0:
        with open(LAST_INDEX_FILE, "r") as conf:
            info_console.print(" Riprendo dall'ultimo id provato")
            start = int(conf.read())

    if exists(CARD_FILE):
        with open(CARD_FILE, "r") as conf:
            info_console.print(" Riapro la lista di carte già trovate")
            cards = json.loads(conf.read())

    max_range = range(start, end)
    info_console.print(f" Inizio la ricerca degli ID (da {start} a {end})")

    try:
        with Progress(console=success_console) as progres:
            task = progres.add_task(
                "Trovo le carte ...", completed=start, total=end)
            for id in max_range:
                sleep(.5)
                progres.advance(task)
                progres.update(
                    task, description=f"Trovo le carte (ID: {id}) ...")

                tmp = get_card(id)

                if tmp is None:
                    continue
                
                success_console.print(f"  ++ Trovata Carta {id}")
                
                if tmp in cards:
                    success_console.print("    [yellow]Carta esistente ![/yellow]")
                    continue

                cards.append(tmp)

    except KeyboardInterrupt:
        info_console.print(" Interrotta ricerca ")
    except Exception:
        error_console.print(" Si è verificato qualche problema :(")
    finally:
        with open(LAST_INDEX_FILE, "w") as conf:
            info_console.print(" Salvo l'ultimo id provato")
            conf.write(f"{id}")

    info_console.print(" Ricerca Terminata")

    return cards


def browse_cards():
    cards = open_cards_file()
    
    if cards is None:
        return

    _ = generate_card_table(cards)


def search_card():
    if not exists(CARD_FILE):
        error_console.print("Devi prima trovare le carte !")
        return

    with open(CARD_FILE, "r") as conf:
        info_console.print(" Riapro la lista di carte già trovate")
        cards = json.loads(conf.read())

    while True:
        search_word = Prompt.ask("[[yellow bold]?[/yellow bold]] Cerca una parola", default="'q' per uscire")

        if search_word == 'q':
            break

        filtered_cards = [card for card in cards if search_word in card['card']['cardName'] or search_word == str(card['card']['cardID']) or any(search_word in s.lower() for s in card['face1']['keywordAbilities']) or any(search_word in s.lower() for s in card['face1']['subtypes'])  or any(search_word in s.lower() for s in card['face1']['types'])]
        
        _ = generate_card_table(filtered_cards)


def main():
    match inquirer.list_input("Comandi", choices=list(CMDS_MAP)):
        case 'Trova le carte esistenti':
            start = inquirer.text("ID Partenza (default l'ultimo usato)")
            end = inquirer.text("ID Fine (default 3000)")
            result = query_all_cards(start=start, end=end)

            with open(CARD_FILE, "w") as f:
                f.write(json.dumps(result))

        case 'Esplora le carte trovate':
            browse_cards()

        case 'Cerca nelle carte':
            search_card()

        case 'Rimuovi tutti i file generati da questo script':
            info_console.print(" Rimuovo tutti i file generati")
            remove(CARD_FILE)
            remove(LAST_INDEX_FILE)


if __name__ == "__main__":
    main()
