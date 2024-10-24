package com.posilcorp.EquipmentLogic;

public class IDGen {
    private static int id;
    static {
        id = 0;
    }

    public static int generate_ID() {
        int generated_id = id;
        id += 1;
        return generated_id;
    }

}
