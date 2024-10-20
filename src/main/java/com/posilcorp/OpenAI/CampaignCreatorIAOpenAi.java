package com.posilcorp.OpenAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONArray;

import org.json.JSONObject;

import com.posilcorp.CampaignCreatorInterface;
import com.posilcorp.Campaign_Engine;
import com.posilcorp.KeyAttribute;

public class CampaignCreatorIAOpenAi implements CampaignCreatorInterface {

    public Campaign_Engine getCampaign_Engine() {
        return campaign_Engine;
    }

    private final String url = "https://api.openai.com/v1/chat/completions";

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

        JSONObject createNpc_json = new JSONObject(createNpc);
        JSONObject createPc_json = new JSONObject(createPc);
        JSONObject createScene_json = new JSONObject(createScene);
        JSONObject setCampaignName_json = new JSONObject(setCampaignName);
        JSONArray tools = new JSONArray();
        tools.put(createNpc_json);
        tools.put(createPc_json);
        tools.put(createScene_json);
        tools.put(setCampaignName_json);
        data.put("tools", tools);
        data.put("model", "gpt-4o-mini");
    }

    public JSONObject performAPICall() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("OpenAI-Project",System.getenv("OPENAI_PROJECT_ID"));
        con.setRequestProperty("Authorization",
        System.getenv("OPENAI_API_KEY"));
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
        campaign_Engine.setCampaignName(campaign_name);
    }

    public void createPc(String name, String physical_description, String scene_name,int hitPoints) throws Exception {
        campaign_Engine.createPc(name, physical_description, scene_name,  hitPoints);
    }

    public void createNpc(String name, String description, String scene_name,int hitPoints) throws Exception {
        campaign_Engine.createNpc(name, description, scene_name,  hitPoints);
    }

    public void createScene(String name, String description) {
        campaign_Engine.createScene(description, name);
    }
    public void createAndAddItemToObject(String item_name, String Object_name, String equip_slot) throws Exception{
        campaign_Engine.createAndAddItemToObject(item_name, Object_name, equip_slot);
    }
}
