package com.posilcorp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.posilcorp.OpenAI.CampaignCreatorIAOpenAi;
import com.posilcorp.OpenAI.CampaignManagerAIOpenAi;

import java.util.List;

public class DungeonMaster implements LongPollingSingleThreadUpdateConsumer {
    private TelegramClient telegramClient = new OkHttpTelegramClient("7957732855:AAFRFb6x5XxgfaT1837wRg_ZDYaS4xky5xY");

    // questi rappresentano oggetti di sessione.
    private HashMap<String, String> campaign_creating_status;
    HashMap<String, CampaignCreatorIAInterface> campaignCreatorList;
    HashMap<String, CampaignManagerIAInterface> campaignManagerList;

    HashMap<String, Boolean> turned_on;

    public DungeonMaster() {
        campaign_creating_status = new HashMap<String, String>();
        campaignManagerList = new HashMap<String, CampaignManagerIAInterface>();
        campaignCreatorList = new HashMap<String, CampaignCreatorIAInterface>();
        turned_on = new HashMap<String, Boolean>();

        // Campaign for dev purposes
        CampaignCreatorIAOpenAi devCampaignCreator = new CampaignCreatorIAOpenAi();
        devCampaignCreator.setCampaignEngine(new Campaign_Engine());
        devCampaignCreator.setCampaignName("era delle ceneri");
        devCampaignCreator.createScene("foresta", "una foresta brulicante di creature mostruose");
        devCampaignCreator.createScene("piazza", "una piazza affollata con bancarelle e mercanti");
        try {
            devCampaignCreator.createPc("cosimo", "un guerriero con scudo e spada", "piazza");
            devCampaignCreator.createPc("Antonio", "un guerriero con scudo e spada", "piazza");
            devCampaignCreator.createPc("Giovanni", "un guerriero con scudo e spada", "piazza");

            devCampaignCreator.createNpc("mario", "un mercante di gioielli", "piazza");
            devCampaignCreator.createNpc("filippo", "un boscaiolo", "foresta");
            

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().equals("/load_dev_campaign@KyrkBot")) {
            CampaignCreatorIAOpenAi devCampaignCreator = new CampaignCreatorIAOpenAi();
            devCampaignCreator.setCampaignEngine(new Campaign_Engine());
            devCampaignCreator.setCampaignName("era delle ceneri");
            devCampaignCreator.createScene("foresta", "una foresta brulicante di creature mostruose");
            devCampaignCreator.createScene("piazza", "una piazza affollata con bancarelle e mercanti");
            try {
                devCampaignCreator.createPc("cosimo", "un guerriero con scudo e spada", "piazza");
                devCampaignCreator.createPc("antonio", "ladro agile", "piazza");

                devCampaignCreator.createNpc("mario", "un mercante di gioielli", "piazza");
                devCampaignCreator.createNpc("filippo", "boscaiolo", "foresta");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            campaignManagerList.put(update.getMessage().getChatId().toString(), new CampaignManagerAIOpenAi().setCampaign_Engine(
                devCampaignCreator.getCampaign_Engine()
            ));
            campaign_creating_status.put(update.getMessage().getChatId().toString(),"terminated");
        } else if (update.hasMessage() && update.getMessage().hasText()
                && update.getMessage().getText().equals("/start@KyrkBot")) {
            turned_on.put(update.getMessage().getChatId().toString(), true);
            String creating_status = campaign_creating_status.get(update.getMessage().getChatId().toString());
            if (creating_status == null) {
                InlineKeyboardRow keyboard = new InlineKeyboardRow();
                InlineKeyboardButton button = new InlineKeyboardButton("Crea Campagna");
                button.setCallbackData("create_Campaign");
                keyboard.add(button);
                List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
                keyboards.add(keyboard);
                InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                        "Salve, sono il Game Master ti guiderò nel processo di creazione della campagna");
                sendMessage.setReplyMarkup(keyboardmarkup);
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (creating_status.equals("ongoing")) {
                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                        "Sei ancora in fase di creazione campagna, sei pregato di terminarla o di riprenderla la creazione in futuro.");
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (creating_status.equals("pending")) {
                campaign_creating_status.put(update.getMessage().getChatId().toString(), "ongoing");
                InlineKeyboardRow keyboard = new InlineKeyboardRow();
                InlineKeyboardButton button = new InlineKeyboardButton("Termina creazione campagna");
                InlineKeyboardButton button2 = new InlineKeyboardButton("Riprendi più tardi");
                button.setCallbackData("terminate_campaign_creation");
                button2.setCallbackData("pause_campaign_creation");
                keyboard.add(button);
                keyboard.add(button2);
                List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
                keyboards.add(keyboard);
                InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
                String response = campaignCreatorList.get(update.getMessage().getChatId().toString())
                        .interact(update.getMessage().getFrom().getFirstName(), null);

                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), response);
                sendMessage.setParseMode("Markdown");
                sendMessage.setReplyMarkup(keyboardmarkup);
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (creating_status.equals("terminated")) {
                CampaignManagerIAInterface campaignManager=campaignManagerList.get(update.getMessage().getChatId().toString());
                InlineKeyboardRow keyboard = new InlineKeyboardRow();
                InlineKeyboardButton button = new InlineKeyboardButton("Esci");

                button.setCallbackData("quit");
                keyboard.add(button);
                List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
                keyboards.add(keyboard);
                InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
                String response = campaignManager.interact(null, null);
                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), response);
                sendMessage.setParseMode("Markdown");

                sendMessage.setReplyMarkup(keyboardmarkup);
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callback = update.getCallbackQuery();

            if (callback.getData().equals("create_Campaign")) {
                EditMessageReplyMarkup emrm = new EditMessageReplyMarkup(
                        callback.getMessage().getChatId().toString(),
                        callback.getMessage().getMessageId(), null, null, null);
                try {
                    telegramClient.execute(emrm);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                try {
                    String chatID = callback.getMessage().getChatId().toString();
                    campaignCreatorList.put(chatID, new CampaignCreatorIAOpenAi());
                    campaignCreatorList.get(chatID).setCampaignEngine(new Campaign_Engine());
                    InlineKeyboardRow keyboard = new InlineKeyboardRow();
                    InlineKeyboardButton button = new InlineKeyboardButton("Termina creazione campagna");
                    InlineKeyboardButton button2 = new InlineKeyboardButton("Riprendi più tardi");
                    button.setCallbackData("terminate_campaign_creation");
                    button2.setCallbackData("pause_campaign_creation");
                    keyboard.add(button);
                    keyboard.add(button2);
                    List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
                    keyboards.add(keyboard);
                    InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
                    SendMessage sm = new SendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                            campaignCreatorList.get(chatID).interact(callback.getFrom().getFirstName(), null));
                    sm.setReplyMarkup(keyboardmarkup);
                    sm.setParseMode("Markdown");
                    telegramClient.execute(sm);
                    campaign_creating_status.put(chatID, "ongoing");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (callback.getData().equals("terminate_campaign_creation")) {
                String chatID = callback.getMessage().getChatId().toString();
                campaign_creating_status.put(chatID, "terminated");
                EditMessageReplyMarkup emrm = new EditMessageReplyMarkup(
                        callback.getMessage().getChatId().toString(),
                        callback.getMessage().getMessageId(), null, null, null);
                try {
                    telegramClient.execute(emrm);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                CampaignManagerAIOpenAi campaignManager = new CampaignManagerAIOpenAi();
                campaignManager.setCampaign_Engine(campaignCreatorList.get(chatID).getCampaign_Engine());
                campaignManagerList.put(chatID, campaignManager);
                InlineKeyboardRow keyboard = new InlineKeyboardRow();
                InlineKeyboardButton button = new InlineKeyboardButton("Esci");

                button.setCallbackData("quit");
                keyboard.add(button);
                List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
                keyboards.add(keyboard);
                InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
                String response = campaignManager.interact(null, null);
                SendMessage sendMessage = new SendMessage(callback.getMessage().getChatId().toString(), response);
                sendMessage.setParseMode("Markdown");

                sendMessage.setReplyMarkup(keyboardmarkup);
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else if (callback.getData().equals("pause_campaign_creation")) {
                EditMessageReplyMarkup emrm = new EditMessageReplyMarkup(
                        callback.getMessage().getChatId().toString(),
                        callback.getMessage().getMessageId(), null, null, null);
                try {
                    telegramClient.execute(emrm);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                turned_on.put(callback.getMessage().getChatId().toString(), false);
                campaign_creating_status.put(callback.getMessage().getChatId().toString(), "pending");
            }

        } else if (update.getMessage().hasText() && turned_on.get(update.getMessage().getChatId().toString()) != null
                && turned_on.get(update.getMessage().getChatId().toString()) == true) {

            if (campaign_creating_status.get(update.getMessage().getChatId().toString()).equals("ongoing")) {
                InlineKeyboardRow keyboard = new InlineKeyboardRow();
                InlineKeyboardButton button = new InlineKeyboardButton("Termina creazione campagna");
                InlineKeyboardButton button2 = new InlineKeyboardButton("Riprendi più tardi");
                button.setCallbackData("terminate_campaign_creation");
                button2.setCallbackData("pause_campaign_creation");
                keyboard.add(button);
                keyboard.add(button2);
                List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
                keyboards.add(keyboard);
                InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
                String response = campaignCreatorList.get(update.getMessage().getChatId().toString())
                        .interact(update.getMessage().getFrom().getFirstName(), update.getMessage().getText());

                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), response);
                sendMessage.setParseMode("Markdown");

                sendMessage.setReplyMarkup(keyboardmarkup);
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                CampaignManagerIAInterface campaignManager = campaignManagerList
                        .get(update.getMessage().getChatId().toString());
                InlineKeyboardRow keyboard = new InlineKeyboardRow();
                InlineKeyboardButton button = new InlineKeyboardButton("Esci");

                button.setCallbackData("quit");
                keyboard.add(button);
                List<InlineKeyboardRow> keyboards = new ArrayList<InlineKeyboardRow>();
                keyboards.add(keyboard);
                InlineKeyboardMarkup keyboardmarkup = new InlineKeyboardMarkup(keyboards);
                String response = campaignManager.interact(update.getMessage().getFrom().getFirstName(),
                        update.getMessage().getText());
                SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(), response);
                sendMessage.setParseMode("Markdown");

                sendMessage.setReplyMarkup(keyboardmarkup);
                try {
                    telegramClient.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void registerCommands() {
        SetMyCommands setMyCommands = new SetMyCommands(
                Arrays.asList(
                        new BotCommand("/start", "Activate DungeonMaster"),
                        new BotCommand("/load_dev_campaign", "List available commands")));

        setMyCommands.setScope(new BotCommandScopeDefault());

        try {
            telegramClient.execute(setMyCommands); // Register the commands
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}