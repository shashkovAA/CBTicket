package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.error.MyFileNotFoundException;
import ru.wawulya.CBTicket.model.ConfigFile;
import ru.wawulya.CBTicket.model.LogFile;
import ru.wawulya.CBTicket.model.RequestResult;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.modelDAO.PropertyDAO;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.service.FileStorageService;
import ru.wawulya.CBTicket.service.RestartService;
import ru.wawulya.CBTicket.utility.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping(value = "/api")
//@PropertySource(value = "file:/temp/cbticket/uploads/external.properties", ignoreResourceNotFound = true)
public class ApiSettingsController {

    private RestartService restartService;
    private FileStorageService fileStorageService;
    private DataService dataService;
    private Utils utils;

    @Value("${testStr}")
    private String testStr;

    @Autowired
    public ApiSettingsController(FileStorageService fileStorageService, DataService dataService, RestartService restartService, Utils utils) {
        this.fileStorageService = fileStorageService;
        this.dataService = dataService;
        this.restartService = restartService;
        this.utils = utils;
    }

    @PostMapping("/settings/restart")
    public RequestResult restart() {
        restartService.restartApp();
        RequestResult result = new RequestResult("Success","Restart application is successfully.");
        return result;
    }

    @GetMapping("/settings/test")
    public String test() {
        return testStr;
    }

    @GetMapping(value = "/settings/files", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConfigFile> getLogFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {

        List<File> files = null;

        try (Stream<Path> paths = Files.walk(fileStorageService.getConfigStorageLocation())) {
            files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }

        List<ConfigFile> configFiles = new ArrayList<>();

        files.forEach(f-> {
            configFiles.add(new ConfigFile(f.getName(), utils.convertMilsToDate(f.lastModified())));
        });

        return configFiles;
    }

    @PostMapping("/settings/upload")
    public RequestResult uploadFileToDB(HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file) {

        //Добавляем список properties в БД
        Path fileStoragePath = fileStorageService.storeConfigFile(request.getRequestedSessionId(), file);

        RequestResult result = new RequestResult("Success","Upload " + file.getOriginalFilename());
        return result;
    }

    @GetMapping("/settings/download/{fileName:.+}")
    public ResponseEntity downloadLogFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String fileName) {

        String fileBasePath = fileStorageService.getConfigStorageLocation().toString() + "\\"+ fileName;
        Path path = Paths.get(fileBasePath);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
            if(!resource.exists()) {
                throw new MyFileNotFoundException("File "+ fileBasePath +" not found");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping(value = "/settings/{filename}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RequestResult deleteProperty(HttpServletRequest request, HttpServletResponse response, @PathVariable("filename") String filename) {

        boolean deleteResult = fileStorageService.deleteFile(request.getRequestedSessionId(), filename);
        RequestResult result = null;

        if (deleteResult) {
            result = new RequestResult("Success", "Delete  " + filename);

        } else
            throw new MyFileNotFoundException("File not found " + filename);

        return result;
    }

}
