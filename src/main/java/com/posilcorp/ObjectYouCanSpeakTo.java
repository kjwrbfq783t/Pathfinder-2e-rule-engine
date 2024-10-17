package com.posilcorp;

public abstract class ObjectYouCanSpeakTo {
    private String name;
    public ObjectYouCanSpeakTo(String name){
        this.name=name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String speak_to(ObjectYouCanSpeakTo obj, String text) throws Exception{
        return null;
    }

}
