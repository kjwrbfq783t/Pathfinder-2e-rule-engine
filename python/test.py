import pandas as pd
import json
import os
import numpy as np
from openai import OpenAI

# Imposta la tua API key di OpenAI
client=OpenAI()
# Funzione per ottenere l'embedding da OpenAI
def get_embedding(text):
    response = client.embeddings.create(
        input=text,
        model="text-embedding-3-large"  # Usa il modello embedding più adatto
    )
    return response.data[0].embedding

# Percorso del file CSV esistente
csv_file_path = os.path.join('python','resources', 'functionsEmbeddings.csv')  # Sostituisci con il percorso corretto del tuo CSV

# Carica il DataFrame dal file CSV
df = pd.read_csv(csv_file_path)

# Converti la colonna 'embedding' in una lista di vettori numpy
df['embedding'] = df['embedding'].apply(lambda x: np.fromstring(x.strip('[]'), sep=',') if isinstance(x, str) else x)

# Funzione per trovare la funzione migliore in base alla richiesta dell'utente
def find_best_match(user_request):
    # Ottieni l'embedding della richiesta
    request_embedding = get_embedding(user_request)
    
    # Calcola la similarità coseno tra l'embedding della richiesta e gli embedding nel DataFrame
    df['similarity'] = df['embedding'].apply(lambda x: np.dot(request_embedding, x) / (np.linalg.norm(request_embedding) * np.linalg.norm(x)))

    # Trova l'indice del massimo della similarità
    best_index = df['similarity'].idxmax()
    
    # Restituisci le informazioni desiderate
    return df.loc[best_index, ['nome_funzione', 'argomenti', 'descrizione']]

# Esempio di richiesta dell'utente
user_request="enterLocation: Il personaggio si sposta in una nuova area o luogo, iniziando una nuova fase del suo viaggio e interagendo con l'ambiente circostante"
best_match = find_best_match(user_request)

# Mostra il risultato
print(user_request)
print("Funzione:", best_match['nome_funzione'])
print("Argomenti:", best_match['argomenti'])
print("Descrizione:", best_match['descrizione'])
