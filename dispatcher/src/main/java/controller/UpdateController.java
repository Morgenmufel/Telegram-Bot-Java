package controller;

import controller.utils.MessageUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import service.UpdateProducer;

import static renatius.commonrabbitmq.model.RabbitQueue.*;


@Component
@Slf4j
public class UpdateController {
    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }
    public void registerBot(TelegramBot bot) {
        this.telegramBot = bot;
    }

    public void processUpdate(Update update) {
        if(update == null){
            log.error("Received is null");
            return;
        }
        if(update.hasMessage()){
            distributeMessageByType(update);
        }
        else {
            log.error("Received is null");
        }
    }

    public void distributeMessageByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        }
        else if (message.hasDocument()) {
            processDocumentMessage(update);
        }

        else if(message.hasPhoto()){
            processPhotoMessage(update);
        }
        else {
            sendUnsupportedTypeOfMessage(update);
        }
    }

    public void sendUnsupportedTypeOfMessage(Update update) {
        var message = messageUtils.generateMessageWithText(update,
                "Неподдерживаемый тип сообщений.");
        setView(message);
    }

    public void setView(SendMessage message) {
        telegramBot.sendMessage(message);
    }

    public void setFileIsReceivedView(Update update) {
        var message = messageUtils.generateMessageWithText(update,
                "Файл получен! Идёт обработка...");
        setView(message);
    }

    public void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE,update);
        setFileIsReceivedView(update);
    }

    public void processDocumentMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE,update);
        setFileIsReceivedView(update);
    }

    public void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

}
