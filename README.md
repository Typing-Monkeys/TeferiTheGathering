# TeferiTheGathering

## Installazione

Per riuscire ad importare il progetto in Eclipse seguire i seguenti punti:

1. Clonare il progetto: eseguire il seguente comando in una cartella a tua scelta 
```bash
git clone https://github.com/Typing-Monkeys/TeferiTheGathering.git
cd TeferiTheGathering
```

2. Installare le dipendenze di NodeJS: eseguire i seguenti comandi per installare i pacchetti necessari per far funzionare il server NodeJs. Le dipendenze non sono state incluse nella repo per risparmiare spazio. Vedrai vari warning e messaggi di vulnerabilità critiche, tranquillo è tutto normale.
```bash
cd mtghub
npm install
```

3. Provare il correttu funzionamento di NodeJs: esegui il seguente comando, se non dà errori vuol dire che tutto funziona 
```bash
npm start
```

4. Importare il progetto in Eclipse: 
    - apri Eclipse, 
    - vai nel menu `File` e `Open Project from File System`. 
    - Premi il bottone `Directory...`,
    - Trova la cartella `TeferiTheGathering`e apri la cartella `mtgengine` (doppio click), solo dopo premi il bottone `open` !!
    - Premi `Finish`

5. Testare il progetto: ora trova il file `Main.java` dall'albero del progetto, poi Tasto destro e `Run As`, `Java Application`. Dovrebbe funzionare senza problemi.

### Known Issue

#### **Drools Errors after restart** 

Può succedere che una volta riaperto Eclipse, questo non riesce a trovare la libreria Drools. In questo caso fai Tasto destro sul progetto, `Properties`, `Drools` e assicurati che la checkbox `Enable project specific settings` **non** sia spuntata.

In caso guarda questa [issue](https://github.com/Typing-Monkeys/TeferiTheGathering/issues/1).

#### **Git/GitHub-Desktop Windows** 

In windows, utilizzare GitHub Desktop e, di conseguenza anche git cli nella powershell/cmd, 
romperà il progetto andando a modificare cose che non dovrebbe, rovina il file Rules.drl 
cambiando indentazione e spaziatura e non ha idea di come gestire le cartelle che iniziano con il `.`. 
L'unico modo per risolvere è **NON** utilizzarlo 🙃 e usare **SOLO** git su un ambiente linux (la WSL va benissimo !).
Se non hai idea di quale versione di git hai utilizza il seguente comando:

```bash
git --version
```

Se in output avrai una roba del tipo:

```
git 2.83.1-windows.1
```

stai utilizzando git sbagliato !!

In caso ne avessi bisogno puoi trovare una guida su come abilitare WSL in Windows [qui](https://learn.microsoft.com/it-it/windows/wsl/install)
