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
import org.json.JSONException;
import org.json.JSONObject;

import com.posilcorp.CampaignCreatorIAInterface;
import com.posilcorp.Campaign_Engine;

public class CampaignCreatorIAOpenAi implements CampaignCreatorIAInterface{
    private final String url = "https://api.openai.com/v1/chat/completions";
    private final String api_Token = "Bearer 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";

    private JSONArray conversation;
    Campaign_Engine campaign_Engine;
    JSONObject data;
    JSONObject response;

    public void setCampaignEngine(Campaign_Engine campaign_Engine) {
        this.campaign_Engine = campaign_Engine;
    }

    public CampaignCreatorIAOpenAi() throws Exception {
        data = new JSONObject();
        conversation = new JSONArray();
        String system_instructions = "Sei un assistente esperto nella creazione di campagne per giochi di ruolo (roleplay). Il tuo compito è guidare l'utente nella creazione di una campagna di gioco raccogliendo i seguenti dati:\n"
                +
                "  - Nome della campagna.\n" +
                "  - Lista delle scene, dove ogni scena ha un nome e una descrizione.\n" +
                "  - Lista dei personaggi giocanti, dove ogni personaggio ha un nome e una descrizione fisica.\n" +
                "  - Lista dei personaggi non giocanti, dove ogni personaggio ha una descrizione e deve essere associato alla scena in cui è presente.\n\n"
                +
                "Per ogni dato raccolto, dovrai invocare una specifica funzione per memorizzarlo. Le funzioni sono le seguenti e dovrai chiamarle una alla volta, nell'ordine necessario:\n\n"
                +
                "1. setCampaignName(String campaign_name) per impostare il nome della campagna.\n" +
                "2. createScene(String name, String description) per creare una scena con nome e descrizione.\n" +
                "3. createPc(String name, String phisical_description) per creare un personaggio giocante con nome e descrizione fisica.\n"
                +
                "4. createNpc(String description, String scene_name) per creare un personaggio non giocante, specificando la sua descrizione e la scena in cui appare.\n\n"
                +
                "Guiderai l'utente passo per passo nel fornire queste informazioni, assicurandoti che ogni funzione sia invocata correttamente per costruire la campagna. Al termine della creazione, inviterai l'utente a premere il bottone 'TERMINA CREAZIONE CAMPAGNA'";
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
                "    \"description\": \"Crea un personaggio giocante con nome e descrizione fisica.\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Il nome del personaggio giocante.\"\n" +
                "        },\n" +
                "        \"physical_description\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Descrizione fisica del personaggio.\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"name\", \"physical_description\"],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String createNpc = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"createNpc\",\n" +
                "    \"description\": \"Crea un personaggio non giocante con descrizione e scena associata.\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"description\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Descrizione del personaggio non giocante.\"\n" +
                "        },\n" +
                "        \"scene_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"Nome della scena in cui appare.\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"description\", \"scene_name\"],\n" +
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

    public String interact(String message) {
        JSONArray conversation_aux = conversation;

        if(message!=null) data.put("messages", conversation.put(new JSONObject().put("role", "user").put("content", message)));

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
                    Class<?> botClazz = CampaignCreatorIAOpenAi.class;
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
                            try{
                                method.invoke(this, arguments);
                                conversation.put(new JSONObject().put("role", "tool")
                                    .put("content", "status: ok")
                                    .put("tool_call_id", tool_call.getString("id")));
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                                conversation.put(new JSONObject().put("role", "tool")
                                    .put("content", "status: Errore, riprovare!")
                                    .put("tool_call_id", tool_call.getString("id")));
                            }
                            
                            
                            response = performAPICall();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response=new JSONObject().put("content", "non sono riuscito a perfezionare la creazione!").put("role", "assistant");
                }
            });
            conversation.put(response);
            return response.getString("content");

        } else {
            conversation.put(response);
            return response.getString("content");
        }
    }

    public void setCampaignName(String campaign_name) {
        campaign_Engine.setCampaign_name(campaign_name);
    }

    public void createPc(String name, String physical_description) {
        campaign_Engine.create_Pc(name, physical_description);
    }

    public void createNpc(String description, String scene_name) throws Exception {
        campaign_Engine.create_npc(description, scene_name);
    }

    public void createScene(String name, String description) {
        campaign_Engine.create_scene(description, name);
    }

    public void selectActualScene(String scene_name) {
        campaign_Engine.select_actual_scene(scene_name);
    }

}
