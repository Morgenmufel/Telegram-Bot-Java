package renatius.node.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import renatius.node.entity.AppDocument;
import renatius.node.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
