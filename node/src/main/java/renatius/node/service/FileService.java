package renatius.node.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import renatius.node.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message externalMessage);
}
