package com.posilcorp;


public class Pc_character extends Character{
    
    public Pc_character(String name,String phisical_descripiton,Scene scene_is_on){
        super(name, phisical_descripiton, scene_is_on);
        isNPC=false;
    }
}
