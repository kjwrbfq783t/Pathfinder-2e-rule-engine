package com.posilcorp.EquipmentLogic;

public class WeaponItem extends Item{

    public WeaponItem(String name, String description, int bulk) {
        super(name, description, bulk);
        possible_slots.add(EquipSlot.WEAPON_SLOTS);
    }

}
