package com.posilcorp.OpenAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.posilcorp.CampaignManagerInterface;
import com.posilcorp.Campaign_Engine;

public class CampaignManagerIAOpenAi implements CampaignManagerInterface {
    private final String url = "https://api.openai.com/v1/chat/completions";

    private JSONArray conversation;
    Campaign_Engine campaign_Engine;
    JSONObject data;
    JSONObject response;
    JSONObject system_instruction_json;

    public CampaignManagerIAOpenAi() {

        data = new JSONObject();
        conversation = new JSONArray();
        JSONArray tools = new JSONArray();
        String systemMessage = "Sei un Game Master virtuale per un'avventura di ruolo. Il tuo compito è esclusivamente invocare le funzioni definite nel JSON array 'tools' per rispondere alle richieste dei giocatori. Non devi improvvisare o inventare nulla, limitandoti a interpretare l'ambiente e le azioni in base alle funzioni disponibili. Dopo aver ricevuto l'output delle funzioni, dovrai interpretarlo e presentarlo in maniera coinvolgente, utilizza discorsi diretti in prima persona! aggiungendo emoji dove appropriato per arricchire l'esperienza dei giocatori.\n"
                +
                "\n" +
                "Usa le seguenti funzioni per ogni richiesta dei giocatori:\n" +
                "\n" +
                "- **getEnvironment(nome_pg)**: Usa questa funzione per fornire la descrizione dell'ambiente in cui si trova un personaggio specifico (nome_pg). Includi emoji appropriate come 🌲 o 🏰 a seconda dell'ambiente descritto.\n"
                +
                "\n" +
                "- **changeScene(recipient, sceneName)**: Usa questa funzione quando un personaggio vuole cambiare scena. 'recipient' è il nome del personaggio e 'sceneName' è il nome della scena in cui vuole andare. Esempio: \"⚔️ [Nome_PG] si sposta nella scena [Nome_Scena]\".\n"
                +
                "\n" +
                "- **speak_to(sender, recipient, text)**: Usa questa funzione quando un personaggio vuole parlare a un NPC o parlare al vuoto nella scena. 'sender' è il nome del personaggio, 'recipient' è il nome dell'NPC o della scena, e 'text' è il messaggio che il personaggio vuole dire. Esempio: \"🗣️ [Nome_PG] dice a [Nome_NPC]: '[Testo]'\".\n"
                +
                "\n" +
                "- **getKnowScenes()**: Usa questa funzione per fornire una lista delle scene conosciute nel gioco. Presenta la lista con delle emoji. Esempio: \"🌍 Scene Disponibili: 1. 🏞️ Foresta Incantata 2. 🏰 Castello Antico 3. 🏙️ Villaggio Medievale\".\n"
                +
                "\n" +
                "- **drawWeapon(recipientName, weaponItemName)**: Usa questa funzione quando un personaggio vuole estrarre un'arma. Esempio: \"🗡️ [Nome_PG] estrae [Nome_Arma]\".\n"
                +
                "\n" +
                "- **putAway(recipientName, weaponItemName)**: Usa questa funzione quando un personaggio vuole rimettere a posto un'arma. Esempio: \"🔫 [Nome_PG] rimette a posto [Nome_Arma]\".\n"
                +
                "\n" +
                "- **openInventory(recipientName)**: Usa questa funzione quando un personaggio vuole aprire il suo inventario per scoprire cosa possiede. Esempio: \"🎒 [Nome_PG] apre il suo inventario\".\n"
                +
                "\n" +
                "- **retrieveStowedItem(recipientName, itemName,equipslot)**: Usa questa funzione quando un personaggio vuole recuperare un oggetto stivato in uno dei suoi contenitori in equipslot. Esempio: \"📦 [Nome_PG] recupera [Nome_Oggetto]\".\n"
                +
                "\n" +
                "- **stowe(recipientName, itemName, slot)**: Usa questa funzione quando un personaggio vuole stivare un oggetto in un container che si trova nello slot indicato (ad esempio, uno zaino). Esempio: \"🧳 [Nome_PG] stiva [Nome_Oggetto] nello slot [Nome_Slot]\".\n"
                +
                "\n" +
                "- **wear(recipientName, item_name, slot)**: Usa questa funzione quando un personaggio vuole indossare un oggetto nell'EquipSlot indicato. Esempio: \"👕 [Nome_PG] indossa [Nome_Oggetto] nello slot [Nome_Slot]\".\n"
                +
                "\n" +
                "- **give(recipientName, senderName, itemName)**: Usa questa funzione quando un personaggio vuole dare un oggetto a un altro personaggio. Esempio: \"🎁 [Nome_PG] dà [Nome_Oggetto] a [Nome_Altro_PG]\".\n"
                +
                "\n" +
                "- **take(recipientName, holderName, itemName)**: Usa questa funzione quando un personaggio vuole prendere un oggetto da un altro personaggio. Esempio: \"🤲 [Nome_PG] prende [Nome_Oggetto] da [Nome_Titolare]\".\n"
                +
                "\n" +
                "- **drawFrom(recipientName, slotName)**: Usa questa funzione quando un personaggio vuole rimuovere o disequipaggiare un oggetto da uno slot specifico. Esempio: \"🧤 [Nome_PG] rimuove un oggetto dallo slot [Nome_Slot]\".\n"
                +
                "\n" +
                "- **changeGrip(recipientName)**: Usa questa funzione quando un personaggio vuole cambiare l'impugnatura della sua arma da una mano a due mani, o viceversa. Esempio: \"✋ [Nome_PG] cambia l'impugnatura dell'arma\".\n"
                +
                "\n" +
                "- **swap(recipientName, weaponToSwapWith)**: Usa questa funzione quando un personaggio vuole scambiare l'arma che ha in mano con un'altra arma specifica. Esempio: \"🔄 [Nome_PG] scambia la sua arma con [Nome_Arma_Scambio]\".\n"
                +
                "\n" +
                "Il tuo unico obiettivo è rispondere alle azioni dei giocatori attraverso queste funzioni. Presenta ogni output con emoji per adattarlo a una chat Telegram. Non devi aggiungere descrizioni extra o improvvisare. Tutto deve essere strettamente legato alle funzioni disponibili.";

        system_instruction_json = new JSONObject().put("role", "system").put("content", systemMessage);
        String speak_to = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"speak_to\",\n" +
                "    \"description\": \"Send a message from one person to another\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"senderName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the person sending the message\"\n" +
                "        },\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the object receiving the message\"\n" +
                "        },\n" +
                "        \"text\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The content of the message\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"senderName\", \"recipientName\", \"text\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String changeScene = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"changeScene\",\n" +
                "    \"description\": \"Change the scene for a specific recipient\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the person for whom the scene is being changed\"\n" +
                "        },\n" +
                "        \"sceneName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the scene to switch to\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"sceneName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String getEnvironment = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"getEnvironment\",\n" +
                "    \"description\": \"Returns a textual description of the environment of the user\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"pcName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the person requesting information about the environment.\"\n"
                +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"pcName\"],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String getKnowScenes = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"getKnownScenes\",\n" +
                "    \"description\": \"Recupera una lista delle scene conosciute nel gioco\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {},\n" +
                "      \"required\": []\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String take = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"take\",\n" +
                "    \"description\": \"Represents the action of a character taking an item from another character\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character who is taking the item\"\n" +
                "        },\n" +
                "        \"holderName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character who holds the item\"\n" +
                "        },\n" +
                "        \"itemName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the item being taken\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"holderName\", \"itemName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String give = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"give\",\n" +
                "    \"description\": \"Represents the action of a character giving an item to another character\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character receiving the item\"\n" +
                "        },\n" +
                "        \"senderName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character giving the item\"\n" +
                "        },\n" +
                "        \"itemName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the item being given\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"senderName\", \"itemName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String wear = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"wear\",\n" +
                "    \"description\": \"Represents the action of a character wearing an item in the specified equip slot\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character wearing the item\"\n" +
                "        },\n" +
                "        \"item_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the item being worn\"\n" +
                "        },\n" +
                "        \"slot\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"enum\": [\n" +
                "            \"WEARED_SX_HAND\",\n" +
                "            \"WEARED_DX_HAND\",\n" +
                "            \"WEARED_TORSO\",\n" +
                "            \"WEARED_UNDERCOAT\",\n" +
                "            \"WEARED_BOOTS\",\n" +
                "            \"WEARED_HEAD\",\n" +
                "            \"WEARED_BELT\",\n" +
                "            \"WEARED_BACK\",\n" +
                "            \"WEARED_VITA\"\n" +
                "          ],\n" +
                "          \"description\": \"The equip slot where the item is being worn\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"item_name\", \"slot\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String stowe = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"stowe\",\n" +
                "    \"description\": \"Represents the action of a character stowing an item in a container located in the specified slot (e.g., backpack in WEARED_BACK).\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character stowing the item\"\n" +
                "        },\n" +
                "        \"itemName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the item being stowed\"\n" +
                "        },\n" +
                "        \"slot\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"enum\": [\n" +
                "            \"WEARED_SX_HAND\",\n" +
                "            \"WEARED_DX_HAND\",\n" +
                "            \"WEARED_TORSO\",\n" +
                "            \"WEARED_UNDERCOAT\",\n" +
                "            \"WEARED_BOOTS\",\n" +
                "            \"WEARED_HEAD\",\n" +
                "            \"WEARED_BELT\",\n" +
                "            \"WEARED_BACK\",\n" +
                "            \"WEARED_VITA\"\n" +
                "          ],\n" +
                "          \"description\": \"The slot where the container is located\"\n"
                +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"itemName\", \"slot\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String retrieveStowedItem = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"retrieveStowedItem\",\n" +
                "    \"description\": \"Represents the action of a character retrieving an item from one of their available containers.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character retrieving the item\"\n" +
                "        },\n" +
                "        \"itemName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the item being retrieved\"\n" +
                "        },\n" + // Manca una virgola qui
                "        \"slot\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"enum\": [\n" +
                "            \"WEARED_SX_HAND\",\n" +
                "            \"WEARED_DX_HAND\",\n" +
                "            \"WEARED_TORSO\",\n" +
                "            \"WEARED_UNDERCOAT\",\n" +
                "            \"WEARED_BOOTS\",\n" +
                "            \"WEARED_HEAD\",\n" +
                "            \"WEARED_BELT\",\n" +
                "            \"WEARED_BACK\",\n" +
                "            \"WEARED_VITA\"\n" +
                "          ],\n" +
                "          \"description\": \"The slot where the container is located\"\n" +
                "        }\n" + // Chiusura corretta del blocco "slot"
                "      },\n" +
                "      \"required\": [\"recipientName\", \"itemName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String drawWeapon = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"drawWeapon\",\n" +
                "    \"description\": \"Represents the action of a character drawing a weapon.\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character drawing the weapon\"\n" +
                "        },\n" +
                "        \"weaponItemName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the weapon being drawn\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"weaponItemName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String putAway = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"putAway\",\n" +
                "    \"description\": \"Represents the action of a character putting away a weapon.\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character putting away the weapon\"\n" +
                "        },\n" +
                "        \"weaponItemName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the weapon being put away\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"weaponItemName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String openInventory = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"openInventory\",\n" +
                "    \"description\": \"Represents the action of a character opening their inventory to see what they possess.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character opening their inventory\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String drawFrom = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"drawFrom\",\n" +
                "    \"description\": \"Represents the action of a character unequipping or removing an item from a specified slot.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character unequipping the item\"\n" +
                "        },\n" +
                "        \"slotName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"enum\": [\n" +
                "            \"WEARED_SX_HAND\",\n" +
                "            \"WEARED_DX_HAND\",\n" +
                "            \"WEARED_TORSO\",\n" +
                "            \"WEARED_UNDERCOAT\",\n" +
                "            \"WEARED_BOOTS\",\n" +
                "            \"WEARED_HEAD\",\n" +
                "            \"WEARED_BELT\",\n" +
                "            \"WEARED_BACK\",\n" +
                "            \"WEARED_VITA\"\n" +
                "          ],\n" +
                "          \"description\": \"The name of the slot from which the item is being unequipped\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"slotName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String changeGrip = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"changeGrip\",\n" +
                "    \"description\": \"Represents the action of a character changing the grip of their weapon from one hand to two hands and vice versa.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character changing the grip of their weapon\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String swap = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"swap\",\n" +
                "    \"description\": \"Represents the action of a character swapping the weapon they are holding with another specified weapon.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character swapping their weapon\"\n" +
                "        },\n" +
                "        \"weaponToSwapWith\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the weapon the character is swapping with\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipientName\", \"weaponToSwapWith\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String attack = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"attack\",\n" +
                "    \"description\": \"Represents the action of a character attacking another character with a specific weapon.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"senderName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character performing the attack\"\n" +
                "        },\n" +
                "        \"recipientName\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the character being attacked\"\n" +
                "        },\n" +
                "        \"weaponItem\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the weapon used to perform the attack\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"senderName\", \"recipientName\", \"weaponItem\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JSONObject attack_json = new JSONObject(attack);
        tools.put(attack_json);

        JSONObject swap_json = new JSONObject(swap);
        tools.put(swap_json);

        JSONObject changeGrip_json = new JSONObject(changeGrip);
        tools.put(changeGrip_json);

        JSONObject drawFrom_json = new JSONObject(drawFrom);
        tools.put(drawFrom_json);

        JSONObject openInventory_json = new JSONObject(openInventory);
        tools.put(openInventory_json);

        JSONObject putAway_json = new JSONObject(putAway);
        tools.put(putAway_json);

        JSONObject drawWeapon_json = new JSONObject(drawWeapon);
        tools.put(drawWeapon_json);

        JSONObject retrieveStowedItem_json = new JSONObject(retrieveStowedItem);
        tools.put(retrieveStowedItem_json);

        JSONObject stowe_json = new JSONObject(stowe);
        tools.put(stowe_json);

        JSONObject wear_json = new JSONObject(wear);
        tools.put(wear_json);

        JSONObject give_json = new JSONObject(give);
        tools.put(give_json);

        JSONObject take_json = new JSONObject(take);
        tools.put(take_json);

        JSONObject speak_to_json = new JSONObject(speak_to);
        JSONObject changeScene_json = new JSONObject(changeScene);
        JSONObject getEnvironment_json = new JSONObject(getEnvironment);
        JSONObject getKnownScenes_json = new JSONObject(getKnowScenes);

        tools.put(getEnvironment_json);
        tools.put(getKnownScenes_json);
        tools.put(speak_to_json);
        tools.put(changeScene_json);
        data.put("tools", tools);
        data.put("model", "gpt-4o-mini");
        conversation.put(system_instruction_json);

    }

    public JSONObject performAPICall() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("OpenAI-Project", System.getenv("OPENAI_PROJECT_ID"));
        con.setRequestProperty("Authorization",
                "Bearer " + System.getenv("OPENAI_API_KEY"));
        data.put("messages", conversation);
        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
        BufferedReader buff = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        String output = buff.lines().reduce((a, b) -> a + b).get();
        buff.close();
        return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getJSONObject("message");

    }

    public String interact(String name, String message) {
        JSONArray conversation_aux = new JSONArray(conversation.toString());
        try {
            if (message != null) {
                data.put("messages",
                        conversation.put(new JSONObject().put("role", "user").put("content", name + ": " + message)));
            }
            response = performAPICall();
            while (response.has("tool_calls")) {
                JSONArray tool_calls = response.getJSONArray("tool_calls");
                for (int i = 0; i < tool_calls.length(); i++) {
                    JSONObject tool_call = tool_calls.getJSONObject(i);
                    conversation.put(new JSONObject().put("role", "assistant").put("tool_calls",
                            new JSONArray().put(tool_call)));
                    String method_name = tool_call.getJSONObject("function").get("name").toString();
                    JSONObject arguments_json = new JSONObject(
                            tool_call.getJSONObject("function").get("arguments").toString());
                    Class<?> botClazz = CampaignManagerIAOpenAi.class;
                    Method[] botMethods = botClazz.getMethods();
                    for (Method method : botMethods) {

                        if (method.getName().equals(method_name)) {

                            Parameter[] parameters = method.getParameters();
                            Object[] arguments = new Object[parameters.length];

                            for (int j = 0; j < parameters.length; j++) {
                                parameters[j].getType();
                                arguments[j] = parameters[j].getType()
                                        .cast(arguments_json.get(parameters[j].getName()));
                            }

                            Object execution_result = method.invoke(this, arguments);
                            conversation.put(new JSONObject().put("role", "tool")
                                    .put("content", (String) execution_result)
                                    .put("tool_call_id", tool_call.getString("id")));
                        }
                    }
                }
                response = performAPICall();
            }
            conversation.put(response);
            return response.getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            Throwable thrownException = e.getCause();
            conversation = conversation_aux;
            if (thrownException != null) {
                return "Errore: " + thrownException.getMessage();
            } else {
                return "Errore: " + e.getMessage();
            }
        }

    }

    public CampaignManagerIAOpenAi setCampaign_Engine(Campaign_Engine campaign_Engine) {
        this.campaign_Engine = campaign_Engine;
        return this;

    }

    public String speak_to(String senderName, String recipientName, String text) throws Exception {
        return campaign_Engine.speak_to(senderName, recipientName, text);
    }

    public String changeScene(String recipientName, String sceneName) throws Exception {
        return campaign_Engine.changeScene(recipientName, sceneName);
    }

    public String getKnownScenes() {
        return campaign_Engine.getKnownScenes();
    }

    public String getEnvironment(String pcName) throws Exception {
        return campaign_Engine.getEnvironment(pcName);
    }

    public String drawFrom(String recipientName, String slotName) throws Exception {
        return campaign_Engine.drawFrom(recipientName, slotName);
    }

    public String take(String recipientName, String holderName, String itemName) throws Exception {
        return campaign_Engine.take(recipientName, holderName, itemName);
    }

    public String give(String recipientName, String senderName, String itemName) throws Exception {
        return campaign_Engine.give(recipientName, senderName, itemName);

    }

    public String wear(String recipientName, String item_name, String slot) throws Exception {
        return campaign_Engine.wear(recipientName, item_name, slot);
    }

    public String stowe(String recipientName, String itemName, String slot) throws Exception {
        return campaign_Engine.stowe(recipientName, itemName, slot);
    }

    public String retrieveStowedItem(String recipientName, String itemName, String equipSlot) throws Exception {
        return campaign_Engine.retrieveStowedItem(recipientName, itemName, equipSlot);
    }

    public String drawWeapon(String recipientName, String weaponItemName) throws Exception {
        return campaign_Engine.drawWeapon(recipientName, weaponItemName);
    }

    public String putAway(String recipientName, String weaponItemName) throws Exception {
        return campaign_Engine.putAway(recipientName, weaponItemName);
    }

    public String openInventory(String recipientName) throws Exception {
        return campaign_Engine.openInventory(recipientName);
    }

    public String changeGrip(String recipientName) throws Exception {
        return campaign_Engine.changeGrip(recipientName);
    }

    public String swap(String recipientName, String weaponToSwapWith) throws Exception {
        return campaign_Engine.swap(recipientName, weaponToSwapWith);
    }

    public String attack(String senderName, String recipientName, String weaponItem) throws Exception {
        return campaign_Engine.attack(senderName, recipientName, weaponItem);
    }
}
