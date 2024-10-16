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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.posilcorp.Campaign_Engine;

public class CampaignManagerAIOpenAi {
    private final String url = "https://api.openai.com/v1/chat/completions";
    private final String api_Token = "Bearer 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";

    private JSONArray conversation;
    Campaign_Engine campaign_Engine;
    JSONObject data;
    JSONObject response;
    JSONObject system_instruction_json;

    public CampaignManagerAIOpenAi() {

        data = new JSONObject();
        conversation = new JSONArray();
        String system_instruction = "Sei il Game Master (GM) di una campagna di gioco di ruolo. Hai a disposizione una function che "
                + "ti restituisce una descrizione dell'ambiente in cui si trova l'user che lo richiede (getEnvironment) e una function da invocare se un utente vuole interagire parlando con altri npc (speak_to) nello stesso environment."
                + "orchestra la situazione chiedendo agli utenti cosa vogliono fare. Ogni messaggio contiene il nome dell'utente che vuole interagire";
        system_instruction_json = new JSONObject().put("role", "system").put("content", system_instruction);
        String speak_to = "{\n" +
                "  \"type\": \"function\",\n" +
                "  \"function\": {\n" +
                "    \"name\": \"speak_to\",\n" +
                "    \"description\": \"Send a message from one person to another\",\n" +
                "    \"parameters\": {\n" +
                "      \"type\": \"object\",\n" +
                "      \"properties\": {\n" +
                "        \"sender\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the person sending the message\"\n" +
                "        },\n" +
                "        \"recipient\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the person receiving the message\"\n" +
                "        },\n" +
                "        \"text\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The content of the message\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"sender\", \"recipient\", \"text\"]\n" +
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
                "        \"recipient\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the person for whom the scene is being changed\"\n" +
                "        },\n" +
                "        \"scene_name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the scene to switch to\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"recipient\", \"scene_name\"]\n" +
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
                "        \"name\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"description\": \"The name of the person requesting information about the environment.\"\n"
                +
                "        }\n" +
                "      },\n" +
                "      \"required\": [\"name\"],\n" +
                "      \"additionalProperties\": false\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JSONObject speak_to_json = new JSONObject(speak_to);
        JSONObject changeScene_json = new JSONObject(changeScene);
        JSONObject getEnvironment_json = new JSONObject(getEnvironment);
        JSONArray tools = new JSONArray();
        tools.put(getEnvironment_json);
        tools.put(speak_to_json);
        tools.put(changeScene_json);
        data.put("tools", tools);
        data.put("model", "gpt-4o");

    }

    public JSONObject performAPICall() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization",
                api_Token);
        JSONArray messages = conversation;
        messages.put(system_instruction_json);
        data.put("messages", messages);
        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
        BufferedReader buff = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        String output = buff.lines().reduce((a, b) -> a + b).get();
        buff.close();
        return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getJSONObject("message");

    }

    public String interact(String name,String message) {
        JSONArray conversation_aux = conversation;

        if (message != null)
            data.put("messages", conversation.put(new JSONObject().put("role", "user").put("content", name+": "+message)));

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
                            try {
                                method.invoke(this, arguments);
                                conversation.put(new JSONObject().put("role", "tool")
                                        .put("content", "status: ok")
                                        .put("tool_call_id", tool_call.getString("id")));
                            } catch (Exception e) {
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
                    response = new JSONObject().put("content", "Errore! non sono riuscito a perfezionare la creazione! "+e.getStackTrace())
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

    public void setCampaign_Engine(Campaign_Engine campaign_Engine) {
        this.campaign_Engine = campaign_Engine;

    }

    public String speak_to(String sender, String recipient, String text) throws Exception {
        return campaign_Engine.getNpcs().get(sender).speak_to(recipient, text);
    }

    public String changeScene(String recipient, String scene_name) {
        campaign_Engine.getNpcs().get(recipient).changeScene(campaign_Engine.getScene_list().get(scene_name));
        return "";
    }

    public String getEnvironment(String name) {
        String scene_description=campaign_Engine.getPcs().get(name).getScene_is_on().getDescription();
        String scene_name=campaign_Engine.getPcs().get(name).getScene_is_on().getName();
        String nearbyNPCs="";
        ArrayList<String> nearbyNpcs=campaign_Engine.getNearbyNpcs(campaign_Engine.getNpcs().get(name).getScene_is_on().getName());
        for(String npc_name:nearbyNpcs){
            nearbyNPCs=nearbyNPCs+", "+npc_name;
        }
        return "Scena: "+scene_name+"\n descrizione scena: \n"+scene_description+"\n npc vicini: \n"+nearbyNPCs;
    }

}
