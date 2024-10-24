package com.posilcorp;

import java.util.HashMap;
import java.util.Map;

import com.posilcorp.Dice.Dice;
import com.posilcorp.Dice.DiceTray;
import com.posilcorp.EquipmentLogic.Inventory;
import com.posilcorp.EquipmentLogic.ObjectWithInventory;

public abstract class Character extends ObjectYouCanSpeakTo implements ObjectWithInventory{
    private Scene scene_is_on;
    public String description;
    protected Boolean isNPC;
    private Inventory inventory;
    private Map<KeyAttribute,Integer> keyAttributeModifiers;
    public int hitPoints;

    public Character(String name, String description, Scene scene_is_on,int hitPoints) {
        super(name);
        this.hitPoints=hitPoints;
        this.keyAttributeModifiers=new HashMap<KeyAttribute,Integer>();
        keyAttributeModifiers.put(KeyAttribute.CHA, 0);
        keyAttributeModifiers.put(KeyAttribute.CON, 0);
        keyAttributeModifiers.put(KeyAttribute.DEX, 0);
        keyAttributeModifiers.put(KeyAttribute.INT, 0);
        keyAttributeModifiers.put(KeyAttribute.STR, 0);
        keyAttributeModifiers.put(KeyAttribute.WIS, 0);
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

    public int getKeyAttributeModifier(KeyAttribute keyAttribute){
        return keyAttributeModifiers.get(keyAttribute);
    }

    public int getAttackRoll(){
        return DiceTray.roll(1, Dice.D20)+keyAttributeModifiers.get(KeyAttribute.STR);
    }



    public String applyDealtDamage(int damage,String dealer) throws Exception{
        hitPoints-=damage;
        return null;
    }

    public int getArmorClass(){
        return keyAttributeModifiers.get(KeyAttribute.DEX);
    }

    




}
