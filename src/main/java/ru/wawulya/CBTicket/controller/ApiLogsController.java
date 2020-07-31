package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.error.MyFileNotFoundException;
import ru.wawulya.CBTicket.model.LogFile;
import ru.wawulya.CBTicket.model.Property;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.utility.Utils;

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
public class ApiLogsController {

    private Utils utils;
    private DataService dataService;

    @Autowired
    public ApiLogsController(DataService dataService, Utils utils) {
        this.dataService = dataService;
        this.utils = utils;
    }

    @GetMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LogFile> getLogFiles() throws IOException {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/logs";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        List<File> files = null;

        try (Stream<Path> paths = Files.walk(Paths.get("logs"))) {
            files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }

        List<LogFile> logs = new ArrayList<>();

        files.forEach(f-> {
            logs.add(new LogFile(f.getName(), utils.humanReadableByteCountBin(f.length()), utils.convertMilsToDate(f.lastModified())));
        });

        dataService.saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,logs),"200 OK");
        return logs;
    }

    @GetMapping("/logs/download/{fileName:.+}")
    public ResponseEntity downloadLogFile(@PathVariable String fileName) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/logs/download/" + fileName;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //TODO Предусмотреть путь не через hardcode
        String fileBasePath = "logs/";
        Path path = Paths.get(fileBasePath + fileName);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
            if(!resource.exists()) {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        dataService.saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,resource),"200 OK");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
