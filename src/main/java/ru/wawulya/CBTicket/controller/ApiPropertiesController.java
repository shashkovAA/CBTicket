package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyFieldsEnum;
import ru.wawulya.CBTicket.enums.PropertyNameEnum;
import ru.wawulya.CBTicket.error.ApiError;
import ru.wawulya.CBTicket.error.BadRequestException;
import ru.wawulya.CBTicket.error.NotFoundException;
import ru.wawulya.CBTicket.model.Property;
import ru.wawulya.CBTicket.model.RequestResult;
import ru.wawulya.CBTicket.model.Session;
import ru.wawulya.CBTicket.modelDAO.PropertyDAO;
import ru.wawulya.CBTicket.service.DataService;
import ru.wawulya.CBTicket.service.FileStorageService;
import ru.wawulya.CBTicket.utility.Utils;
import ru.wawulya.CBTicket.modelCache.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiPropertiesController {


    private FileStorageService fileStorageService;
    private DataService dataService;
    private Properties properties;
    private Utils utils;

    @Autowired
    public ApiPropertiesController(FileStorageService fileStorageService, DataService dataService, Properties properties, Utils utils) {
        this.fileStorageService = fileStorageService;
        this.dataService = dataService;
        this.properties = properties;
        this.utils = utils;
    }

    @GetMapping(value = "/property/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Property> getAllProperties(HttpServletRequest request, HttpServletResponse response) {
        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        List<Property> propList = properties.getAllProperties();

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                "",
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());
        return propList;
    }

    @GetMapping(value = "/properties/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property getPropertyById(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) {
        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        //Property property = dataService.findPropertyById(id);
        Property property = properties.getPropertyById(id);

        if (property == null)
            throw new NotFoundException(getSession().getUuid(),  request.getMethod(), request.getRequestURI(), "Not found");

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(),property),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return property;
    }

    @GetMapping(value = "/property/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property getPropertyByName(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "name") String name) {
        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        //Property property = dataService.findPropertyByName(name);
        Property property = properties.getPropertyByName(name);

        if (property == null) {
            throw new NotFoundException(getSession().getUuid(),  request.getMethod(), request.getRequestURI(), "Not found");
        }

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                utils.createJsonStr(getSession().getUuid(),property),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return property;
    }

    @PostMapping(value = "/property/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property addProperty(HttpServletRequest request, HttpServletResponse response, @RequestBody Property property) {
        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        Property prop = dataService.getPropertyDataService().addProperty(property);

        if (prop == null)
            throw new BadRequestException(getSession().getUuid(),  request.getMethod(), request.getRequestURI(), "Check log file for details.");
        else
            properties.addProperty(prop);

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                utils.createJsonStr(getSession().getUuid(),prop),
                utils.createJsonStr(getSession().getUuid(),prop),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return prop;
    }

    @PutMapping(value = "/property/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property updateProperty(HttpServletRequest request, HttpServletResponse response,@RequestBody Property property) {
        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        properties.updateProperty(property);

        dataService.getPropertyDataService().updateProperty(property);

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                utils.createJsonStr(getSession().getUuid(),property),
                utils.createJsonStr(getSession().getUuid(),property),
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());

        return property;
    }

    @DeleteMapping(value = "/property/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteProperty(HttpServletRequest request, HttpServletResponse response,@PathVariable("id") Long id) {
        log.info(getSession().getUuid() + " | REST " + request.getMethod() + " " + request.getRequestURI());

        properties.deleteProperty(id);

        dataService.getPropertyDataService().deleteProperty(id);

        dataService.getLogService().saveLog(
                getSession().getUuid().toString(),
                request.getRemoteUser(),
                LogLevel.INFO,
                request.getMethod(),
                request.getRequestURI(),
                "",
                "",
                String.valueOf(response.getStatus()),
                request.getRemoteAddr());
    }

    @PostMapping("/properties/upload")
    public RequestResult uploadFileToDB(@RequestParam("file") MultipartFile file) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="POST";
        String logApiUrl = "/api/properties/upload?file=" + file.getOriginalFilename();
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //Добавляем список properties в БД
        Path fileStoragePath = fileStorageService.uploadFile(sessionId, file);
        List<PropertyDAO> propertyDAOs = dataService.getPropertyDataService().insertPropertyToDBv2(sessionId, fileStoragePath);

        //Добавляем список properties в кэш-модель
        propertyDAOs.forEach(p->properties.addProperty(p.toProperty()));

        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "","","200 OK");
        RequestResult result = new RequestResult("Success","Upload "+propertyDAOs.size()+" records from property file ["+file.getOriginalFilename()+"]");
        return result;
    }

    @GetMapping("/properties/export")
    public void exportCSV2(HttpServletResponse response) throws Exception {

        //set file name and content type
        String filename = "properties.csv";

        response.setContentType("text/csv;charset=UTF8");
        //response.setContentType("charset=UTF8");

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");

        List<Property> list = dataService.getPropertyDataService().findAllProperties();

        String delimiterStr = properties.getPropertyByName(PropertyNameEnum.EXPORT_DATA_DELIMITER).getValue();
        char delimiter = delimiterStr.replace("\"", "").replace("\'", "").charAt(0);

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.newFormat(delimiter).withRecordSeparator('\n').withHeader(
                                                                                PropertyFieldsEnum.ID,
                                                                                PropertyFieldsEnum.NAME,
                                                                                PropertyFieldsEnum.VALUE,
                                                                                PropertyFieldsEnum.DESCRIPTION,
                                                                                PropertyFieldsEnum.EDITABLE))) {
            for (Property prop : list) {
                log.info(prop.toString());
                csvPrinter.printRecord(Arrays.asList(prop.getName(), prop.getValue(), prop.getDescription(), prop.isEditable()));
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiError sendNotFoundResponse(NotFoundException except) {

        log.error(except.getSessionId()+ " | Error " + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.WARN,except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404 NOT FOUND");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiError sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.getLogService().saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400 BAD REQUEST");
        log.error(apiError.toString());

        return apiError;
    }

    @Lookup
    public Session getSession() {
        return null;
    }


}
