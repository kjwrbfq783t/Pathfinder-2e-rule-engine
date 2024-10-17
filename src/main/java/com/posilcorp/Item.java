package com.posilcorp;

public class Item {

    private static int id=0;
    private int ID;
    private String name;
    private String description;
    public Item(String name, String description) {
        this.name = name;
        this.description = description;
        ID=id;
        id=id+1;
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
