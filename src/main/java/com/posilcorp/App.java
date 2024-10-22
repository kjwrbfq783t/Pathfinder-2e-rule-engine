package com.posilcorp;

import java.io.IOException;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.opencsv.exceptions.CsvValidationException;
import com.posilcorp.Utilities.FunctionEmbeddingMatcher;

public class App {
    public static void main(String[] args) {
        try {
            FunctionEmbeddingMatcher.loadCSV();
            String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            DungeonMaster myBot = new DungeonMaster();
            botsApplication.registerBot(botToken, myBot);
            myBot.registerCommands();
        } catch (TelegramApiException | CsvValidationException | NumberFormatException | IOException e) {
            e.printStackTrace();
        }

    }
}
