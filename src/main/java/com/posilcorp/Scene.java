package com.posilcorp;

import java.util.Collection;
import java.util.HashMap;

public class Scene implements InventoryableInterface{
    private String Description;
    private String name;
    private HashMap<String,Pc_character> pc_charatcters;
    private HashMap<String,Npc_character> npc_charatcters;
    private Inventory inventory;

    

    public Scene(String description, String name) {
        Description = description;
        this.name = name;
        this.pc_charatcters=new HashMap<String,Pc_character>();
        this.npc_charatcters=new HashMap<String,Npc_character>();
        inventory=new Inventory();
    }
    public String getDescription() {
        return Description;
    }
    public Collection<Pc_character> getPc_characters(){
        return pc_charatcters.values();
    }
    public Collection<Npc_character> getNpc_characters(){
        return npc_charatcters.values();
    }
    public void addPc(Pc_character pc_character){
        pc_charatcters.put(pc_character.getName(), pc_character);
    }
    public void addNpc(Npc_character npc_character){
        npc_charatcters.put(npc_character.getName(), npc_character);
    }

    public Inventory getInventory() {
        return inventory;
    }
    public void setDescription(String description) {
        Description = description;
  
    }
    public String getName(){
        return this.name;
    }
    @Override
    public void putInInventory(Item item) {
        inventory.put(item);
    }

    @Override
    public Item getFromInventory(String item_name) {
        return inventory.get(item_name);
    }

    
}
