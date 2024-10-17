package com.posilcorp;

import com.posilcorp.EquipmentLogic.Inventory;
import com.posilcorp.EquipmentLogic.ObjectWithInventory;

public abstract class Character extends ObjectYouCanSpeakTo implements ObjectWithInventory{
    private Scene scene_is_on;
    public String description;
    protected Boolean isNPC;
    private Inventory inventory;

    public Character(String name, String description, Scene scene_is_on) {
        super(name);
        this.scene_is_on = scene_is_on;
        this.description = description;
        this.inventory=new Inventory();
    }

    public Scene getScene_is_on() {
        return scene_is_on;
    }

    public void setScene_is_on(Scene scene_is_on) {
        this.scene_is_on = scene_is_on;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsNPC() {
        return isNPC;
    }

    public void changeScene(Scene new_scene) {
        this.scene_is_on = new_scene;
    }

    @Override
    public Inventory getInventory(){
        return inventory;
    }




}
