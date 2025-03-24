package renatius.restservice.service;

import org.springframework.core.io.FileSystemResource;
import renatius.node.entity.AppDocument;
import renatius.node.entity.AppPhoto;
import renatius.node.entity.BinaryContent;

public interface FileServiceRest {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
