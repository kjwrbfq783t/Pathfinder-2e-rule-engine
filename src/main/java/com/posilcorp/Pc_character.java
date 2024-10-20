package com.posilcorp;

import java.util.Map;

public class Pc_character extends Character{
    
    public Pc_character(String name,String phisical_descripiton,Scene scene_is_on,int hitPoints){
        super(name, phisical_descripiton, scene_is_on,hitPoints);
        isNPC=false;
    }
}
