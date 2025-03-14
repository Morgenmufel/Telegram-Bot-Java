package renatius.node.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import renatius.node.dao.AppDocumentDAO;
import renatius.node.dao.BinaryContentDAO;
import renatius.node.entity.AppDocument;
import renatius.node.service.FileService;


@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${bot.token}")
    private String token;

    @Value("${service.file_info.url}")
    private String fileInfoUrl;

    @Value("${service.file_storage.url}")
    private String fileStorageUrl;

    private final AppDocumentDAO appDocumentDAO;

    private final BinaryContentDAO binaryContentDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
    }

    @Override
    public AppDocument processDoc(Message externalMessage) {
        
    }
}
