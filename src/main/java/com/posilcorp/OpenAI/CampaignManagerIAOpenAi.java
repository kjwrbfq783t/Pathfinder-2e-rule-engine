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

import com.posilcorp.Campaign_Engine;
import com.posilcorp.Utilities.FunctionEmbeddingMatcher;

public class CampaignManagerIAOpenAi {
    private final String url = "https://api.openai.com/v1/chat/completions";

    Campaign_Engine campaign_Engine;
    JSONObject data;
    JSONObject response;
    String system_instruction = "ogni messaggio è diviso in 2 parti separate da '|'. la prima parte è la richiesta di un utente in un gioco di ruolo."+
                                " La seconda è la segnatura della funzione. Popola gli argomenti";
    private final String responseFormat = "{\n" +
            "        \"type\": \"json_schema\",\n" +
            "        \"json_schema\": {\n" +
            "            \"name\": \"functionCall\",\n" +
            "            \"schema\": {\n" +
            "                \"type\": \"object\",\n" +
            "                \"properties\": {\n" +
                                  "  \"functionName\":{\"type\":\"string\",\"description\":\"name of the function\"},"+
            "                    \"argumentValues\": {\n" +
            "                        \"type\": \"array\",\n" +
            "                        \"items\": {\n" +
            "                            \"type\":\"object\",\n" +
            "                            \"properties\":{\n" +
            "                                \"parameterName\":{\"type\":\"string\"},\n" +
            "                                \"parameterValue\":{\"type\":\"string\"}\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }"+
            "                },\n" +
            "                \"required\": [\n" +
            "                    \"argumentValues\",\n" +
            "                    \"isValid\"\n" +
            "                ],\n" +
            "                \"additionalProperties\": false\n" +
            "            }\n" +
            "        }\n" +
            "}";

    public double[] getEmbedding(String text) throws Exception {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.openai.com/v1/embeddings")
                    .openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("OpenAI-Project", System.getenv("OPENAI_PROJECT_ID"));
            con.setRequestProperty("Authorization",
                    "Bearer " + System.getenv("OPENAI_API_KEY"));
            JSONObject data = new JSONObject();
            data.put("model", "text-embedding-3-large");
            data.put("input", text);
            con.setDoOutput(true);
            con.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
            BufferedReader buff = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

            String output = buff.lines().reduce((a, b) -> a + b).get();
            buff.close();
            JSONArray jsonDoubleArray = new JSONObject(output).getJSONArray("data").getJSONObject(0)
                    .getJSONArray("embedding");
            double[] embedding = new double[jsonDoubleArray.length()];
            for (int i = 0; i < jsonDoubleArray.length(); i++) {
                embedding[i] = jsonDoubleArray.getDouble(i);
            }
            return embedding;

        } catch (Exception e) {
            throw new Exception("errore nell'ottenere l'embedding: " + e.getMessage());
        }
    }

    public JSONObject performAPICall(String text) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("OpenAI-Project", System.getenv("OPENAI_PROJECT_ID"));
        con.setRequestProperty("Authorization",
                "Bearer " + System.getenv("OPENAI_API_KEY"));
        JSONArray conversation = new JSONArray();
        conversation.put(new JSONObject().put("role", "system").put("content", system_instruction));
        conversation.put(new JSONObject().put("role", "user").put("content", text));
        JSONObject data = new JSONObject();
        data.put("model", "gpt-4o-mini");
        data.put("response_format", new JSONObject(responseFormat));
        data.put("messages",conversation);

        con.setDoOutput(true);
        con.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
        BufferedReader buff = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        String output = buff.lines().reduce((a, b) -> a + b).get();
        buff.close();
        String functionCallJsonString=new JSONObject(output).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        JSONObject functionCallJson=new JSONObject(functionCallJsonString);
        return functionCallJson;
    }

    public String interact(String name, String message) {
        return "peto";

        // try {
        //     double[] embedding = getEmbedding(message);
        //     FunctionEmbeddingMatcher.FunctionData functionData = FunctionEmbeddingMatcher.fetchFunction(embedding);
        //     JSONObject functionCall=performAPICall(name+": "+message+"| "+functionData.functionName+functionData.functionArgs);
        //     return functionCall.toString();
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     Throwable thrownException = e.getCause();
        //     if (thrownException != null) {
        //         return "Errore: " + thrownException.getMessage();
        //     } else {
        //         return "Errore: " + e.getMessage();
        //     }
        // }
        /*
         * JSONArray conversation_aux = new JSONArray(conversation.toString());
         * try {
         * if (message != null) {
         * data.put("messages",
         * conversation.put(new JSONObject().put("role", "user").put("content", name +
         * ": " + message)));
         * }
         * response = performAPICall();
         * while (response.has("tool_calls")) {
         * JSONArray tool_calls = response.getJSONArray("tool_calls");
         * for (int i = 0; i < tool_calls.length(); i++) {
         * JSONObject tool_call = tool_calls.getJSONObject(i);
         * conversation.put(new JSONObject().put("role", "assistant").put("tool_calls",
         * new JSONArray().put(tool_call)));
         * String method_name =
         * tool_call.getJSONObject("function").get("name").toString();
         * JSONObject arguments_json = new JSONObject(
         * tool_call.getJSONObject("function").get("arguments").toString());
         * Class<?> botClazz = CampaignManagerIAOpenAi.class;
         * Method[] botMethods = botClazz.getMethods();
         * for (Method method : botMethods) {
         * 
         * if (method.getName().equals(method_name)) {
         * 
         * Parameter[] parameters = method.getParameters();
         * Object[] arguments = new Object[parameters.length];
         * 
         * for (int j = 0; j < parameters.length; j++) {
         * parameters[j].getType();
         * arguments[j] = parameters[j].getType()
         * .cast(arguments_json.get(parameters[j].getName()));
         * }
         * 
         * Object execution_result = method.invoke(this, arguments);
         * conversation.put(new JSONObject().put("role", "tool")
         * .put("content", (String) execution_result)
         * .put("tool_call_id", tool_call.getString("id")));
         * }
         * }
         * }
         * response = performAPICall();
         * }
         * conversation.put(response);
         * return response.getString("content");
         * 
         * } catch (Exception e) {
         * e.printStackTrace();
         * Throwable thrownException = e.getCause();
         * conversation = conversation_aux;
         * if (thrownException != null) {
         * return "Errore: " + thrownException.getMessage();
         * } else {
         * return "Errore: " + e.getMessage();
         * }
         * }
         */
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
