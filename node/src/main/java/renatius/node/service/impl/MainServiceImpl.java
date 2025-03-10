package renatius.node.service.impl;

import org.telegram.telegrambots.meta.api.objects.User;
import renatius.node.repository.RawDataDAO;
import renatius.node.entity.RawData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import renatius.node.service.MainService;
import renatius.node.service.ProducerService;


@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;

    private final  ProducerService producerService;


    public MainServiceImpl(RawDataDAO rawDataDAO ,ProducerService producerService) {
        this.producerService = producerService;
        this.rawDataDAO = rawDataDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
    }


    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .update(update)
                .build();
        rawDataDAO.save(rawData);

        var sendMessage = new SendMessage();
        var message = update.getMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Данные сохранены");
        producerService.produceAnswer(sendMessage);
    }
}
