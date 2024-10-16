package com.posilcorp;

import java.util.ArrayList;
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

    public HashMap<String, Npc_character> getNpcs() {
        return npcs;
    }

    public HashMap<String, Pc_character> getPcs() {
        return pcs;
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

    public String create_Pc(String name, String phisical_description,String scene_name){
        Pc_character pc=new Pc_character(scene_name, phisical_description, this.getScene_list().get(scene_name));
        this.getScene_list().get(scene_name).addPc(pc);
        pcs.put(pc.getName(),pc);
        return "";
    }

    public String create_npc(String name,String description,String scene_name){
        Npc_character npc=new Npc_character(name,description,this.getScene_list().get(scene_name),npc_intelligence);
        this.getScene_list().get(scene_name).addNpc(npc);
        npcs.put(npc.getName(),npc);
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

    public String pc_changeScene(String pc_name,String scene_name){
        pcs.get(pc_name).changeScene(scene_list.get(scene_name));
        return "";
    }
    public ArrayList<String> getNearbyNpcs(String scene_name){
        ArrayList<String> nearbyNpcs=new ArrayList<String>();
        getScene_list().get(scene_name).getNpc_characters().forEach(item->{
            Npc_character npc=(Npc_character) item;
            nearbyNpcs.add(npc.getName());
        });
        return nearbyNpcs;
    }





}
