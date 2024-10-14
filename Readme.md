# Funzionamento  e struttura del motore

## Idea di base
Il motore di gioco orchestra tutte le interazioni del gioco e modifica in modo appropriato tutti i parametri del gioco.
Ad una nuova campagna il motore viene inizializzato con un **context** che comprende tutto sulla campagna (località, **npc_character**, etc..) e i **pc_character**. Il motore fa interagire i diversi elementi della campagna, mantiene in memoria tutte le operazioni svolte e aggiorna di volta in volta lo stato.
L'oggetto **State** contiene dunque le informazioni dello stato attuali.

In Pathfinder 2e ci sono 3 modalità di gioco:
 * *Encounter Mode*: modalità per combattimenti.
 * *Exploration Mode*: modalità per attività di esplorazione.
 * *Downtime*: modalità per attività che richiedono molto tempo come ad esempio forgiare una spada o fare viaggi lunghi. 

 Una caratteristica che è condivisa dalle tre modalità è che i giocatori scelgono di fare delle azioni (proposte dal motore). 

 L'oggetto **State** dovrebbe contenere le seguenti informazioni 
 * *current_time*: indica il tempo attuale.
 * *game_mode*: indica una delle 3 modalità di gioco menzionate sopra.
  * il *game_mode* Encounter sarà un oggetto a se stante in cui gestire il combattimento.
 * *pc_charachters*: la lista dei personaggi giocanti nella scena di gioco.
    * ogni *pc_charachter* oltre alla sua definizione completa 
 * *npc_characters*: lista dei personaggi non giocanti nella scena di gioco.
    * ogni npc_characters contiene la sua definizione e il suo *behaviour* (utilizzabile dall'IA per simulare l'npc) e mantiene un log delle conversazioni.
 * *context*: un oggetto che rappresenta la contesto in cui si svolge la parte di gioco attuale (griglia, edifici, elementi di natura, oggetti, contesto dell'avventura)

 Il motore di gioco lavora diversamente a seconda del *game_mode*. 
 
 ## Exploration Mode
 Probabilmente le idee contenute qui sono applicabili, con una certa misura, anche alle altre modalità. Dunque Immagino di dividere le interazioni possibili in **Layers**. Ogni *layer* è specifico di un tipo interazione. Essendo un gioco di roleplay l'interazione più base è quella del dialogo con gli npc. Dunque:
 * *Conversational Layer*

### Conversational Layer

l'oggetto **npc_character** ha un metodo *to_speak(**pc_character**,**what**)*. Il metodo viene invocato ogniqualvolta un pc desidera parlare con un npc. inoltre possiede un oggetto *Hashmap*<String pc_name,**conversation_log**> dove sono memorizzati i log delle conversazioni. **conversation_log** è un oggetto che ha diversi metodi e utility per estrarre la conversazione. **npc_character** possiede anche un **actual_contest** che viene aggiornato di volta in volta. Ad esempio se l'npc cambia località (da una taverna in una foresta) allora l'**actual_contest** viene aggiornato. Durante, l'inizializzazione del motore di gioco, una descrizione testuale del **context** di gioco viene aggiunta all'**actual_contest** dell'npc.