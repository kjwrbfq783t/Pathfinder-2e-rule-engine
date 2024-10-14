# Funzionamento  e struttura del motore

## Idea di base
Fondamentalmente il motore prende in input uno stato e ne fornisce uno nuovo da riempire con informazioni ottenute dall'user., cosicchè possa venir dato in pasto al motore nuovamente e così via. Per elaborare il nuovo stato il motore fa affidamento a risorse esterne come IA per elaborare risultati non deterministici, ad esempio, la risposta che darebbe un NPC. L'oggetto **State** deve contenere tutte le informazioni necessarie per determinare il prossimo stato del gioco. In Pathfinder 2e ci sono 3 modalità di gioco:
 * *Encounter Mode*: modalità per combattimenti.
 * *Exploration Mode*: modalità per attività di esplorazione.
 * *Downtime*: modalità per attività che richiedono molto tempo come ad esempio forgiare una spada o fare viaggi lunghi. 

 Una caratteristica che è condivisa dalle tre modalità è che i giocatori scelgono di fare delle azioni (proposte dal motore). 

 L'oggetto **State** dovrebbe contenere le seguenti informazioni necessarie:
 * *current_time*: indica il tempo attuale.
 * *game_mode*: indica una delle 3 modalità di gioco menzionate sopra.
 * *pc_charachters*: la lista dei personaggi giocanti nella scena di gioco.
    * ogni *pc_charachter* oltre alla sua definizione completa conterrà informazioni sull'azione scelta.
 * *npc_characters*: lista dei personaggi non giocanti nella scena di gioco.
 * *context*: un oggetto che rappresenta la contesto in cui si svolge la parte di gioco attuale (griglia, edifici, elementi di natura, oggetti, contesto dell'avventura)

 Il motore di gioco lavora diversamente a seconda del *game_mode*. 
 
 ## Exploration Mode
 Probabilmente le idee contenute qui sono applicabili, con una certa misura, anche alle altre modalità. Dunque Immagino di dividere le interazioni possibili in **Layers**. Ogni *layer* è specifico di un tipo interazione. Essendo un gioco di roleplay l'interazione più base è quella del dialogo con gli npc. Dunque:
 * *Conversational Layer*