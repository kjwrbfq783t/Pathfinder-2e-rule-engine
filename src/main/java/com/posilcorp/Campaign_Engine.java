package com.posilcorp;

import java.util.HashMap;

import com.posilcorp.OpenAI.NpcIAOpenAi;

public class Campaign_Engine {
    private String campaign_name;
    private HashMap<String,Pc_character> pcs;
    private HashMap<String,Npc_character> npcs;
    private HashMap<String,Scene> scene_list;
    private Scene actual_scene;
    private NpcIAInterface npc_intelligence;

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    public Scene getActual_scene() {
        return actual_scene;
    }

    public HashMap<String, Scene> getScene_list() {
        return scene_list;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public Campaign_Engine() throws Exception {
        this.pcs = new HashMap<String,Pc_character>();
        this.npcs = new HashMap<String,Npc_character>();
        this.scene_list = new HashMap<String,Scene>();
        this.npc_intelligence=new NpcIAOpenAi();
    }

    public String create_Pc(String name, String phisical_description){
        pcs.put(name, new Pc_character(name, phisical_description));
        return "";
    }

    public String create_npc(String description,String scene_name){
        npcs.put(scene_name, new Npc_character(description,this.getScene_list().get(scene_name),npc_intelligence));
        return "";
    }

    public String create_scene(String scene_description,String name){
        scene_list.put(name, new Scene(scene_description,name));
        return "";
    }

    public String select_actual_scene(String scene_name){
        actual_scene=scene_list.get(scene_name);
        return "";
    }

    public String speak(String sender,String recipient,String message){
        String response=npcs.get(recipient).speak_to(sender, message);
        return response;
    }

    public String npc_changeScene(String npc_name,String scene_name){
        npcs.get(npc_name).changeScene(scene_list.get(scene_name));
        return "";
    }



}
