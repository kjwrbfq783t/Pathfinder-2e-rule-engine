package com.posilcorp.EquipmentLogic;

import java.util.ArrayList;

public class Item {

    private static IDGen idgen=new IDGen();
    private int bulk;
    private int ID;
    private String name;
    private String description;
    public ArrayList<EquipSlot> possible_slots;
    public Item(String name, String description,ArrayList<EquipSlot> possible_slots,int bulk) {
        this.name = name;
        this.description = description;
        this.possible_slots=possible_slots;
        this.bulk=bulk;
        ID=idgen.generate_ID();

    }
    public int getBulk() {
        return bulk;
    }
    public String getName() {
        return name;
    }
    public ArrayList<EquipSlot> getPossible_slots() {
        return possible_slots;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getID() {
        return ID;
    }
}
