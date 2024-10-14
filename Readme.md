# Funzionamento del motore

## Idea di base
Il motore di gioco orchestra tutte le interazioni del gioco e modifica in modo appropriato tutti i parametri del gioco.
Ad una nuova campagna il motore viene inizializzato con un **context** che comprende tutto sulla campagna (località, **npc_character**, etc..) e i **pc_character**. Il motore fa interagire i diversi elementi della campagna, mantiene in memoria tutte le operazioni svolte e aggiorna lo stato di volta in volta.

In Pathfinder 2e ci sono 3 modalità di gioco:
 * *Encounter Mode*: modalità per combattimenti.
 * *Exploration Mode*: modalità per attività di esplorazione.
 * *Downtime*: modalità per attività che richiedono molto tempo come ad esempio forgiare una spada o fare viaggi lunghi. 

 Una caratteristica che è condivisa dalle tre modalità è che i giocatori scelgono di fare delle azioni (proposte dal motore). 

 L'oggetto **State** dovrebbe contenere le seguenti informazioni 
 * *current_time*: indica il tempo attuale.
 * *game_mode*: indica una delle 3 modalità di gioco menzionate sopra.
  * il *game_mode* Encounter sarà un oggetto a se stante in cui gestire il combattimento.
 * *actual_scene*: l'oggetto **scene** conterrà tutto ciò che riguarda l'ambiente, i pc e gli npc inclusi nella scena.


 
 Il motore di gioco lavora diversamente a seconda del *game_mode*. 
 
 ## Exploration Mode
 Probabilmente le idee contenute qui sono applicabili, con una certa misura, anche alle altre modalità. Dunque Immagino di dividere le interazioni possibili in **Layers**. Ogni *layer* è specifico di un tipo interazione. Essendo un gioco di roleplay l'interazione più base è quella del dialogo con gli npc. Dunque:
 * *Conversational Layer*

### Conversational Layer

l'oggetto **npc_character** ha un metodo *to_speak(**pc_character**,**what**)*. Il metodo viene invocato ogniqualvolta un pc desidera parlare con un npc. inoltre possiede un oggetto *Hashmap*<String pc_name,**conversation_log**> dove sono memorizzati i log delle conversazioni. **conversation_log** è un oggetto che ha diversi metodi e utility per estrarre la conversazione. **npc_character** possiede informazioni sulla località in cui è presente e un **private_context** che viene aggiornato di volta in volta. Ad esempio se l'npc cambia località (da una taverna in una foresta) allora il **private_context** viene aggiornato (conterrà per esempio, il motivo per cui si è spostato). Durante, l'inizializzazione del motore di gioco, una descrizione del **context** di gioco viene aggiunta all'**private_context** dell'npc. 

# Struttura del motore, di pc_character e di npc_character

## Motore
Il motore ha come campi:

* lista di *npc_character*
* lista di *pc_character*
* *context* generale di gioco
* *state*: lo stato attuale.
* lista di *scene*: una lista delle scene della campagna.
* *Logger*: un log di ogni singola cosa succede durante la campagna.
* *AI* : un oggetto che ha tutto quello che serve per interpellare l'AI

Piccola anticipazione: sarà l'AI a utilizzare questo motore (openAPI function calls);

Il motore ha come metodi i soliti getters and setters. inoltre ha i metodi:

* *create_pc_character(pc_character_info)*: crea un nuovo pc
* *create_npc_character(npc_character_info)*: crea un nuovo npc
* *create_context(context_info)*: crea e imposta un contesto
* *create_scene(scene_info)*: crea una scena e la carica nella lista di scene;
* *update_state()*: fa l'update dello stato
* *initializeAI(Api_key)*: inizializza la classe AI e imposta l'ApiKey (necessaria per utilizzare Openai).

Inoltre per ogni singola azione possibile immaginabile nel gioco, esiste un metodo. Al momento vogliamo implementare l'azione *speak*
