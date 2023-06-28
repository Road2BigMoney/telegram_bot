package com.example.demo.bot;

import com.example.demo.config.BotConfig;
import com.example.demo.model.CurrencyModel;
import com.example.demo.model.IPModel;
import com.example.demo.service.CurrencyService;
import com.example.demo.service.IPService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    private final CurrencyService currencyService;
    @Override
    public void onUpdateReceived(Update update) {
        IPModel ipModel = new IPModel();
        CurrencyModel currencyModel = new CurrencyModel();
        String ip = "";
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText.split(" ")[0]) {
                case "/start" :
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "currency" :
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                default:
                     try {
                        ip = IPService.getIP(messageText, ipModel);
                    } catch (IOException e) {
                        sendMessage(chatId, "We cant find your IP");
                    }
                    sendMessage(chatId, ip);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return  botConfig.getToken();
    }
    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Enter getMyIP if you want to know your ip";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}
