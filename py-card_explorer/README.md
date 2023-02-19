# py-card_explorer

Questo è uno script in python che permette di trovare tutte le carte di Magic implementate nell'API
a cui Java fa riferimento per ottenere i dati.

## Installazione

È consigliato utilizzare un ambiente come `venv` o `anaconda` per eseguire questo script.
Il file `create_env.sh` creerà in automatico un ambiente `venv`.

Una volta creato l'abimente ed attivato (per venv `source .venv/bin/activate`) utilizzare il seguente comando per installare i requirements
necessari:

`pip install -r requirements.txt`

o in alternativa:

`python3 -m pip install -r requirements.txt`

## Utilizzo

Una volta installati i requirements necessari, se non avete attivato l'abimente fatelo (per venv `source .venv/bin/actiate`)
e lanciate lo script con

`python3 mtg_card_explorer.py`

Sono implementate 4 funzioni differenti:

- `Trova le carte esistenti`: avvia la ricerca delle carte presenti nell'API. 
Effettua una chiamata con ID progressivo, attende qualche secondo per evitare di venir bloccato
dal server e continua. È possibile specificare i seguenti parametri:
  - `ID start`: id della carta da cui partire per la ricerca
  - `ID end`: ultimo id della carta dopo il quale termina la ricerca
Se lasciati vuoti utilizzeranno l'ultimo id trovato durante la ricerca (verrà creato un file apposito dove saranno salvati i dati).
È possibile interrompere e riprendere la ricerca quando si vuole, lo script è abbastanza intelligente da non aggiungere doppioni.
Una volta terminata la ricerca verrà generato un file di nome `all_cards.json` dove saranno contenuti tutti i dati (sotto forma di josn)
delle carte trovate durante la ricerca.
- `Esplora le carte trovate`: una volta trovate tutte le carte con la fase
di ricerca apposita, questo stamperà a video una tebella con tutte le carte
trovate ed alcune loro proprietà di interesse.
- `Cerca nelle carte`: permette di cercare per parole chiavi all'interno di alcuni campi come:
  - `type`
  - `subtype`
  - `name`
  - `abilities`
- `Rimuovi tutti i file generati da questo script`: elimina tutti i file generati da questo script:
  - `all_cards.json`
  - `.lastindex`
