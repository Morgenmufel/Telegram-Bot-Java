package renatius.restservice.service.impl;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import renatius.node.dao.AppDocumentDAO;
import renatius.node.dao.AppPhotoDAO;
import renatius.node.entity.AppDocument;
import renatius.node.entity.AppPhoto;
import renatius.node.entity.BinaryContent;
import renatius.restservice.service.FileServiceRest;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


@Service
public class FileServiceRestImpl implements FileServiceRest {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiceRestImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }


    @Override
    public AppDocument getDocument(String DocId) {
        //TODO добавить дешифрование хэш-строки
        var id = Long.parseLong(DocId);
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String PhotoId) {
        //TODO добавить дешифрование хэш-строки
        var id = Long.parseLong(PhotoId);
        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        //TODO добавить генерацию названий для временных файлов
        try{
            File temp = File.createTempFile("tempFile", ".bin");
            temp.deleteOnExit();
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(temp);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
