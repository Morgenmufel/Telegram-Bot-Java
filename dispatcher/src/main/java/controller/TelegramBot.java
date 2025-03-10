package controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class.getName());
    private UpdateController updateController;


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }
    @PostConstruct
    public void init(){
        updateController.registerBot(this);
    }

    public void sendMessage(SendMessage message) {
        if(message != null){
            try{
                execute(message);
            }catch (TelegramApiException e){
                logger.info(e.getMessage());
            }
        }



    }
}
