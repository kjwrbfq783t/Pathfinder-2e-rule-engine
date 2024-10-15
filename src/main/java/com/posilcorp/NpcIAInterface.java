package com.posilcorp;

public interface NpcIAInterface{




    public void load_initialConf(String description,String scene_description);

    public String speak_to(String name,String text) throws Exception;

    public void updateScene(String scene_description);



}
