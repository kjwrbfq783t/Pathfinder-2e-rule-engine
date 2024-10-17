package com.posilcorp.EquipmentLogic;

import java.util.ArrayList;
import java.util.Collection;

public class StowingItem extends Item {
    private ArrayList<Item> items;
    private int bulk_capacity;

    public StowingItem(String name, String description, ArrayList<EquipSlot> possible_slots, int bulk,
            int bulk_capacity) {
        super(name, description, possible_slots, bulk);
        this.bulk_capacity = bulk_capacity;
    }

    public void put(Item item) throws Exception {
        if (getTotalBulk() + item.getBulk() > bulk_capacity)
            throw new Exception("si supera il bulk massimo");
        items.add(item);
    }

    public int getTotalBulk() {
        int total = 0;
        for (Item item : items) {
            total += item.getBulk();
        }
        return total;
    }

    public void takeOut(Item item) throws Exception {
        if (!items.contains(item)) {
            throw new Exception("item non trovato");
        } else {
            items.remove(item);
        }
    }

    public String[] getItemNames() {
        String[] itemNames = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i).getName();
        }
        return itemNames;
    }

    public Collection<Item> getAll() {
        return items;
    }

}
