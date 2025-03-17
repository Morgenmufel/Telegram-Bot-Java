package renatius.node.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import renatius.node.dao.AppDocumentDAO;
import renatius.node.dao.AppPhotoDAO;
import renatius.node.dao.BinaryContentDAO;
import renatius.node.entity.AppDocument;
import renatius.node.entity.AppPhoto;
import renatius.node.entity.BinaryContent;
import renatius.node.exceptions.UploadFileException;
import renatius.node.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


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

    private final AppPhotoDAO appPhotoDAO;

    private final BinaryContentDAO binaryContentDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public AppDocument processDoc(Message externalMessage)  {
        Document telegramDoc = externalMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode().equals(HttpStatus.OK)){
            try{
                BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
                AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
                return appDocumentDAO.save(transientAppDoc);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        return null;
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //TODO пока что обрабатывается только 1 фото
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(0);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode().equals(HttpStatus.OK)){
            try{
                BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
                AppPhoto transientAppDoc = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
                return appPhotoDAO.save(transientAppDoc);
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        return null;
    }


    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) throws JSONException {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private static String getFilePath(ResponseEntity<String> response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }



    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramField(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramField(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUrl = fileStorageUrl.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try{
            urlObj = new URL(fullUrl);
        }catch (MalformedURLException e){
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try(InputStream inputStream = urlObj.openStream()){
            return inputStream.readAllBytes();
        }catch (IOException e){
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUrl,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }
}
