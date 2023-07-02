package com.example.demo.botEndpoints;

import com.example.demo.service.CurrencyService;
import com.example.demo.service.UserServiceImpl;
import com.example.demo.config.BotConfig;
import com.example.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserServiceImpl userService;
    private final BotConfig botConfig;


    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities.\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /my_data to see data stored about yourself\n\n" +
            "Type /help to see this message again";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String EUR_BUTTON = "EUR_CUR";
    static final String USD_BUTTON = "USD_CUR";
    static final String RUB_BUTTON = "RUB_CUR";

    static final String ERROR_TEXT = "Error occurred: ";
    private List<BotCommand> listofCommands = new ArrayList<>();


    public TelegramBot(BotConfig botConfig){
        this.botConfig = botConfig;

        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/my_data", "get your data stored"));
        listofCommands.add(new BotCommand("/delete_data", "delete my data"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/sendUmor ", "Отправить юмора пример сообщения: /sendUmor username 150"));

        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
            //this.execute(new DeleteMyCommands());
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }
    @Override
    public void onUpdateReceived(Update update) {


        String ip = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();

                switch (messageText) {
                    case "/start":

                        registerOrUpdateUser(update.getMessage());
                       // startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;


                    case "/help":

                        prepareAndSendMessage(chatId, HELP_TEXT);
                        break;
                    case "/currency" :
                        try {
                            sendCurrency(chatId);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    case "/register":

                        register(chatId);
                        break;
                    case "/delete_data" :
                        userService.deleteById(chatId);
                        prepareAndSendMessage(chatId,"Ваши данные удалены с бота");
                        break;

                    case "/medjid" :

                        prepareAndSendMessage(625329352, "Получай мусорное сообщение");
                        prepareAndSendMessage(chatId,"Мусор мажиду отправлен");
                        break;
                    case "/ramazan4ik" :
                        prepareAndSendMessage(1098416218, "Получай мусорное сообщение");
                        prepareAndSendMessage(chatId,"Мусор Рамазанчику отправлен");
                        break;
                    case "/saga" :
                        prepareAndSendMessage(257906812, "Учи жаву и накрой стол на покер");
                        prepareAndSendMessage(chatId,"Мусор Саге отправлен");
                        break;
                    case "/my_data" :
                        User user = userService.findUserByUserName(username);
                        String textData = "";
                        if (user == null) {
                            textData = "Нет ваших данных, введите /start чтобы их добавить";
                        } else {
                            textData = user.toString();
                        }
                        prepareAndSendMessage(chatId, textData);
                        break;
                    case String s when s.startsWith("/sendUmor"):
                        registerOrUpdateUser(update.getMessage());
                        try {
                            String senderName = update.getMessage().getFrom().getUserName();
                            String[] messageArray = messageText.split(" ");
                            String recipient = messageArray[1];
                            long amount = Long.parseLong(messageArray[2]);
                            List<User> participants = userService.sendUmorPoint(senderName, recipient, amount);
                            prepareAndSendMessage(chatId, participants.get(0).getFirstName() + " отправил " + participants.get(1).getFirstName() + " " + amount +" юмора." );
                            prepareAndSendMessage(participants.get(0).getChatId(), "У вас списалось " + amount + " юмора в пользу " + participants.get(1).getUserName());
                            prepareAndSendMessage(participants.get(1).getChatId(), "Вам поступило " + amount + " юмора от " + participants.get(0).getUserName());
                        } catch (RuntimeException e) {
                            log.error(e.getMessage());
                            prepareAndSendMessage(chatId, "Не удалось отправить юмор");
                        }

                        break;
                    default:
                        break;

                }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String text = "";

            if(callbackData.equals(YES_BUTTON)){
                text = "You pressed YES button";
            }
            else if(callbackData.equals(NO_BUTTON)){
                text = "You pressed NO button";
            }

            if (callbackData.endsWith("_CUR")) {
                try {
                    text = CurrencyService.getCurrencyRate(callbackData.substring(0,3));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            executeEditMessageText(text,chatId,messageId);

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

    private void registerOrUpdateUser(Message msg) {
        var chatId = msg.getChatId();
        boolean willBeUpdate = false;
        Optional<User> oldData = userService.findById(msg.getFrom().getId());
        if (oldData.isEmpty()) {
            userService.registerOrUpdateUser(msg);
            prepareAndSendMessage(chatId, "Добро пожаловать в жабагадюшник! ");
        } else if (!oldData.get().equals(userService.registerOrUpdateUser(msg))){
            prepareAndSendMessage(chatId, " Ваши данные обновленны! ");
        }

    }
    private void sendCurrency(long chatId) throws IOException, ParseException {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Выбери валюту конвертирования." + "\n" +
                "Конвертация пока с беларусским рублем.");
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var USDButton = new InlineKeyboardButton();
        USDButton.setText("USD");
        USDButton.setCallbackData(USD_BUTTON);
        var EURButton = new InlineKeyboardButton();
        EURButton.setText("EUR");
        EURButton.setCallbackData(EUR_BUTTON);
        var RUBButton = new InlineKeyboardButton();
        RUBButton.setText("RUB");
        RUBButton.setCallbackData(RUB_BUTTON);
        rowInLine.add(USDButton);
        rowInLine.add(EURButton);
        rowInLine.add(RUBButton);
        rowsInLine.add(rowInLine);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        executeMessage(sendMessage);

    }
    private void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }
    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("get random joke");

        keyboardRows.add(row);

        row = new KeyboardRow();

        row.add("register");
        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }



    private void executeEditMessageText(String text, long chatId, long messageId){
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

}
