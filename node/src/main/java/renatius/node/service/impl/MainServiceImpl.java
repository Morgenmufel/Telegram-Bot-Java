package renatius.node.service.impl;

import lombok.extern.slf4j.Slf4j;
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
import static renatius.node.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static renatius.node.service.ServiceCommands.*;


@Service
@Slf4j
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
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var output = " ";
        if (CANCEL.equals(text)){
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)){
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)){
            //TODO добавить при внедрении обработки email
        } else{
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Попробуйте ввести /cancel и повторить попытку";
        }
        
        var chatId = update.getMessage().getChatId();
        sendMessage(chatId, output);


    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            
        }

        //TODO добавить сохранение документа
        var answer = "Документ успешно загружен.";
        sendMessage(chatId, answer);
    }



    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){

        }

        //TODO добавить сохранение документа
        var answer = "Фото успешно загружен.";
        sendMessage(chatId, answer);
    }


    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();
        if (!appUser.getIsActive()){
            var error = "Ошибка! Попробуйте зарегистрироваться или подтвердить аккаунт";
            sendMessage(chatId, error);
            return true;
        } else if(!BASIC_STATE.equals(userState)){
            var error = "Отмените текущую команду /cancel и попробуйте снова";
            sendMessage(chatId, error);
            return true;
        }
        return false;
    }


    private void sendMessage(Long chatId, String output) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }



    private String processServiceCommand(AppUser appUser, String text) {
        if (REGISTRATION.equals(text)){
            //TODO реализовать регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(text)){
           return help();
        } else if (START.equals(text)){
            return "Вас приветствует бот Ренатиуса Великолепный. " +
                    "Когда он правил ещё не придумали слово пиздатый, " +
                    "поэтому его назвали великолепный. Чтобы узнать список великолепных комманд введите /help";
        } else{
            log.error("Unknown command :" + text);
            return "Неизвестная команда. Попробуйте ввести /cancel и начать сначала.";
        }
    }

    public String help() {
        return "Список доступных комманд\n" +
                "/cancel - отмена выполнения текущей команды\n" +
                "/registration - регистрация пользователя\n" +
                //TODO доработать остальные команды
                "/start - начать\n" +
                "/tickets - узнать ближайшие афиши\n" +
                "/help - узнать все команды";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
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
