package renatius.node.service.impl;

import org.telegram.telegrambots.meta.api.objects.User;
import renatius.node.dao.AppUserDAO;
import renatius.node.entity.AppUser;
import renatius.node.repository.RawDataDAO;
import renatius.node.entity.RawData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import renatius.node.service.MainService;
import renatius.node.service.ProducerService;

import static renatius.node.entity.enums.UserState.BASIC_STATE;


@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;

    private final  ProducerService producerService;

    private final AppUserDAO appUserDAO;


    public MainServiceImpl(RawDataDAO rawDataDAO ,ProducerService producerService, AppUserDAO appUserDAO) {
        this.producerService = producerService;
        this.rawDataDAO = rawDataDAO;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var message = update.getMessage();
        var telegramUser = message.getFrom();
        var appUser = findOrSaveAppUser(telegramUser);

        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText("Я в мейн сервисе");
        producerService.produceAnswer(sendMessage);

    }

    public AppUser findOrSaveAppUser(User telegramUser){
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistentAppUser == null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    //TODO изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .userState(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser;
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
