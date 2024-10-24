package com.posilcorp.EquipmentLogic;


import com.posilcorp.Dice.Dice;
import com.posilcorp.Dice.DiceTray;

public class WeaponItem extends Item{
    public final int diceMultiplier;
    public final Dice diceType;
    public final DamageType damageType;

    public WeaponItem(String name, String description, int bulk,int diceMultiplier,Dice diceType,DamageType damageType) {
        super(name, description, bulk);
        this.diceMultiplier=diceMultiplier;
        this.diceType=diceType;
        this.damageType=damageType;        
    }

    public int getBaseDamage(){
        return DiceTray.roll(diceMultiplier, diceType);
    }

}
