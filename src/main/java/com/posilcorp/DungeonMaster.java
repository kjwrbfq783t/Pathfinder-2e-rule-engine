package com.posilcorp;

import java.util.ArrayList;


import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.telegram.telegrambots.meta.generics.TelegramClient;



import java.util.List;

public class DungeonMaster implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("7957732855:AAFRFb6x5XxgfaT1837wRg_ZDYaS4xky5xY");

    public DungeonMaster() {


    }

    @Override
    public void consume(Update update) {
        KeyboardRow keyboardRow=new KeyboardRow();

        KeyboardButton kbutton2=new KeyboardButton("apri app");
       
        kbutton2.setWebApp(new WebAppInfo("https://kjwrbfq783t.github.io/Pathfinder-2e-rule-engine/"));


        keyboardRow.add(kbutton2);
        List<KeyboardRow> keyboardRowList=new ArrayList<>();
        keyboardRowList.add(keyboardRow);
        ReplyKeyboardMarkup replyKeyboardMarkup=new ReplyKeyboardMarkup(keyboardRowList);
        replyKeyboardMarkup.setOneTimeKeyboard(true);


      
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), "peto");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}