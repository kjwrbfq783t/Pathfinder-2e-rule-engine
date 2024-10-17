package com.posilcorp;

public class Pc_character implements InventoryableInterface{
    private String name;
    private String phisical_description;
    private Scene scene_is_on;
    private Inventory inventory;
    
    public Pc_character(String name,String phisical_descripiton,Scene scene_is_on){
        this.scene_is_on=scene_is_on;
        this.name=name;
        this.phisical_description=phisical_descripiton;
        inventory=new Inventory();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scene getScene_is_on() {
        return scene_is_on;
    }

    public void setScene_is_on(Scene scene_is_on) {
        this.scene_is_on = scene_is_on;
    }

    public String getPhisical_description() {
        return phisical_description;
    }

    public void setPhisical_description(String phisical_description) {
        this.phisical_description = phisical_description;
    }

    public void changeScene(Scene scene) {
        this.scene_is_on=scene;
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
