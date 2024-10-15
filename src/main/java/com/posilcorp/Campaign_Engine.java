package com.posilcorp;

import java.util.HashMap;

public class Campaign_Engine {
    private String campaign_name;
    private HashMap<String,Pc_character> pcs;
    private HashMap<String,Npc_character> npcs;
    private HashMap<String,Scene> scene_list;
    private Scene actual_scene;
    NpcIAInterface npc_intelligence;

    public Scene getActual_scene() {
        return actual_scene;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public Campaign_Engine(String campaign_name) throws Exception {
        this.pcs = new HashMap<String,Pc_character>();
        this.npcs = new HashMap<String,Npc_character>();
        this.scene_list = new HashMap<String,Scene>();
        this.campaign_name=campaign_name;
        this.npc_intelligence=new NpcIAOpenAi();
        npc_intelligence.initialize("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");
    }

    public String create_Pc(String name, String phisical_description){
        pcs.put("name", new Pc_character(name, phisical_description));
        return "";
    }

    public String create_npcs(String description,Scene scene_is_on){
        npcs.put("name", new Npc_character(description,scene_is_on,npc_intelligence));
        return "";
    }

    public String create_scene(String scene_description,String name){
        scene_list.put("name", new Scene(scene_description,name));
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



}
