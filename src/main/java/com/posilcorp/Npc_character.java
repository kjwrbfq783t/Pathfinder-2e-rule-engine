package com.posilcorp;



public class Npc_character {

    public String name;
    public Scene scene_is_on;
    public NpcIAInterface intelligence;
    public String description;


    public Npc_character(String name,String description,Scene scene_is_on,NpcIAInterface intelligence) {
        this.name=name;

        this.scene_is_on = scene_is_on;
        this.description=description;
        intelligence.load_initialConf(description, scene_is_on.getDescription(),scene_is_on.getName());
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

    public Scene getScene_is_on() {
        return scene_is_on;
    }

    public String getName() {
        return name;
    }

    public void changeScene(Scene new_scene){
        this.scene_is_on=new_scene;
        intelligence.updateScene(new_scene.getDescription(),new_scene.getName());
    }

    




}
