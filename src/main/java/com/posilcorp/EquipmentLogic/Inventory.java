package com.posilcorp.EquipmentLogic;

import java.util.ArrayList;
import java.util.HashMap;


import com.posilcorp.Character;

public class Inventory {

    protected HashMap<EquipSlot, Item> wornSlotItems;
    protected ArrayList<Item> wornItems;
    protected ArrayList<Item> itemsOnHands;

    public Inventory() {
        wornSlotItems = new HashMap<>();
        wornItems = new ArrayList<>();
        itemsOnHands = new ArrayList<>();
    }

    public void stowe(Item item, StowingItem container) throws Exception {
        container.stowe(item);
    }

    public void retrieveStowed(Item item, StowingItem container) throws Exception {
        if (itemsOnHands.size() == 2) {
            throw new Exception("hai le mani occupate");
        } else {
            container.remove(item);
            itemsOnHands.add(item);
        }
    }

    // USATO PER LE AZIONI: DRAW, PICK UP, TAKE
    public void take(Item item) throws Exception {
        if (itemsOnHands.size() == 2) {
            throw new Exception("hai entrambe le mani occupate");
        } else {
            wornItems.remove(item);
            itemsOnHands.add(item);
        }
    }

    public void removeArmor() throws Exception {
        if (wornSlotItems.get(EquipSlot.WORN_ARMOR) == null) {
            throw new Exception("non indossi armature");
        } else {
            wornItems.add(wornSlotItems.remove(EquipSlot.WORN_ARMOR));
        }
    }

    public void donArmor(Item item) throws Exception {
        if (wornSlotItems.get(EquipSlot.WORN_ARMOR) != null) {
            throw new Exception("indossi già un'armatura");
        } else {
            wornSlotItems.put(EquipSlot.WORN_ARMOR, item);
        }
    }

    public void passOff(Item item, Character character) throws Exception {
        if (character.getInventory().itemsOnHands.size() == 2) {
            throw new Exception("il destinatario ha le mani occupate");
        } else {
            itemsOnHands.remove(item);
            character.getInventory().itemsOnHands.add(item);
        }
    }

    public void add(Item item){
        wornItems.add(item);
    }

    public void wear(Item item){
        if (item.slot != null) {
            wornSlotItems.put(item.slot, item);
        } else {
            wornItems.add(item);
        }
    }
    public void putAway(Item item) throws Exception {
        wornItems.add(item);

    }
    public void changeGrip() throws Exception {
        if(itemsOnHands.size()==0){
            throw new Exception("non hai nulla in mano");
        }else if(itemsOnHands.size()==1){
            itemsOnHands.add(itemsOnHands.get(0));
        }else if(itemsOnHands.size()==2 && itemsOnHands.get(0).equals(itemsOnHands.get(1))){
            itemsOnHands.remove(1);
        }else{
            throw new Exception("hai oggetti distinti nelle tue mani!");
        }
    }

    public void swap(Item substituting,Item substituted) throws Exception {
        itemsOnHands.remove(substituted);
        wornItems.add(substituted);
        itemsOnHands.add(substituting);
    }


    @SuppressWarnings("unchecked")
    public ArrayList<Item> getWornItems(){
        return (ArrayList<Item>)wornItems.clone();
    }

    @SuppressWarnings("unchecked")
    public HashMap<EquipSlot,Item> getWornSlotItems(){
        return (HashMap<EquipSlot,Item>)wornSlotItems.clone();
    }

}
