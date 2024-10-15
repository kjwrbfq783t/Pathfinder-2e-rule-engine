package com.posilcorp.OpenAI;

import org.json.JSONArray;
import org.json.JSONObject;

import com.posilcorp.NpcIAInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class NpcIAOpenAi implements NpcIAInterface {
    private JSONArray conversation;
    private JSONObject system_instruction;
    private String description;
    private final String api_token = "Bearer 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";


    @Override
    public void load_initialConf(String description, String scene_description) {
        this.description=description;
        this.system_instruction= new JSONObject().put("role", "system").put("content", description
                + ". Attualmente ti trovi nella seguente località: " + scene_description);
        this.conversation=new JSONArray();
        
    }
        
    @Override
    public String speak_to(String name, String text) throws MalformedURLException, IOException {
        String url = "https://api.openai.com/v1/chat/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization",
                "Bearer " + api_token);
        con.setRequestProperty("OpenAI-Project", "proj_CCAWMJmb5LiimSbyUq3vmEeR");
        JSONObject data = new JSONObject();
        data.put("model", "gpt-4o");
        JSONArray messages=conversation;
        messages.put(system_instruction);
        messages.put(new JSONObject().put("role", "user").put("content", name + ": " + text));
        data.put("messages", conversation);
        con.setDoOutput(true);

        con.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
        BufferedReader buff = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        String output = buff.lines().reduce((a, b) -> a + b).get();
        buff.close();
        String response = new JSONObject(output).getJSONArray("choices").getJSONObject(0).getJSONObject("message").toString();
        conversation.put(new JSONObject().put("role", "user").put("content", name + ": " + text));
        conversation.put(new JSONObject().put("role", "assistant").put("content", response));
        return response;
    }

    @Override
    public void updateScene(String scene_description) {
        system_instruction= new JSONObject().put("role", "system").put("content", description
                + ". Attualmente ti trovi nella seguente località: " + scene_description);

    }

}
