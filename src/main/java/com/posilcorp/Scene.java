package com.posilcorp;

public class Scene {
    private String Description;
    private String name;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
  
    }

    public Scene(String description,String name) {
        Description = description;
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    
}
