package com.posilcorp.EquipmentLogic;

import java.util.ArrayList;


public class StowingItem extends Item {
    private int bulkBonus;
    private int bulkCapacity;
    private ArrayList<Item> stowedItems;
    private int totalBulk;

    public StowingItem(String name, String description, int bulk, int bulkCapacity,
            int bulkBonus) {
        super(name, description, bulk);
        this.bulkBonus = bulkBonus;
        this.bulkCapacity=bulkCapacity;
        stowedItems=new ArrayList<>();
        totalBulk=0;
    }

    public int getBulkBonus() {
        return bulkBonus;
    }

    public void stowe(Item item) throws Exception{
        if(totalBulk+item.getBulk()>bulkCapacity)throw new Exception("non è possibile aggiungere l'item perchè si supera la bulk capacity");
        stowedItems.add(item);
        totalBulk+=item.getBulk();
    }


    @SuppressWarnings("unchecked")
    public ArrayList<Item> open(){
        return (ArrayList<Item>)stowedItems.clone();
    }

    public void remove(Item item) throws Exception{
        if(!stowedItems.remove(item)) throw new Exception("l'oggetto non si trova in questo container");
    }
    
}
