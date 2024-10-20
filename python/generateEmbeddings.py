from openai import OpenAI
import pandas as pd
import json
import os

client = OpenAI()



# Percorso del file JSON (resource è la cartella in cui si trova il file)
json_file_path = os.path.join('python','resources', 'functionDefs.json')

# Funzione per ottenere embedding da OpenAI
def get_embedding(text):
    response = client.embeddings.create(
        input=text,
        model="text-embedding-3-large"  # Usa il modello embedding più adatto
    )
    return response.data[0].embedding

# Carica il file JSON
with open(json_file_path, 'r', encoding='utf-8') as f:
    functions = json.load(f)

# Crea una lista per immagazzinare i dati
data = []

# Cicla attraverso ciascuna funzione e ottieni gli embedding per gli esempi
for function in functions:
    
    nome_funzione = function['nome']
    argomenti=function['argomenti']
    descrizione=function['descrizione']
    embedding = get_embedding(descrizione)
    data.append({
             'nome_funzione': nome_funzione,
            'argomenti': argomenti,
            'descrizione': descrizione,
            'embedding': embedding })
       
# Crea il DataFrame dai dati raccolti
df = pd.DataFrame(data)



# Salva il DataFrame in un file CSV per usi futuri
df.to_csv( os.path.join('python','resources','functionsEmbeddings.csv'), index=False)
