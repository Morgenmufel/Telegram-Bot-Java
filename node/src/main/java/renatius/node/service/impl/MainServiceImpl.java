package renatius.node.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.objects.User;
import renatius.node.dao.AppUserDAO;
import renatius.node.entity.*;
import renatius.node.exceptions.UploadFileException;
import renatius.node.dao.RawDataDAO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import renatius.node.service.FileService;
import renatius.node.service.MainService;
import renatius.node.service.ParsingService;
import renatius.node.service.ProducerService;
import renatius.node.entity.enums.ServiceCommands;

import java.io.IOException;
import java.util.ArrayList;

import static renatius.node.entity.enums.UserState.BASIC_STATE;
import static renatius.node.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static renatius.node.entity.enums.ServiceCommands.*;


@Service
@Slf4j
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;

    private final  ProducerService producerService;

    private final AppUserDAO appUserDAO;

    private final FileService fileService;

    private final ParsingService parsingService;


    public MainServiceImpl(RawDataDAO rawDataDAO ,ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, ParsingService parsingService) {
        this.producerService = producerService;
        this.rawDataDAO = rawDataDAO;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.parsingService = parsingService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var output = " ";
        var serviceCommand = ServiceCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)){
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
        try{
            AppDocument doc = fileService.processDoc(update.getMessage());
            //TODO добавить генерацию ссылки для скачивания
            var answer = "Документ успешно загружен " +
                    "Ссылка для скачивания: ...";
            sendMessage(chatId, answer);

        }catch (UploadFileException e){
            log.error(e.toString());
            String error = "Загрузка файла не удалась";
            sendMessage(chatId, error);
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
            return;
        }
        try{
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            //TODO добавить генерацию ссылки для скачивания
            var answer = "Фото успешно загружено! Ссылка для скачивания ...";
            sendMessage(chatId, answer);
        }catch (UploadFileException e){
            log.error(e.toString());
            var answer = "К сожалению не удалось загрузить фото =(";
            sendMessage(chatId, answer);
        }
        //TODO добавить сохранение фото
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
        if (REGISTRATION.equals(ServiceCommands.fromValue(text))){
            //TODO реализовать регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(ServiceCommands.fromValue(text))){
           return help();
        } else if (START.equals(ServiceCommands.fromValue(text))){
            return "Вас приветствует бот Ренатиуса Великолепный. " +
                    "Когда он правил ещё не придумали слово пиздатый, " +
                    "поэтому его назвали великолепный. Чтобы узнать список великолепных комманд введите /help";
        } else if (TICKETS.equals(ServiceCommands.fromValue(text))) {
            try {
                Document doc = parsingService.getHTMLDocument("https://www.ticketpro.by/");
                Elements elements = parsingService.getTickets(doc);
                ArrayList<UpComingEvents> tickets = parsingService.getUpComingEvents(elements);
                String str = "Предстоящие события: ";
                for (UpComingEvents event : tickets) {
                    str += "\n" + "\n" + event.toString();
                }
                return str;
            }catch (IOException e){
                log.error(e.toString());
            }
        } else{
            log.error("Unknown command :" + text);
            return "Неизвестная команда. Попробуйте ввести /cancel и начать сначала.";
        }
        return null;
    }

    public String help() {
        return "Список доступных комманд\n" +
                "/cancel - отмена выполнения текущей команды\n" +
                "/registration - регистрация пользователя\n" +
                //TODO доработать остальные команды
                "/start - начать\n" +
                "/tickets - узнать ближайшие события\n" +
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
    @Override
    public Elements getTags(String html) throws IOException {
        Document doc = parsingService.getHTMLDocument(html);
        Elements ticketBoxes = parsingService.getTickets(doc);
        for (Element ticketBox : ticketBoxes) {
            System.out.println(ticketBox.html());
        }
        return ticketBoxes;
    }


    public ArrayList<UpComingEvents> getEvents(Elements elements){
        return parsingService.getUpComingEvents(elements);
    }
}
