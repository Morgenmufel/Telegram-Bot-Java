package renatius.restservice.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import renatius.node.entity.BinaryContent;
import renatius.restservice.service.FileServiceRest;


@RestController
public class FileController {

    private final FileServiceRest fileService;
    public FileController(FileServiceRest fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id){
        var doc = fileService.getDocument(id);
        //TODO сделать ControllerAdvice
        if (doc == null) {
            System.out.println("я тут АЛООООООООООООО");
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = doc.getBinaryContent();
        var fileSystemResources = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResources == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .header("Content-disposition", "attachment; filename= " + doc.getDocName())
                .body(fileSystemResources);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id){
        var photo = fileService.getPhoto(id);
        //TODO сделать ControllerAdvice
        if (photo == null) {
            System.out.println("я тут АЛООООООООООООО");
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = photo.getBinaryContent();
        var fileSystemResources = fileService.getFileSystemResource(binaryContent);
        if (fileSystemResources == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResources);
    }
}
