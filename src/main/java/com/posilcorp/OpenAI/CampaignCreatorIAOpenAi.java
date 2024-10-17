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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.posilcorp.CampaignCreatorIAInterface;
import com.posilcorp.Campaign_Engine;

public class CampaignCreatorIAOpenAi implements CampaignCreatorIAInterface {

    public Campaign_Engine getCampaign_Engine() {
        return campaign_Engine;
    }

    private final String url = "https://api.openai.com/v1/chat/completions";
    private final String api_Token = "Bearer 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";

    private JSONArray conversation;
    Campaign_Engine campaign_Engine;
    JSONObject data;
    JSONObject response;

    public void setCampaignEngine(Campaign_Engine campaign_Engine) {
        this.campaign_Engine = campaign_Engine;
    }

    public CampaignCreatorIAOpenAi() {
        data = new JSONObject();
        conversation = new JSONArray();
        String system_instructions = "Sei un assistente esperto nella creazione di campagne per giochi di ruolo (roleplay). Il tuo compito è guidare gli utenti nella creazione di una campagna di gioco raccogliendo i seguenti dati:\n"
                +
                "  - Nome della campagna.\n" +
                "  - Lista delle scene, dove ogni scena ha un nome e una descrizione.\n" +
                "  - Lista dei personaggi giocanti, dove ogni personaggio ha un nome e una descrizione fisica.\n" +
                "  - Lista dei personaggi non giocanti, dove ogni personaggio ha una descrizione e deve essere associato alla scena in cui è presente.\n"
                + "- Items da aggiungere agli elementi di gioco\n"
                + "Per ogni dato raccolto, dovrai invocare una specifica funzione per memorizzarlo. Le funzioni sono le seguenti e dovrai chiamarle una alla volta, nell'ordine necessario:\n\n"
                +
                "1. setCampaignName(String campaign_name) per impostare il nome della campagna.\n" +
                "2. createScene(String name, String description) per creare una scena con nome e descrizione.\n" +
                "3. createPc(String name, String phisical_description,String scene_name) per creare un personaggio giocante. Se non viene fornito alcun nome, il campo 'name' va riempito con il nome dell'utente che ha inviato il messaggio.\n"
                +
                "4. createNpc(String description, String scene_name) per creare un personaggio non giocante, specificando la sua descrizione e la scena in cui appare.\n"
                + "5. createAndAddItem(String item_name,String item_description,String element_name,String element_type) crea un item e aggiungilo all'elemento di gioco.\n\n"
                + "Guiderai l'utente passo per passo nel fornire queste informazioni,oppure invocherai le funzioni tutte in una volta, assicurandoti che ogni funzione sia invocata correttamente per costruire la campagna. Al termine della creazione, inviterai l'utente a terminare la creazione. Utilizza emoji per una chat telegram"
                + "Non inserire eventuali oggetti nella descrizione. aggiungili successivamente invocando la funzione createAndAddItem";
        conversation.put(new JSONObject().put("role", "system").put("content", system_instructions));

        String setCampaignName = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"setCampaignName\",\n" +
                "    \"description\": \"Imposta il nome della campagna.\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"campaign_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome della campagna.\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"campaign_name\"],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String createScene = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"createScene\",\n" +
                "    \"description\": \"Crea una scena con nome e descrizione.\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome della scena.\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"La descrizione della scena.\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"name\", \"description\"],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String createPc = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"createPc\",\n" +
                "    \"description\": \"Crea un personaggio giocante specificando  descrizione fisica e scena iniziale.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome dell'user che ha invocato la creazione.\"\n" +
                "        },\n" +
                "        \"physical_description\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Descrizione fisica del personaggio.\"\n" +
                "        },\n" +
                "        \"scene_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome della scena in cui il personaggio si trova.\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"name\", \"physical_description\", \"scene_name\"],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String createNpc = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"createNpc\",\n" +
                "    \"description\": \"Crea un personaggio non giocante con nome, descrizione e scena in cui vuole iniziare.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome del personaggio non giocante.\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Descrizione del personaggio non giocante.\"\n" +
                "        },\n" +
                "        \"scene_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Nome della scena in cui appare.\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"name\", \"description\", \"scene_name\"],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String createAndAddItem = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"createAndAddItem\",\n" +
                "    \"description\": \"Crea un oggetto e lo assegna a un elemento di gioco specifico, come un personaggio giocante, un personaggio non giocante o una scena di gioco.\",\n"
                +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"item_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome dell'oggetto da creare.\"\n" +
                "        },\n" +
                "        \"item_description\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Una descrizione dettagliata dell'oggetto.\"\n" +
                "        },\n" +
                "        \"element_type\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"enum\": [\"Pc_character\", \"Npc_character\", \"Scene\"],\n" +
                "          \"description\": \"Il tipo di elemento di gioco a cui assegnare l'oggetto. I valori possibili sono: 'Pc_character' per i personaggi giocanti, 'Npc_character' per i personaggi non giocanti e 'Scene' per le scene di gioco.\"\n"
                +
                "        },\n" +
                "        \"element_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome dell'elemento di gioco (personaggio o scena) a cui viene aggiunto l'oggetto.\"\n"
                +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"item_name\", \"item_description\", \"element_type\", \"element_name\"]\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JSONObject createNpc_json = new JSONObject(createNpc);
        JSONObject createPc_json = new JSONObject(createPc);
        JSONObject createScene_json = new JSONObject(createScene);
        JSONObject setCampaignName_json = new JSONObject(setCampaignName);
        JSONObject createAndAddItem_json = new JSONObject(createAndAddItem);
        JSONArray tools = new JSONArray();
        tools.put(createNpc_json);
        tools.put(createPc_json);
        tools.put(createScene_json);
        tools.put(setCampaignName_json);
        tools.put(createAndAddItem_json);
        data.put("tools", tools);
        data.put("model", "gpt-4o");
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
                    Class<?> botClazz = CampaignCreatorIAOpenAi.class;
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

                            method.invoke(this, arguments);
                            conversation.put(new JSONObject().put("role", "tool")
                                    .put("content", "status: ok")
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
            conversation = conversation_aux;
            return "Errore di sistema: " + e.getMessage();
        }

    }

    public void setCampaignName(String campaign_name) {
        campaign_Engine.setCampaign_name(campaign_name);
    }

    public void createPc(String name, String physical_description, String scene_name) throws Exception {
        campaign_Engine.create_Pc(name, physical_description, scene_name);
    }

    public void createNpc(String name, String description, String scene_name) throws Exception {
        campaign_Engine.create_npc(name, description, scene_name);
    }

    public void createScene(String name, String description) {
        campaign_Engine.create_scene(description, name);
    }

    public void createAndAddItem(String item_name, String item_description, String element_name, String element_type)
            throws Exception {
        Set<String> names;
        Integer best_score = null;
        String matched_element_type = null;
        for (String fetched_name : campaign_Engine.getElementTypes()) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(element_type, fetched_name);
                matched_element_type = fetched_name;
            } else if (LevenshteinDistance.getDefaultInstance().apply(element_type, fetched_name) < best_score) {
                matched_element_type= fetched_name;
            }
        }
        names=campaign_Engine.getKeys(matched_element_type);
        best_score = null;
        String matched_element_name = null;
        for (String fetched_name : names) {
            if (best_score == null) {
                best_score = LevenshteinDistance.getDefaultInstance().apply(element_name, fetched_name);
                matched_element_name = fetched_name;
            } else if (LevenshteinDistance.getDefaultInstance().apply(element_name, fetched_name) < best_score) {
                matched_element_name = fetched_name;
            }
        }

        campaign_Engine.createAndAddItem(item_name, item_description, matched_element_name, matched_element_type);
    }

}
