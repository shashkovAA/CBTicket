package ru.wawulya.CBTicket.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import ru.wawulya.CBTicket.enums.LogLevel;
import ru.wawulya.CBTicket.enums.PropertyFieldsEnum;
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

    @Autowired
    private FileStorageService fileStorageService;

    private DataService dataService;
    private Properties properties;
    private Utils utils;

    @Autowired
    public ApiPropertiesController(DataService dataService, Properties properties, Utils utils) {
        this.dataService = dataService;
        this.properties = properties;
        this.utils = utils;
    }

/*   @GetMapping(value = "/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Property> getAllProperties(@RequestParam ("page") int page,
                                           @RequestParam ("size") int size,
                                           @PageableDefault (sort = {"id"}, direction = Sort.Direction.DESC, size = 5) Pageable pageRequest
                                            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                            @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
                                            ) {

        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/properties";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //log.info("pageNo : " + pageNo);
        //log.info("pageSize : " + pageSize);
        //log.info("param : " + param);

        Pageable pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));


        //log.info(String.valueOf("pageRequest.getPageNumber() : " + pageRequest.getPageNumber()));
        //log.info(String.valueOf("pageRequest.getPageSize() : " + pageRequest.getPageSize()));

        //Page<PropertyDAO> pageList = dataService.findAllProperties(pageRequest);

        List<Property> propList = dataService.findAllProperties(pageRequest);

        //propList.forEach(p->log.info(p.toString()));
        pageList.getContent().forEach( p-> log.info(p.toString()));
        log.info(String.valueOf("page.getTotalElements() : " + pageList.getTotalElements()));
        log.info(String.valueOf("page.getTotalPages() : " + pageList.getTotalPages()));


        dataService.saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,propList),"200 OK");
        return propList;
    }*/

    @GetMapping(value = "/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Property> getAllProperties() {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/properties";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //List<Property> propList = dataService.findAllProperties();
        List<Property> propList = properties.getAllProperties();
        log.debug(properties.toString());

        dataService.saveLog(sessionId.toString(), LogLevel.INFO,logMethod,logApiUrl,"",utils.createJsonStr(sessionId,propList),"200 OK");
        return propList;
    }

    @GetMapping(value = "/properties/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property getPropertyById(@PathVariable("id") Long id) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/properties/ " + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //Property property = dataService.findPropertyById(id);
        Property property = properties.getPropertyById(id);

        if (property == null)
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Not found");

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "",utils.createJsonStr(sessionId,property),"200 OK");
        return property;
    }

    @GetMapping(value = "/properties/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property getPropertyByName(@RequestHeader Map<String, String> headers, @RequestParam(name = "name") String name) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="GET";
        String logApiUrl = "/api/properties/find?name=" + name;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);
        headers.forEach((key, value) -> {
            log.info(String.format("Header '%s' = %s", key, value));
        });

        //Property property = dataService.findPropertyByName(name);
        Property property = properties.getPropertyByName(name);

        if (property == null) {
            throw new NotFoundException(sessionId, logMethod, logApiUrl, "Not found");
        }

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "",utils.createJsonStr(sessionId,property),"200 OK");
        return property;
    }

    @PostMapping(value = "/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property addProperty(@RequestBody Property property) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="POST";
        String logApiUrl = "/api/properties";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        Property prop = dataService.addProperty(property);

        if (prop == null)
            throw new BadRequestException(sessionId, logMethod, logApiUrl, "Check log file for details.");
        else
            properties.addProperty(prop);

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,prop),utils.createJsonStr(sessionId,prop),"200 OK");
        return prop;
    }

    @PutMapping(value = "/properties", produces = MediaType.APPLICATION_JSON_VALUE)
    public Property updateProperty(@RequestBody Property property) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="PUT";
        String logApiUrl = "/api/properties";
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        properties.updateProperty(property);

        dataService.updateProperty(property);
        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, utils.createJsonStr(sessionId,property),utils.createJsonStr(sessionId,property),"200 OK");
        return property;
    }

    @DeleteMapping(value = "/properties/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteProperty(@PathVariable("id") Long id) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="DELETE";
        String logApiUrl = "/api/properties/" + id;
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        properties.deleteProperty(id);

        dataService.deleteProperty(id);
        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "","","200 OK");
    }

    @PostMapping("/properties/upload")
    public RequestResult uploadFileToDB(@RequestParam("file") MultipartFile file) {
        UUID sessionId = new Session().getUuid();
        String logMethod ="POST";
        String logApiUrl = "/api/properties/upload?file=" + file.getOriginalFilename();
        log.info(sessionId + " | REST " + logMethod + " " + logApiUrl);

        //Добавляем список properties в БД
        Path fileStoragePath = fileStorageService.storeFile(sessionId, file);
        List<PropertyDAO> propertyDAOs = dataService.insertPropertyToDBv2(sessionId, fileStoragePath);

        //Добавляем список properties в кэш-модель
        propertyDAOs.forEach(p->properties.addProperty(p.toProperty()));

        dataService.saveLog(sessionId.toString(),LogLevel.INFO,logMethod,logApiUrl, "","","200 OK");
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

        List<Property> list = dataService.findAllProperties();

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader(
                                                                                PropertyFieldsEnum.NAME,
                                                                                PropertyFieldsEnum.VALUE,
                                                                                PropertyFieldsEnum.DESCRIPTION,
                                                                                PropertyFieldsEnum.EDITABLE,
                                                                                PropertyFieldsEnum.REMOVABLE))) {
            for (Property prop : list) {
                log.info(prop.toString());
                csvPrinter.printRecord(Arrays.asList(prop.getName(), prop.getValue(), prop.getDescription(), prop.isEditable(), prop.isRemovable()));
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
        dataService.saveLog(except.getSessionId().toString(),LogLevel.WARN,except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "404 NOT FOUND");
        log.error(apiError.toString());

        return apiError;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ApiError sendBadRequestResponse(BadRequestException except) {

        log.error(except.getSessionId()+ " | Error :" + except.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, except.getSessionId(), except.getMessage());
        dataService.saveLog(except.getSessionId().toString(),LogLevel.ERROR, except.getMethod(),except.getApiUrl(), "",utils.createJsonStr(except.getSessionId(), apiError), "400 BAD REQUEST");
        log.error(apiError.toString());

        return apiError;
    }


}
