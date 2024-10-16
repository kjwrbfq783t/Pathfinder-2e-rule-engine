package com.posilcorp;

public interface CampaignCreatorIAInterface {

    public String interact(String name,String message);

    public void setCampaignName(String campaign_name);

    public void createPc(String name, String physical_description,String scene_name);

    public void createNpc(String name,String description, String scene_name)throws Exception;

    public void createScene(String name, String description);

    public void selectActualScene(String scene_name);
    public void setCampaignEngine(Campaign_Engine campaign_Engine);
    public Campaign_Engine getCampaign_Engine();


}
