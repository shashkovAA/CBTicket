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

    @GetMapping(value = "/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Property> getAllProperties() {
        UUID sessionId = getSession().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/properties";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //List<Property> propList = dataService.findAllProperties();
        List<Property> propList = properties.getAllProperties();
        log.debug(properties.toString());

        dataService.getLogService().saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,propList),"200 OK");
        return propList;
    }

    @GetMapping(value = "/properties/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property getPropertyById(@PathVariable("id") Long id) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/properties/ " + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //Property property = dataService.findPropertyById(id);
        Property property = properties.getPropertyById(id);

        if (property == null)
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Not found");

        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "",utils.createJsonStr(sessionId,property),"200 OK");
        return property;
    }

    @GetMapping(value = "/properties/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property getPropertyByName(@RequestParam(name = "name") String name) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/properties/find?name=" + name;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //Property property = dataService.findPropertyByName(name);
        Property property = properties.getPropertyByName(name);

        if (property == null) {
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Not found");
        }

        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "",utils.createJsonStr(sessionId,property),"200 OK");
        return property;
    }

    @PostMapping(value = "/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property addProperty(@RequestBody Property property) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="POST";
        String logApiUrl = "/api/properties";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Property prop = dataService.getPropertyDataService().addProperty(property);

        if (prop == null)
            throw new BadRequestException(sessionId, logMethod, logApiUrl, "Check log file for details.");
        else
            properties.addProperty(prop);

        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,prop),utils.createJsonStr(sessionId,prop),"200 OK");
        return prop;
    }

    @PutMapping(value = "/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property updateProperty(@RequestBody Property property) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="PUT";
        String logApiUrl = "/api/properties";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        properties.updateProperty(property);

        dataService.getPropertyDataService().updateProperty(property);
        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,property),utils.createJsonStr(sessionId,property),"200 OK");
        return property;
    }

    @DeleteMapping(value = "/properties/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteProperty(@PathVariable("id") Long id) {
        UUID sessionId = getSession().getUuid();
        String logMethod ="DELETE";
        String logApiUrl = "/api/properties/" + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        properties.deleteProperty(id);

        dataService.getPropertyDataService().deleteProperty(id);
        dataService.getLogService().saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "","","200 OK");
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
