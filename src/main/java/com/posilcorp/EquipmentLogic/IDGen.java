package com.posilcorp.EquipmentLogic;

public class IDGen {
    private int id;
    public IDGen(){
        id=0;
    }
    public int generate_ID(){
        int generated_id=id;
        id+=1;
        return generated_id;
    }


}
