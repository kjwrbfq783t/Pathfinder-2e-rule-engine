package com.posilcorp.EquipmentLogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.posilcorp.Utilities.Levenshtein;

public class Inventory {
    public HashMap<EquipSlot, Item> getItems_inventory() {
        return items_inventory;
    }

    public Collection<WeaponItem> getWeapons() {
        return weapons;
    }

    protected HashMap<EquipSlot, Item> items_inventory;
    protected HashMap<String, Item> items_without_slots;
    protected Collection<WeaponItem> weapons;
    private static ArrayList<EquipSlot> drawableSlots = new ArrayList<EquipSlot>();
    static {
        drawableSlots.add(EquipSlot.WEARED_SX_HAND);
        drawableSlots.add(EquipSlot.WEARED_DX_HAND);
        drawableSlots.add(EquipSlot.WEARED_TORSO);
        drawableSlots.add(EquipSlot.WEARED_UNDERCOAT);
        drawableSlots.add(EquipSlot.WEARED_BOOTS);
        drawableSlots.add(EquipSlot.WEARED_HEAD);
        drawableSlots.add(EquipSlot.WEARED_BELT);
        drawableSlots.add(EquipSlot.WEARED_BACK);
        drawableSlots.add(EquipSlot.WEARED_VITA);
    }

    public Inventory() {

        items_inventory = new HashMap<EquipSlot, Item>();
        items_without_slots = new HashMap<String, Item>();
        weapons = new ArrayList<WeaponItem>();
    }

    public void stowe(Item item, EquipSlot slot_to) throws Exception {
        if (slot_to == null)
            throw new Exception("slot non trovato");
        Item dx = items_inventory.get(EquipSlot.HELD_DX_HAND);
        Item sx = items_inventory.get(EquipSlot.HELD_SX_HAND);
        if ((dx == null || !dx.equals(item)) && (sx == null || !sx.equals(item))) {
            throw new Exception(
                    "non puoi eseguire la stowe action se non hai niente in mano o l'item che che vuoi stoware non è in mano");
        } else if (!(items_inventory.get(slot_to) instanceof StowingItem)) {
            throw new Exception("non puoi storare in qualcosa che non è un container");
        } else if((dx != null && dx.equals(item))){
            items_inventory.remove(EquipSlot.HELD_DX_HAND);
            ((StowingItem) items_inventory.get(slot_to)).put(item);
        }else if((sx == null || !sx.equals(item))){
            items_inventory.remove(EquipSlot.HELD_SX_HAND);
            ((StowingItem) items_inventory.get(slot_to)).put(item);
        }
    }

    public Item drawFrom(EquipSlot slot) throws Exception {
        if (!drawableSlots.contains(slot))
            throw new Exception("non puoi draware da questo slot");
        if (items_inventory.get(slot) == null) {
            throw new Exception("non c'è nulla nello slot desiderato");
        } else if (items_inventory.get(EquipSlot.HELD_DX_HAND) != null
                && items_inventory.get(EquipSlot.HELD_SX_HAND) != null) {
            throw new Exception("hai le mani occupate!");
        } else if (items_inventory.get(EquipSlot.HELD_DX_HAND) == null) {
            Item item = items_inventory.get(slot);
            items_inventory.put(EquipSlot.HELD_DX_HAND, item);
            items_inventory.remove(slot);
            return item;
        } else {
            Item item = items_inventory.get(slot);
            items_inventory.put(EquipSlot.HELD_SX_HAND, item);
            items_inventory.remove(slot);
            return item;
        }
    }

    public Item drawWeapon(String Item_name) throws Exception {
        Item weaponItem = Levenshtein.fetchItem(Item_name, weapons);
        if (items_inventory.get(EquipSlot.HELD_DX_HAND) != null
                && items_inventory.get(EquipSlot.HELD_SX_HAND) != null) {
            throw new Exception("hai le mani occupate");
        } else if (items_inventory.get(EquipSlot.HELD_DX_HAND) == null) {
            weapons.remove(weaponItem);
            items_inventory.put(EquipSlot.HELD_DX_HAND, weaponItem);
        } else {
            weapons.remove(weaponItem);
            items_inventory.put(EquipSlot.HELD_SX_HAND, weaponItem);
        }
        return weaponItem;
    }
    public Item putAway(String weaponItemName) throws Exception{
       Collection<Item> itemsOnHands= this.getItemsOnHands();
       Item weaponItem=Levenshtein.fetchItem(weaponItemName, itemsOnHands);
       if(!weaponItem.possible_slots.contains(EquipSlot.WEAPON_SLOTS)){
        throw new Exception("non si può rimettere a posto qualcosa che non è un arma, dovresti indossarla o fare 'stowe'");
       } else if(items_inventory.get(EquipSlot.HELD_DX_HAND)!=null && items_inventory.get(EquipSlot.HELD_DX_HAND).equals(weaponItem)){
        weapons.add((WeaponItem)weaponItem);
        items_inventory.remove(EquipSlot.HELD_DX_HAND);
       }else {
        weapons.add((WeaponItem)weaponItem);
        items_inventory.remove(EquipSlot.HELD_SX_HAND);
       }
       return weaponItem;
    }
        

    

    public void wear(Item item, EquipSlot slot) throws Exception {
        Item dx = items_inventory.get(EquipSlot.HELD_DX_HAND);
        Item sx = items_inventory.get(EquipSlot.HELD_SX_HAND);
        if (items_inventory.get(slot) != null) {
            throw new Exception("lo slot è già occupato");
        } else if (!item.getPossible_slots().contains(slot)) {
            throw new Exception("non è possibile equipaggiare nello slot desiderato");
        } else if (dx != null && dx.equals(item)) {
            items_inventory.put(slot, items_inventory.remove(EquipSlot.HELD_DX_HAND));
        } else if (sx != null && sx.equals(item)) {
            items_inventory.put(slot, items_inventory.remove(EquipSlot.HELD_SX_HAND));
        }
    }

    public void take(Item item) throws Exception {
        if (items_inventory.get(EquipSlot.HELD_DX_HAND) != null
                && items_inventory.get(EquipSlot.HELD_SX_HAND) != null) {
            throw new Exception("hai le mani occupate");
        } else if (items_inventory.get(EquipSlot.HELD_DX_HAND) == null) {
            items_inventory.put(EquipSlot.HELD_DX_HAND, item);
        } else {
            items_inventory.put(EquipSlot.HELD_SX_HAND, item);
        }

    }

    public Item give(String item_name) throws Exception {
        if (items_inventory.get(EquipSlot.HELD_DX_HAND) == null
                && items_inventory.get(EquipSlot.HELD_SX_HAND) == null) {
            throw new Exception("non si ha nulla tra le mani");
        } else{
            Item item=Levenshtein.fetchItem(item_name, getItemsOnHands());
            Item dx=items_inventory.get(EquipSlot.HELD_DX_HAND);
            Item sx=items_inventory.get(EquipSlot.HELD_SX_HAND);
            if(dx!=null && dx.equals(item)){
                items_inventory.put(EquipSlot.HELD_DX_HAND, null);
                return item;

            }else if(sx!=null && dx.equals(item)){
                items_inventory.put(EquipSlot.HELD_SX_HAND, null);
                return item;
            }
            return null;
        }
    }

    public void set(Item item) {
        items_without_slots.put(item.getName(), item);
    }

    public Item get(String itemName) throws Exception {
        Item item = items_without_slots.get(itemName);
        if (item == null) {
            throw new Exception("oggetto senza slot specificato non esistente");
        } else {
            return item;
        }
    }

    public void set(Item item, EquipSlot slot) {
        if (slot == EquipSlot.WEAPON_SLOTS && item instanceof WeaponItem) {
            weapons.add((WeaponItem)item);
        }else{
            items_inventory.put(slot, item);
        }
    }

    public Item retrieveStowedItem(String itemName) throws Exception {
        Item stowedItem = null;
        if (items_inventory.get(EquipSlot.HELD_DX_HAND) != null
                && items_inventory.get(EquipSlot.HELD_SX_HAND) != null) {
            throw new Exception("hai le mani occupate!");
        }
        for (Item optionalStowingItem : items_inventory.values()) {
            if (optionalStowingItem instanceof StowingItem) {
                stowedItem = Levenshtein.fetchItem(itemName, ((StowingItem) optionalStowingItem).getAll());
                ((StowingItem) optionalStowingItem).takeOut(stowedItem);

            } else {
                continue;
            }
        }
        if (stowedItem == null) {
            throw new Exception("oggetto non trovato");
        }
        return stowedItem;
    }

    public HashMap<String, Item> getItems_without_slot() {
        return items_without_slots;
    }

    public Collection<Item> getItemsOnHands() throws Exception {
        ArrayList<Item> itemsOnHand = new ArrayList<Item>();
        Item dx = items_inventory.get(EquipSlot.HELD_DX_HAND);
        Item sx = items_inventory.get(EquipSlot.HELD_SX_HAND);
        if (dx != null) {
            itemsOnHand.add(dx);
        } else if (sx != null) {
            itemsOnHand.add(sx);
        } else {
            throw new Exception("non hai niente tra le mani");
        }
        return itemsOnHand;
    }

    

}
