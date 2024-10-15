package com.posilcorp;



public class Npc_character {

    
    public Scene scene_is_on;
    public NpcIAInterface intelligence;
    public String description;


    public Npc_character(String description,Scene scene_is_on,NpcIAInterface intelligence) {

        this.scene_is_on = scene_is_on;
        this.description=description;
        intelligence.load_initialConf(description, scene_is_on.getDescription());
    }

    public String speak_to(String name,String text){
        String response="";
        
        try{
            response=intelligence.speak_to(name,text);
        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public void changeScene(Scene new_scene){
        this.scene_is_on=new_scene;
        intelligence.updateScene(new_scene.getDescription());
    }

    




}
