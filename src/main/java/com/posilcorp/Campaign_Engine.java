package com.posilcorp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.posilcorp.EquipmentLogic.EquipSlot;
import com.posilcorp.EquipmentLogic.Inventory;
import com.posilcorp.EquipmentLogic.Item;
import com.posilcorp.EquipmentLogic.ItemBuilder;
import com.posilcorp.EquipmentLogic.ObjectWithInventory;
import com.posilcorp.Utilities.Levenshtein;

public class Campaign_Engine {
    private String campaign_name;
    private HashMap<String, Character> characters;
    private HashMap<String, Scene> scenes;

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    public void createAndAddItemToObject(String item_name, String Object_name, String equip_slot) throws Exception {

        HashMap<String, ObjectWithInventory> objectsWithInventory = new HashMap<String, ObjectWithInventory>();
        for (Map.Entry<String, Scene> entry : scenes.entrySet()) {
            objectsWithInventory.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Character> entry : characters.entrySet()) {
            objectsWithInventory.put(entry.getKey(), entry.getValue());
        }
        if (objectsWithInventory.get(Object_name) instanceof Scene) {
            objectsWithInventory.get(Object_name).getInventory().set(ItemBuilder.getIstanceof(item_name));
        } else {
            objectsWithInventory.get(Object_name).getInventory().set(ItemBuilder.getIstanceof(item_name),
                    EquipSlot.valueOf(equip_slot));
        }
    }

    public HashMap<String, ObjectYouCanSpeakTo> getObjectYouCanSpeakTos() {
        HashMap<String, ObjectYouCanSpeakTo> ObjectYouCanSpeakTos = new HashMap<String, ObjectYouCanSpeakTo>();
        for (Map.Entry<String, Scene> entry : scenes.entrySet()) {
            ObjectYouCanSpeakTos.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Character> entry : characters.entrySet()) {
            ObjectYouCanSpeakTos.put(entry.getKey(), entry.getValue());
        }
        return ObjectYouCanSpeakTos;
    }

    public HashMap<String, ObjectWithInventory> getObjectWithInventorys() {
        HashMap<String, ObjectWithInventory> ObjectWithInventorys = new HashMap<String, ObjectWithInventory>();
        for (Map.Entry<String, Scene> entry : scenes.entrySet()) {
            ObjectWithInventorys.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Character> entry : characters.entrySet()) {
            ObjectWithInventorys.put(entry.getKey(), entry.getValue());
        }
        return ObjectWithInventorys;
    }

    public HashMap<String, Character> getCharacters() {
        return characters;
    }

    public HashMap<String, Scene> getScenes() {
        return scenes;
    }

    public Collection<Character> getNpc_characthers() {
        ArrayList<Character> npcs = new ArrayList<Character>();
        for (Character character : characters.values()) {
            if (character.isNPC) {
                npcs.add(character);
            }
        }
        return npcs;
    }

    public Collection<Character> getPc_characthers() {
        ArrayList<Character> pcs = new ArrayList<Character>();
        for (Character character : characters.values()) {
            if (!character.isNPC) {
                pcs.add(character);
            }
        }
        return pcs;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    // al costruttore bisogna dare un oggetto classe di tipo NpcAIInterface in modo
    // che poi ne istanzierà la classe.
    public Campaign_Engine() {
        this.characters = new HashMap<String, Character>();
        this.scenes = new HashMap<String, Scene>();
    }

    public void create_Pc(String name, String phisical_description, String scene_name) {
        Character pc = new Pc_character(name, phisical_description, this.getScenes().get(scene_name));
        this.getScenes().get(scene_name).addCharacter(pc);
        characters.put(name, pc);
    }

    public void create_npc(String name, String description, String scene_name) {
        Character npc = new Npc_character(name, description, this.getScenes().get(scene_name));
        this.getScenes().get(scene_name).addCharacter(npc);
        characters.put(npc.getName(), npc);
    }

    public String create_scene(String scene_description, String name) {
        scenes.put(name, new Scene(scene_description, name));
        return "";
    }

    public String speak_to(String senderName, String recipientName, String text) throws Exception {
        ObjectYouCanSpeakTo matched_sender = Levenshtein.fetchObjectYouCanSpeakTo(senderName,
                this.getObjectYouCanSpeakTos());
        ObjectYouCanSpeakTo matched_recipient = Levenshtein.fetchObjectYouCanSpeakTo(recipientName,
                this.getObjectYouCanSpeakTos());
        String response = matched_recipient.speak_to(matched_sender, text);
        return "{\"name\": " + matched_recipient.getName() + ", \"text\":" + response + "}";
    }

    public String changeScene(String recipientName, String sceneName) throws Exception {
        Scene matched_scene = Levenshtein.fetchScenes(sceneName, this.getScenes());
        Character matched_recipient = Levenshtein.fetchCharacter(recipientName, this.getCharacters());
        matched_recipient.changeScene(matched_scene);

        return matched_recipient.getName() + " ha cambiato scena. Ecco la descrizione del nuovo ambiente: "
                + getEnvironment(matched_scene.getName());
    }

    public String getKnownScenes() {
        String scene_List = "";
        for (String sceneName : this.getScenes().keySet()) {
            String scene_description = this.getScenes().get(sceneName).getDescription();
            scene_List = scene_List + "{sceneName: " + sceneName + ", scene_description: "
                    + scene_description + "}";
        }
        return scene_List;
    }

    public String drawFrom(String recipientName, String slotName) throws Exception {
        EquipSlot slot = EquipSlot.valueOf(slotName);
        Character character = Levenshtein.fetchCharacter(recipientName, this.getCharacters());
        Item drawedItem = character.getInventory().drawFrom(slot);
        return "recipientName ha estratto " + drawedItem.getName() + "dallo slot " + slot.toString() + " con successo";
    }

    public String take(String recipientName, String holderName, String itemName) throws Exception {
        Character matchedCharacter = Levenshtein.fetchCharacter(recipientName, this.getCharacters());
        ObjectWithInventory holder = Levenshtein.fetchObjectWithInventory(holderName,
                this.getObjectWithInventorys());
        Item item = Levenshtein.fetchItem(itemName, holder.getInventory().getItems_without_slot());
        if (matchedCharacter.getScene_is_on().equals(holder.getScene_is_on())) {
            matchedCharacter.getInventory().take(holder.getInventory().get(itemName));
        }
        return matchedCharacter.getName() + "ha preso da " + holder.getName() + " l'oggetto " + item.getName();
    }

    public String getEnvironment(String pcName) throws Exception {
        Character character = Levenshtein.fetchCharacter(pcName, this.getCharacters());
        String scene_description = character.getScene_is_on().getDescription();
        String sceneName = character.getScene_is_on().getName();
        String nearbyNPCs = "";
        for (Character npc : character.getScene_is_on().getNpc_characters()) {
            nearbyNPCs = nearbyNPCs + npc.getName() + ", ";
        }
        return "Scena: " + sceneName + "\n descrizione scena: \n" + scene_description + "\n npc vicini: \n"
                + nearbyNPCs;
    }

    public String give(String recipientName, String senderName, String itemName) throws Exception {
        Character matchedRecipient = Levenshtein.fetchCharacter(recipientName, this.getCharacters());
        Character matchedSender = Levenshtein.fetchCharacter(senderName, this.getCharacters());
        if (!matchedRecipient.getScene_is_on().equals(matchedSender.getScene_is_on())) {

            throw new Exception("Non puoi dare qualcosa a qualcuno che non si trova vicino a te...");
        } else {
            Item item = matchedSender.getInventory().give(itemName);
            matchedRecipient.getInventory().take(item);
            return matchedRecipient.getName() + "ha dato a " + matchedSender.getName() + " l'oggetto " + item.getName();
        }

    }

    public String wear(String recipientName, String item_name, String slotString) throws Exception {
        EquipSlot slot=EquipSlot.valueOf(slotString);
        Character matchedCharacter = Levenshtein.fetchCharacter(recipientName, characters);
        Item matchedItem = Levenshtein.fetchItem(item_name, matchedCharacter.getInventory().getItemsOnHands());
        matchedCharacter.getInventory().wear(matchedItem, slot);
        return matchedCharacter.getName() + "ha utilizzato l'azione wear per equipaggiare o mettere a posto l'oggetto "
                + matchedItem.getName();
    }

    public String stowe(String recipientName, String itemName, String slotString) throws Exception {
        Character matchedCharacter = Levenshtein.fetchCharacter(recipientName, characters);
        Item matchedItem = Levenshtein.fetchItem(itemName, matchedCharacter.getInventory().getItemsOnHands());
        EquipSlot equipSlot = EquipSlot.valueOf(slotString);
        matchedCharacter.getInventory().stowe(matchedItem, equipSlot);
        return matchedCharacter.getName() + "ha eseguito l'azione di stowe dell'oggetto" + matchedItem.getName() +
                " nel container che ha indossato in " + equipSlot.toString();
    }

    public String retrieveStowedItem(String recipient, String itemName) throws Exception {
        Character matchedCharacter = Levenshtein.fetchCharacter(itemName, characters);
        Item item = matchedCharacter.getInventory().retrieveStowedItem(itemName);
        return matchedCharacter.getName() + " ha recuperato l'oggetto " + item.getName() + " dal suoi container";
    }

    public String drawWeapon(String recipientName,String weaponItemName) throws Exception{
        Character matchedRecipient=Levenshtein.fetchCharacter(recipientName, characters);
        Item weaponItem=matchedRecipient.getInventory().drawWeapon(weaponItemName);
        return matchedRecipient.getName()+" ha estratto l'arma "+weaponItem.getName();
    }

    public String putAway(String recipientName,String weaponItemName) throws Exception{
        Character matchedCharacter=Levenshtein.fetchCharacter(recipientName, characters);
        Item weaponItem=matchedCharacter.getInventory().putAway(weaponItemName);
        return matchedCharacter.getName()+" ha riposto l'arma "+weaponItem.getName();
        
    }

    public String openInventory(String recipientName) throws Exception{
        Character matchedCharacter=Levenshtein.fetchCharacter(recipientName, characters);
        String description="";
        for(Map.Entry<EquipSlot,Item> entry:matchedCharacter.getInventory().getItems_inventory().entrySet()){
            description+=entry.getValue().getName()+" equipaggiato nello slot "+entry.getKey().toString()+"\n\n";
        }
        for(Item weaponItem:matchedCharacter.getInventory().getWeapons()){
            description+=weaponItem.getName()+" riposta a posto\n\n";
        }
        return description;
        
    }



}
