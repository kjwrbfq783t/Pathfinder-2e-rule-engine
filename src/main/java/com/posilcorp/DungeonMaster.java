package com.posilcorp;

import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.List;

public class DungeonMaster implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("7957732855:AAFRFb6x5XxgfaT1837wRg_ZDYaS4xky5xY");
    private Boolean campaign_creating_mode = false;
    CampaignCreatorIAOpenAi campaignCreator;


    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callback = update.getCallbackQuery();
            if (callback.getData().equals("create_Campaign")) {
                try {
                    this.campaignCreator=new CampaignCreatorIAOpenAi();
                    campaignCreator.setCampaignEngine(new Campaign_Engine());
                    SendMessage sm = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                            campaignCreator.tellGPT(null));
                            sm.setParseMode("Markdown");
                    telegramClient.execute(sm);
                    campaign_creating_mode = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(callback.getData().equals("terminate_campaign_creation")){
                campaign_creating_mode = false;
            }

        } else if (update.getMessage().hasText() && update.getMessage().getText().equals("@KyrkBot")
                && campaign_creating_mode == false) {
            InlineKeyboardRow keyboard = new InlineKeyboardRow();
            InlineKeyboardButton button = new InlineKeyboardButton("Crea Campagna");
            button.setCallbackData("create_Campaign");
            keyboard.add(button);
            List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
            keyboards.add(keyboard);
            InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                    "Salve, aono il Game Master");
            sendMessage.setReplyMarkup(keyboardmarkup);
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.getMessage().hasText() && campaign_creating_mode == true) {
            InlineKeyboardRow keyboard = new InlineKeyboardRow();
            InlineKeyboardButton button = new InlineKeyboardButton("Termina creazione campagna");
            button.setCallbackData("terminate_campaign_creation");
            keyboard.add(button);
            List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
            keyboards.add(keyboard);
            InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
            String response;

            response = campaignCreator.tellGPT(update.getMessage().getText());

            SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),response);
            sendMessage.setParseMode("Markdown");

            sendMessage.setReplyMarkup(keyboardmarkup);
            try {
                telegramClient.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }

    public void registerCommands() {
        SetMyCommands setMyCommands = new SetMyCommands(
                Arrays.asList(
                        new BotCommand("/start", "Start interacting with the bot"),
                        new BotCommand("/help", "List available commands")));

        setMyCommands.setScope(new BotCommandScopeDefault());

        try {
            telegramClient.execute(setMyCommands); // Register the commands
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}