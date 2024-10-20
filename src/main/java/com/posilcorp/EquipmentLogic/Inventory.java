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
        } else if ((dx != null && dx.equals(item))) {
            items_inventory.remove(EquipSlot.HELD_DX_HAND);
            ((StowingItem) items_inventory.get(slot_to)).put(item);
        } else if ((sx == null || !sx.equals(item))) {
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

    public Item putAway(String weaponItemName) throws Exception {
        Collection<Item> itemsOnHands = this.getItemsOnHands();
        Item weaponItem = Levenshtein.fetchItem(weaponItemName, itemsOnHands);
        if (!weaponItem.possible_slots.contains(EquipSlot.WEAPON_SLOTS)) {
            throw new Exception(
                    "non si può rimettere a posto qualcosa che non è un arma, dovresti indossarla o stivarla in un container..");
        } else if (items_inventory.get(EquipSlot.HELD_DX_HAND) != null
                && items_inventory.get(EquipSlot.HELD_DX_HAND).equals(weaponItem)) {
            weapons.add((WeaponItem) weaponItem);
            items_inventory.remove(EquipSlot.HELD_DX_HAND);
        } else {
            weapons.add((WeaponItem) weaponItem);
            items_inventory.remove(EquipSlot.HELD_SX_HAND);
        }
        return weaponItem;
    }

    public ArrayList<Item> swap(String wornWeapon) throws Exception{
        if(weapons.size()==0){
            throw new Exception("non hai sufficienti armi per poter swappare..");
        }
        Item dx=items_inventory.get(EquipSlot.HELD_DX_HAND);
        Item sx=items_inventory.get(EquipSlot.HELD_SX_HAND);
        if(dx==null && sx==null){
            throw new Exception("non hai nulla tra le mani che potresti swappare con qualcos'altro");
        }else if(dx!=null && sx!=null){
            throw new Exception("hai entrambe le mani occupate! ti serve una mano libera per poter swappare");
        }else{
            Item weaponToDraw=Levenshtein.fetchItem(wornWeapon, weapons);
            ArrayList<Item> swappedItems=new ArrayList<Item>();
            if(dx!=null && !(dx instanceof WeaponItem )){
                throw new Exception("non puoi swappare qualcosa che non è un arma");
            }else if(dx!=null && (dx instanceof WeaponItem )){
                items_inventory.remove(EquipSlot.HELD_DX_HAND);
                weapons.add((WeaponItem)dx);
                items_inventory.put(EquipSlot.HELD_DX_HAND, weaponToDraw);
                weapons.remove(weaponToDraw);
                swappedItems.add(dx);
                swappedItems.add(weaponToDraw);
                return swappedItems;
            }else if(!(sx instanceof WeaponItem )){
                throw new Exception("non puoi swappare qualcosa che non è un arma");
            }else{
                items_inventory.remove(EquipSlot.HELD_SX_HAND);
                weapons.add((WeaponItem)dx);
                items_inventory.put(EquipSlot.HELD_DX_HAND, weaponToDraw);
                weapons.remove(weaponToDraw);
                swappedItems.add(sx);
                swappedItems.add(weaponToDraw);
                return swappedItems;
            }
            }
        }
    

    

    // se return=null vuol dire che si è impugnato a due mani; se non è null vuol
    // dire che si impugna a una mano
    public Item changeGrip() throws Exception {
        Item dx = items_inventory.get(EquipSlot.HELD_DX_HAND);
        Item sx = items_inventory.get(EquipSlot.HELD_SX_HAND);
        if (dx == null && sx == null) {
            throw new Exception("non hai nulla su cui cambiare tipo di grip..");
        } else if (dx != null && sx == null) {
            return items_inventory.put(EquipSlot.HELD_SX_HAND, dx);
        } else if (dx == null && sx != null) {
            return items_inventory.put(EquipSlot.HELD_DX_HAND, sx);
        } else if (dx != null && sx != null && dx.equals(sx)) {
            return items_inventory.remove(EquipSlot.HELD_SX_HAND);
        } else {
            throw new Exception("hai entrambe le mani occupate!!");
        }

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
        } else {
            Item item = Levenshtein.fetchItem(item_name, getItemsOnHands());
            Item dx = items_inventory.get(EquipSlot.HELD_DX_HAND);
            Item sx = items_inventory.get(EquipSlot.HELD_SX_HAND);
            if (dx != null && dx.equals(item)) {
                items_inventory.remove(EquipSlot.HELD_DX_HAND);
                return item;

            } else if (sx != null && dx.equals(item)) {
                items_inventory.remove(EquipSlot.HELD_SX_HAND);
                return item;
            }
            return null;
        }
    }

    public void set(Item item) {
        items_without_slots.put(item.getName(), item);
    }

    public Item get(String itemName) throws Exception {
        Item item = items_without_slots.remove(itemName);
        if (item == null) {
            throw new Exception("oggetto senza slot specificato non esistente");
        } else {
            return item;
        }
    }

    public void set(Item item, EquipSlot slot) {
        if (slot == EquipSlot.WEAPON_SLOTS && item instanceof WeaponItem) {
            weapons.add((WeaponItem) item);
        } else {
            items_inventory.put(slot, item);
        }
    }

    public Item retrieveStowedItem(String itemName,EquipSlot containerSlot) throws Exception {
        Item stowedItem = null;
        if (items_inventory.get(EquipSlot.HELD_DX_HAND) != null
                && items_inventory.get(EquipSlot.HELD_SX_HAND) != null) {
            throw new Exception("hai le mani occupate!");
        }else if(!(items_inventory.get(containerSlot) instanceof StowingItem)) {
            throw new Exception("non hai equipaggiato un container nello slot specificato");
        }else {
            StowingItem container=((StowingItem)items_inventory.get(containerSlot));
            stowedItem=Levenshtein.fetchItem(itemName, container.getAll());
            if(items_inventory.get(EquipSlot.HELD_DX_HAND)==null){
                items_inventory.put(EquipSlot.HELD_DX_HAND,container.takeOut(stowedItem));
                return stowedItem;
            }else{
                items_inventory.put(EquipSlot.HELD_SX_HAND,container.takeOut(stowedItem));
                return stowedItem;
            }
            }
        }

        public WeaponItem weaponToAttackWith(String weaponItem) throws Exception{
            Item dx=items_inventory.get(EquipSlot.HELD_DX_HAND);
            Item sx=items_inventory.get(EquipSlot.HELD_SX_HAND);
            if(dx==null && sx==null){
                throw new Exception("non hai armi in mano");
            }else{
                Item weapon=Levenshtein.fetchItem(weaponItem, getItemsOnHands());
                if(!(weapon instanceof WeaponItem)){
                    throw new Exception("non puoi attaccare con questo item");
                }else {
                    return (WeaponItem)weapon;
                }
            
        }
    }
    
           

           

    public HashMap<String, Item> getItems_without_slot() {
        return items_without_slots;
    }

    public ArrayList<Item> getItemsOnHands() throws Exception {
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
