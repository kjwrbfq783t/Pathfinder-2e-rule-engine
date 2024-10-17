package com.posilcorp;

public interface InventoryableInterface {

    public void putInInventory(Item item);
    public Item getFromInventory(String item_name);

}
