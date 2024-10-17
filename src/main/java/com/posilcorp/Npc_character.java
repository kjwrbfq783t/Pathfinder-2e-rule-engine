package com.posilcorp;


import com.posilcorp.OpenAI.NpcIAOpenAi;


public class Npc_character extends Character{

    public String name;
    public Scene scene_is_on;
    public NpcIAInterface intelligence;
    public String description;


    public Npc_character(String name,String description,Scene scene_is_on) {
        super(name,description,scene_is_on);
        this.isNPC=true;
        intelligence=new NpcIAOpenAi(name,description, scene_is_on.getDescription(),scene_is_on.getName());
    }
    
    @Override
    public String speak_to(ObjectYouCanSpeakTo obj,String text){
        String response="";
        try{
            response=intelligence.speak_to(obj.getName(),text);
        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void changeScene(Scene new_scene){
        this.scene_is_on=new_scene;
        intelligence.updateScene(new_scene.getDescription(),new_scene.getName());
    }

    


}
