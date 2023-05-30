package com.example.telegrambot;

import com.example.telegrambot.dto.VacancyDto;
import com.example.telegrambot.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VacanciesBot extends TelegramLongPollingBot {
    @Autowired
    private VacancyService vacancyService;

    private final Map<Long, String> lastShownVacancyLevel = new HashMap<>();

    public VacanciesBot() {
        //constructor
        super("write here your private botToken");
    }

    @Override
    public void onUpdateReceived(Update update) {
        //message processing logic (receiving messages from bot)
        //update.getMessage() - to get message from user
        try {
            if (update.getMessage() != null){
                handleStartCommand(update);
            }
            if (update.getCallbackQuery() != null){
                String callbackData = update.getCallbackQuery().getData();
                if ("show vacancies for junior".equals(callbackData)){
                    showJuniorVacancies(update);
                } else if ("show vacancies for middle".equals(callbackData)) {
                    showMiddleVacancies(update);
                } else if ("show vacancies for senior".equals(callbackData)) {
                    showSeniorVacancies(update);
                } else if (callbackData.startsWith("vacancyID=")) {
                    String id = callbackData.split("=")[1];
                    showVacancyDescription(id, update);
                } else if ("backToVacancies".equals(callbackData)) {
                    handleBackToVacanciesCommand(update);
                } else if ("backToStartMenu".equals(callbackData)) {
                    handleBackToStartMenu(update);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't send message to user!", e);
        }
    }

    private void handleBackToStartMenu(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose title:");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getStartMenu());
        execute(sendMessage);
    }

    private void handleBackToVacanciesCommand(Update update) throws TelegramApiException {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String level = lastShownVacancyLevel.get(chatId);

        if ("junior".equals(level)){
            showJuniorVacancies(update);
        } else if ("middle".equals(level)) {
            showMiddleVacancies(update);
        } else if ("senior".equals(level)) {
            showSeniorVacancies(update);
        }
    }

    private void showVacancyDescription (String id, Update update) throws TelegramApiException {
        VacancyDto vacancyDto = vacancyService.get(id);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        String vacancyInfo = """
                *Title:* %s
                *Company:* %s
                *Short Description:* %s
                *Salary:* %s
                *Link:* [%s](%s)
                """.formatted(
                        escapeMarkdownReservedChars(vacancyDto.getTitle()),
                        escapeMarkdownReservedChars(vacancyDto.getCompany()),
                        escapeMarkdownReservedChars(vacancyDto.getShortDescription()),
                        escapeMarkdownReservedChars(vacancyDto.getLongDescription()),
                        vacancyDto.getSalary().isBlank() ? "Not specified" : escapeMarkdownReservedChars(vacancyDto.getSalary()),
                        "Click here for more details",
                        escapeMarkdownReservedChars(vacancyDto.getLink())
        );
        sendMessage.setText(vacancyInfo);
        sendMessage.setParseMode(ParseMode.MARKDOWNV2);
        sendMessage.setReplyMarkup(getBackToVacanciesMenu());
        execute(sendMessage);
    }
    private String escapeMarkdownReservedChars(String text){
        return text
                .replace("-","\\-")
                .replace("_","\\_")
                .replace("*","\\*")
                .replace("[","\\[")
                .replace("]","\\]")
                .replace("(","\\(")
                .replace(")","\\)")
                .replace("~","\\~")
                .replace("`","\\`")
                .replace(">","\\>")
                .replace("#","\\#")
                .replace("+","\\+")
                .replace(".","\\.")
                .replace("!","\\!");
    }

    private ReplyKeyboard getBackToVacanciesMenu(){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton backToVacanciesMenu = new InlineKeyboardButton();
        backToVacanciesMenu.setText("Back to vacancies menu");
        backToVacanciesMenu.setCallbackData("backToVacancies");
        row.add(backToVacanciesMenu);

        InlineKeyboardButton backToStartMenuButton = new InlineKeyboardButton();
        backToStartMenuButton.setText("Back to start menu");
        backToStartMenuButton.setCallbackData("backToStartMenu");
        row.add(backToStartMenuButton);

        InlineKeyboardButton getChatGptButton = new InlineKeyboardButton();
        getChatGptButton.setText("Get cover letter");
        getChatGptButton.setUrl("https://chat.openai.com/");
        row.add(getChatGptButton);

        return new InlineKeyboardMarkup(List.of(row));
    }

    private void showJuniorVacancies(Update update) throws TelegramApiException {
        //update.getMessage().getChatId() - ID of the user who writes to the bot
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose vacancy:");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getJuniorVacanciesMenu());
        execute(sendMessage);
        lastShownVacancyLevel.put(chatId, "junior");
    }
    private void showMiddleVacancies(Update update) throws TelegramApiException {
        //update.getMessage().getChatId() - ID of the user who writes to the bot
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose vacancy:");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getMiddleVacanciesMenu());
        execute(sendMessage);
        lastShownVacancyLevel.put(chatId, "middle");
    }
    private void showSeniorVacancies(Update update) throws TelegramApiException {
        //update.getMessage().getChatId() - ID of the user who writes to the bot
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please choose vacancy:");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getSeniorVacanciesMenu());
        execute(sendMessage);
        lastShownVacancyLevel.put(chatId, "senior");
    }
    private ReplyKeyboard getVacanciesMenu(List <VacancyDto> vacancies){
        List <InlineKeyboardButton> row = new ArrayList<>();
        for (VacancyDto element : vacancies){
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(element.getTitle());
            vacancyButton.setCallbackData("vacancyID=" + element.getId());
            row.add(vacancyButton);
        }
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }
    private ReplyKeyboard getJuniorVacanciesMenu() {
        List <VacancyDto> vacancies = vacancyService.getJuniorVacancies();
        return getVacanciesMenu(vacancies);
    }
    private ReplyKeyboard getMiddleVacanciesMenu() {
        List <VacancyDto> vacancies = vacancyService.getMiddleVacancies();
        return getVacanciesMenu(vacancies);
    }
    private ReplyKeyboard getSeniorVacanciesMenu() {
        List <VacancyDto> vacancies = vacancyService.getSeniorVacancies();
        return getVacanciesMenu(vacancies);
    }
    private void handleStartCommand(Update update){
        String text = update.getMessage().getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Welcome to vacancies bot! Please choose the title:");
        sendMessage.setReplyMarkup(getStartMenu());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboard getStartMenu() {
        //creating keyboard with buttons

        //callbackData - message we receive from telegram when user clicks the button
        //to understand which button user pressed
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton junior = new InlineKeyboardButton();
        junior.setText("Junior"); //button's text
        junior.setCallbackData("show vacancies for junior");
        row.add(junior); //adding button to our button's list

        InlineKeyboardButton middle = new InlineKeyboardButton();
        middle.setText("Middle");
        middle.setCallbackData("show vacancies for middle");
        row.add(middle);

        InlineKeyboardButton senior = new InlineKeyboardButton();
        senior.setText("Senior");
        senior.setCallbackData("show vacancies for senior");
        row.add(senior);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    @Override
    public String getBotUsername() {
        return "VS vacancies bot";
    }
}
