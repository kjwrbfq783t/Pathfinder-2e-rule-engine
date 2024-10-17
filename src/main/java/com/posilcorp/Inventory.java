package com.posilcorp;

import java.util.HashMap;

public class Inventory {
    private int weight;
    private HashMap<Integer,Item> inventory;
    public Inventory(){
        inventory=new HashMap<Integer,Item>();
    }
    public void put(Item item){
        inventory.put(item.getID(), item);
    }
    public Item get(String item_name){
        for(Item item: inventory.values()){
            if(item.getName().equals(item_name)){
                inventory.remove(item.getID());
                return item;
            }
        }
        return null;
    }
}
