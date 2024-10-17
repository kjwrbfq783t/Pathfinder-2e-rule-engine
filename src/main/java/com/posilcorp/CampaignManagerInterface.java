package com.posilcorp;

import com.posilcorp.OpenAI.CampaignManagerIAOpenAi;

public interface CampaignManagerInterface {
    public String interact(String name, String message);
    public CampaignManagerIAOpenAi setCampaign_Engine(Campaign_Engine campaign_Engine);
    public String speak_to(String sender, String recipient, String text) throws Exception;
    public String changeScene(String recipient, String scene_name) throws Exception;
    public String getKnownScenes();
    public String getEnvironment(String name) throws Exception ;

}
