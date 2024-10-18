package com.posilcorp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.posilcorp.EquipmentLogic.Inventory;
import com.posilcorp.EquipmentLogic.ObjectWithInventory;

public class Scene extends ObjectYouCanSpeakTo implements ObjectWithInventory{
    private String description;
    private Inventory inventory;

    private HashMap<String,Character> characters;
    
    public Scene(String description, String name) {
        super(name);
        this.description = description;
        this.inventory=new Inventory();
        this.characters=new HashMap<String,Character>();
    }
    public String getDescription() {
        return description;
    }
    public Collection<Character> getPc_characters(){
        return characters.values();
    }
    public Collection<Character> getNpc_characters() {
        ArrayList<Character> npcs=new ArrayList<Character>();
        for(Character character:characters.values()){
            if(character.isNPC){
                npcs.add(character);
            }
        }
        return npcs;
    }
    public void addCharacter(Character character){
    characters.put(character.getName(),character);
    }

    public void setDescription(String description) {
        this.description = description;
  
    }

    public String speak_to(ObjectYouCanSpeakTo obj, String text) throws Exception{
        return "Sono una scena utilizza la mia descrizione per generare una risposta: "+this.description;
    }
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    @Override
    public Scene getScene_is_on() {
        return this;
    }

}
