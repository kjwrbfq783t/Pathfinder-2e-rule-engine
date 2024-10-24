package com.posilcorp.EquipmentLogic;

import com.posilcorp.IDGen;

public class Item {

    private int bulk;
    private int ID;
    private String name;
    private String description;
    protected EquipSlot slot;
    public Item(String name, String description,int bulk) {
        this.name = name;
        this.description = description;
        this.bulk=bulk;
        slot=null;
        ID=IDGen.generate_ID();
    }
    public int getBulk() {
        return bulk;
    }

    public String getName() {
        return name;
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
