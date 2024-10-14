package com.posilcorp;

public class Pc_character {
    private String name;
    private String phisical_description;
    
    public Pc_character(String name,String phisical_descripiton){
        this.name=name;
        this.phisical_description=phisical_descripiton;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhisical_description() {
        return phisical_description;
    }

    public void setPhisical_description(String phisical_description) {
        this.phisical_description = phisical_description;
    }
}
