package com.posilcorp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.posilcorp.OpenAI.NpcIAOpenAi;

public class Campaign_Engine {
    private String campaign_name;
    private HashMap<String, Pc_character> pcs;
    private HashMap<String, Npc_character> npcs;
    private HashMap<String, Scene> scenes;

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    @SuppressWarnings("unchecked")
    public void createAndAddItem(String item_name, String item_descritption, String element_name, String element_type)
            throws Exception {
        Item item = new Item(item_name, item_descritption);
        Method[] methods = Campaign_Engine.class.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("get" + element_type + "s")) {
                HashMap<String, InventoryableInterface> obj = (HashMap<String, InventoryableInterface>) method
                        .invoke(this);
                obj.get(element_name).putInInventory(item);
            }
        }
    }

    public HashMap<String, Scene> getScenes() {
        return scenes;
    }

    public HashMap<String, Npc_character> getNpc_characthers() {
        return npcs;
    }

    public HashMap<String, Pc_character> getPc_characthers() {
        return pcs;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public Campaign_Engine() {
        this.pcs = new HashMap<String, Pc_character>();
        this.npcs = new HashMap<String, Npc_character>();
        this.scenes = new HashMap<String, Scene>();
    }

    public String create_Pc(String name, String phisical_description, String scene_name) {
        Pc_character pc = new Pc_character(name, phisical_description, this.getScenes().get(scene_name));
        this.getScenes().get(scene_name).addPc(pc);
        pcs.put(pc.getName(), pc);
        return "";
    }

    public String create_npc(String name, String description, String scene_name) {
        NpcIAInterface npc_intelligence = new NpcIAOpenAi();
        Npc_character npc = new Npc_character(name, description, this.getScenes().get(scene_name), npc_intelligence);
        this.getScenes().get(scene_name).addNpc(npc);
        npcs.put(npc.getName(), npc);
        return "";
    }

    public String create_scene(String scene_description, String name) {
        scenes.put(name, new Scene(scene_description, name));
        return "";
    }

    public String speak(String sender, String recipient, String message) {
        String response = npcs.get(recipient).speak_to(sender, message);
        return response;
    }

    public String npc_changeScene(String npc_name, String scene_name) {
        npcs.get(npc_name).changeScene(scenes.get(scene_name));
        return "";
    }

    public String pc_changeScene(String pc_name, String scene_name) {
        pcs.get(pc_name).changeScene(scenes.get(scene_name));
        scenes.get(scene_name).addPc(pcs.get(pc_name));
        return "";
    }

    // Da modificare, deve ritornare un'array di npc_characters
    public ArrayList<String> getNearbyNpcs(String scene_name) {
        ArrayList<String> nearbyNpcs = new ArrayList<String>();
        getScenes().get(scene_name).getNpc_characters().forEach(item -> {
            Npc_character npc = (Npc_character) item;
            nearbyNpcs.add(npc.getName());
        });
        return nearbyNpcs;
    }

    public String[] getElementTypes() {
        String[] game_elements_strings = new String[GameElement.values().length];
        for (int i = 0; i < GameElement.values().length; i++) {
            game_elements_strings[i] = GameElement.values()[i].toString();
        }
        return game_elements_strings;

    }

    @SuppressWarnings("unchecked")
    public Set<String> getKeys(String gameElement)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method[] methods = Campaign_Engine.class.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("get" + gameElement + "s")) {

                HashMap<String, Object> obj = (HashMap<String, Object>) method.invoke(this);
                return obj.keySet();
            }
        }
        return null;

    }

}
