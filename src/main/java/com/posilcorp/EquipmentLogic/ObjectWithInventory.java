package com.posilcorp.EquipmentLogic;

import com.posilcorp.Scene;

public interface ObjectWithInventory{
    public Inventory getInventory();
    public Scene getScene_is_on();
    public String getName();
}
