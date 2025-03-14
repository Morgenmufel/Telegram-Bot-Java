package renatius.node.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import renatius.node.service.ConsumerService;
import renatius.node.service.MainService;

import static renatius.commonrabbitmq.model.RabbitQueue.*;


@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {


    private final MainService mainService;


    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }


    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.info("NODE: Text message is receieved");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdates(Update update) {
        log.info("NODE: Doc message is receieved");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdates(Update update) {
        log.info("NODE: Photo message is receieved");
        mainService.processPhotoMessage(update);
    }
}
