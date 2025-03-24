package renatius.node.service;

import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.objects.Update;
import renatius.node.entity.UpComingEvents;

import java.io.IOException;
import java.util.ArrayList;

public interface MainService {
    void processTextMessage(Update update);
    void processDocMessage(Update update);
    void processPhotoMessage(Update update);
    Elements getTags(String html) throws IOException;
    ArrayList<UpComingEvents> getEvents (Elements events) throws IOException;
}
