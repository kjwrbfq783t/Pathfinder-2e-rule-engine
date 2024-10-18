package com.posilcorp.EquipmentLogic;

import java.util.ArrayList;
import java.util.Collection;

import com.posilcorp.Utilities.Levenshtein;

public class ItemBuilder {
    public static Item getIstanceof(String item_name) throws Exception{
        return Levenshtein.fetchItem(item_name, items);
    }

    private static Collection<Item> items=new ArrayList<Item>();

    static{
        items.add(new StowingItem("zaino","uno zaino con max bulk", 0, 5));
        items.add(new WeaponItem("spada", "una spada rozza", 1));
    }

}
