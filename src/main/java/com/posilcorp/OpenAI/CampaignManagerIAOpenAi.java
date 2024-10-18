package com.posilcorp.OpenAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.posilcorp.CampaignManagerInterface;
import com.posilcorp.Campaign_Engine;
import com.posilcorp.ObjectYouCanSpeakTo;
import com.posilcorp.Scene;
import com.posilcorp.EquipmentLogic.EquipSlot;
import com.posilcorp.EquipmentLogic.Item;
import com.posilcorp.EquipmentLogic.ObjectWithInventory;
import com.posilcorp.Character;
import com.posilcorp.Utilities.Levenshtein;

public class CampaignManagerIAOpenAi implements CampaignManagerInterface {
    private final String url = "https://api.openai.com/v1/chat/completions";
    private final String api_Token = "Bearer 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";

    private JSONArray conversation;
    Campaign_Engine campaign_Engine;
    JSONObject data;
    JSONObject response;
    JSONObject system_instruction_json;

    public CampaignManagerIAOpenAi() {

        data = new JSONObject();
        conversation = new JSONArray();
        String systemMessage = "Sei un Game Master virtuale per un'avventura di ruolo. Il tuo compito è esclusivamente invocare le funzioni definite nel JSON array 'tools' per rispondere alle richieste dei giocatori. Non devi improvvisare o inventare nulla, limitandoti a interpretare l'ambiente e le azioni in base alle funzioni disponibili. Dopo aver ricevuto l'output delle funzioni, dovrai interpretarlo e formattarlo in Markdown per essere utilizzato in una chat Telegram, aggiungendo emoji per rendere l'esperienza più coinvolgente.\n"
                +
                "\n" +
                "Usa le seguenti funzioni per ogni richiesta dei giocatori:\n" +
                "\n" +
                "- **getEnvironment(nome_pg)**: Usa questa funzione per fornire la descrizione dell'ambiente in cui si trova un personaggio specifico (nome_pg). Formatta la risposta in Markdown, ad esempio con *testo in corsivo* o **testo in grassetto**, e includi emoji appropriate come 🌲 o 🏰 a seconda dell'ambiente descritto.\n"
                +
                "- **changeScene(recipient, sceneName)**: Usa questa funzione quando un personaggio vuole cambiare scena. 'recipient' è il nome del personaggio e 'sceneName' è il nome della scena in cui vuole andare. Fornisci un output ben formattato, ad esempio: \"⚔️ **[Nome_PG] si sposta nella scena [Nome_Scena]**\".\n"
                +
                "- **speak_to(sender, recipient, text)**: Usa questa funzione quando un personaggio vuole parlare a un'NPC o parlare al vuoto nella scena. 'sender' è il nome del personaggio, 'recipient' è il nome dell'NPC o della scena e 'text' è il messaggio che il personaggio vuole dire. Rispondi in Markdown, ad esempio: \"🗣️ *[Nome_PG] dice a [Nome_NPC]: '[Testo]'*\".\n"
                +
                "- **getKnowScenes()**: Usa questa funzione per fornire una lista delle scene conosciute nel gioco. Presenta la lista formattata in Markdown con delle emoji, ad esempio: \"🌍 **Scene Disponibili**: 1. 🏞️ Foresta Incantata 2. 🏰 Castello Antico 3. 🏙️ Villaggio Medievale\".\n"
                +
                "\n" +
                "Il tuo unico obiettivo è rispondere alle azioni dei giocatori attraverso queste funzioni. Interpreta e formatta ogni output con Markdown e emoji per adattarlo a una chat Telegram. Non devi aggiungere descrizioni extra o improvvisare. Tutto deve essere strettamente legato alle funzioni disponibili.";

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

        JSONObject speak_to_json = new JSONObject(speak_to);
        JSONObject changeScene_json = new JSONObject(changeScene);
        JSONObject getEnvironment_json = new JSONObject(getEnvironment);
        JSONObject getKnownScenes_json = new JSONObject(getKnowScenes);
        JSONArray tools = new JSONArray();
        tools.put(getEnvironment_json);
        tools.put(getKnownScenes_json);
        tools.put(speak_to_json);
        tools.put(changeScene_json);
        data.put("tools", tools);
        data.put("model", "gpt-4o");
        conversation.put(system_instruction_json);

    }

    public JSONObject performAPICall() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization",
                api_Token);
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

        if (message != null)
            data.put("messages",
                    conversation.put(new JSONObject().put("role", "user").put("content", name + ": " + message)));

        try {
            response = performAPICall();
        } catch (IOException e) {
            e.printStackTrace();
            conversation = conversation_aux;
            return "Qualcosa è andato storto, riprova!";

        }
        JSONArray tool_calls = null;
        try {
            tool_calls = response.getJSONArray("tool_calls");
        } catch (JSONException e) {
        }
        if (tool_calls != null) {
            tool_calls.forEach(item -> {
                try {
                    JSONObject tool_call = (JSONObject) item;
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

                            for (int i = 0; i < parameters.length; i++) {
                                parameters[i].getType();
                                arguments[i] = parameters[i].getType()
                                        .cast(arguments_json.get(parameters[i].getName()));
                            }
                            try {
                                Object execution_result = method.invoke(this, arguments);
                                conversation.put(new JSONObject().put("role", "tool")
                                        .put("content", (String) execution_result)
                                        .put("tool_call_id", tool_call.getString("id")));
                            } catch (Exception e) {
                                e.printStackTrace();
                                conversation.put(new JSONObject().put("role", "tool")
                                        .put("content",
                                                "status:Errore nell'esecuzione della funzione. chiedi di riformulare la domanda.")
                                        .put("tool_call_id", tool_call.getString("id")));
                            }

                            response = performAPICall();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response = new JSONObject()
                            .put("content",
                                    "Errore! non sono riuscito a perfezionare la creazione! " + e.getStackTrace())
                            .put("role", "assistant");
                }
            });
            conversation.put(response);
            return response.getString("content");

        } else {
            conversation.put(response);
            return response.getString("content");
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

    public String drawFrom(String recipientName, String slotName) throws Exception {
        return campaign_Engine.drawFrom(recipientName, slotName);
    }

    public String take(String recipientName, String holderName, String itemName) throws Exception {
        return campaign_Engine.take(recipientName, holderName, itemName);
    }
    

    
    public String getEnvironment(String pcName) throws Exception {
        return campaign_Engine.getEnvironment(pcName);
    }

}
