package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.error.MyFileNotFoundException;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.service.DataService;
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
@Controller
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
    @ResponseBody
    public List<LogFile> getLogFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

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

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(), logs),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return logs;
    }

    @GetMapping("/logs/download/{fileName:.+}")
    @ResponseBody
    public ResponseEntity downloadLogFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String fileName) {

        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

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

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(), resource),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Lookup
    public Session getSession() {
        return null;
    }
}
