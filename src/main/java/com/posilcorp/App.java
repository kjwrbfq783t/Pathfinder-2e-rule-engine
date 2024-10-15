package com.posilcorp;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App
{
    public static void main( String[] args )
    {
         try {
           String botToken = "7957732855:AAFRFb6x5XxgfaT1837wRg_ZDYaS4xky5xY";
           TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
           DungeonMaster myBot=new DungeonMaster();
           botsApplication.registerBot(botToken,myBot);
           myBot.registerCommands();
       } catch (TelegramApiException e) {
           e.printStackTrace();
       }
    }
}
