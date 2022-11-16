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

- Può succedere che una volta riaperto Eclipse, questo non riesce a trovare la libreria Drools. In questo caso fai Tasto destro sul progetto, `Properties`, `Drools` e assicurati che la checkbox `Enable project specific settings` **non** sia spuntata.
In caso guarda questa [issue](https://github.com/Typing-Monkeys/TeferiTheGathering/issues/1)