package ru.wawulya.CBTicket.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.JpaPropertyRepository;
import ru.wawulya.CBTicket.enums.PropertyNameEnum;
import ru.wawulya.CBTicket.model.Property;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.modelDAO.PropertyDAO;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PropertyDataService {

    private JpaPropertyRepository propRepo;
    private Properties properties;

    @Autowired
    public PropertyDataService(JpaPropertyRepository propRepo, Properties properties) {
        this.propRepo = propRepo;
        this.properties = properties;

        initProperties();
    }

    public void initProperties() {
        log.info("Initializing properties ...");

        Optional<PropertyDAO> oPropertyDAO = null;

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.AVAILABILITY);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.AVAILABILITY+"] not found in database and will be added with value [IfYouGotMeThenItIsWork]");
            propRepo.save(new PropertyDAO(PropertyNameEnum.AVAILABILITY, "IfYouGotMeThenItIsWork", "Параметр проверки работоспособности API и доступности БД", false, false));
        }

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.AFTER_ANSWER_NUMBER);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.AFTER_ANSWER_NUMBER+"] not found in database and will be added with value [0]");
            propRepo.save(new PropertyDAO(PropertyNameEnum.AFTER_ANSWER_NUMBER, "0", "Номер VDN приложения AEP, после ответа оператора", true, false));
        }

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.MAX_CALLBACK_ATTEMPTS);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.MAX_CALLBACK_ATTEMPTS+"] not found in database and will be added with value [1]");
            propRepo.save(new PropertyDAO(PropertyNameEnum.MAX_CALLBACK_ATTEMPTS, "1", "Количество попыток обратного вызова", true, false));
        }

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.PHANTOM_NUMBER_POOL);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.PHANTOM_NUMBER_POOL+"] not found in database and will be added with value [0]");
            propRepo.save(new PropertyDAO(PropertyNameEnum.PHANTOM_NUMBER_POOL, "0", "Пул фантомых номеров для callBack-a", true, false));
        }

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.DEFAULT_ORIGINATOR);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.DEFAULT_ORIGINATOR+"] not found in database and will be added with value [0]");
            propRepo.save(new PropertyDAO(PropertyNameEnum.DEFAULT_ORIGINATOR, "0", "Номер по умолчанию, на который инициируется вызов до callback-a", true, false));
        }

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.DEFAULT_MESSAGE);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.DEFAULT_MESSAGE+"] not found in database and will be added with value [default.wav]");
            propRepo.save(new PropertyDAO(PropertyNameEnum.DEFAULT_MESSAGE, "default.wav", "Сообщение по умолчанию для исходящей стороны (оператор, КМ)", true, false));
        }

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.CALLBACK_ATTEMPTS_TIMEOUT);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.CALLBACK_ATTEMPTS_TIMEOUT+"] not found in database and will be added with value [1]");
            propRepo.save(new PropertyDAO(PropertyNameEnum.CALLBACK_ATTEMPTS_TIMEOUT, "1", "Таймаут в минутах между попытками calback-a, если соединения не произошло", true, false));
        }

        oPropertyDAO = propRepo.findByNameIgnoringCase(PropertyNameEnum.EXPORT_DATA_DELIMITER);
        if (!oPropertyDAO.isPresent()) {
            log.debug("Property ["+PropertyNameEnum.EXPORT_DATA_DELIMITER+"] not found in database and will be added with value [';']");
            propRepo.save(new PropertyDAO(PropertyNameEnum.EXPORT_DATA_DELIMITER, "';'", "Символ-разделитель, используемый при экспорте данных в csv-файл.", true, false));
        }

        List<Property> propList = findAllProperties();
        log.debug("Loaded " + propList.size()+ " properties from DB.");

        propList.forEach(property-> properties.addProperty(property));
        log.debug(properties.toString());
    }

    public Property addProperty(Property property) {
        PropertyDAO propertyDAO;
        Property prop = null;
        try {
            propertyDAO = propRepo.save(property.toPropertyDAO());
            prop = propertyDAO.toProperty();
        } catch (Exception except)   {
            log.error("Inserting property with non-unique name is not allowed!");
        }
        return prop;
    }

    public List<PropertyDAO> insertPropertyToDBv2(UUID sessionId , Path path) {

        List<PropertyDAO> parsedPropList = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(path)) {

            CsvToBean csvToBean = new CsvToBeanBuilder(reader)
                    .withType(PropertyDAO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // iterate through users
            for (PropertyDAO prop : (Iterable<PropertyDAO>) csvToBean) {

                System.out.println("Name: " + prop.getName());
                System.out.println("Value: " + prop.getValue());
                System.out.println("Description: " + prop.getDescription());

                parsedPropList.add(prop);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        log.info(String.valueOf(parsedPropList.size()));

        List<PropertyDAO> listInsertedPropertyDOA = new ArrayList<>();

        parsedPropList.forEach( p -> {
            try {
                log.info(p.getName());
                propRepo.save(p);
                listInsertedPropertyDOA.add(p);
            }
            catch (Exception ex) {
                log.error(ex.getMessage());
            }
        });

        return listInsertedPropertyDOA;
    }

    public Page<PropertyDAO> findAll(Pageable pageRequest) {
        return propRepo.findAll(pageRequest);
    }

    public List<Property> findAllProperties() {
        return propRepo.findAll().stream().map(p->p.toProperty()).collect(Collectors.toList());
    }

    public Property findPropertyById(Long id) {
        Property property = null;
        Optional<PropertyDAO> oPropertyDAO = propRepo.findById(id);

        if (oPropertyDAO.isPresent())
            property = oPropertyDAO.get().toProperty();

        return property;
    }

    public Property findPropertyByName(String name) {

        Property property = null;

        Optional<PropertyDAO> oPropertyDAO = propRepo.findByNameIgnoringCase(name);
        if (oPropertyDAO.isPresent()) {
            property = oPropertyDAO.get().toProperty();
        }
        return property;
    }

    public PropertyDAO addProperty(PropertyDAO propertyDAO) {

        return propRepo.save(propertyDAO);
    }

    @Async
    public Property updateProperty(Property property) {

        PropertyDAO propertyDAO = null;
        Optional<PropertyDAO> oPropertyDAO = propRepo.findById(property.getId());

        if (oPropertyDAO.isPresent()) {

            propertyDAO = oPropertyDAO.get();

            propertyDAO.setName(property.getName());
            propertyDAO.setValue(property.getValue());
            propertyDAO.setDescription(property.getDescription());
            propertyDAO.setEditable(property.isEditable());
            log.info(propertyDAO.toString());
        }
        propertyDAO = propRepo.save(propertyDAO);

        return propertyDAO.toProperty();
    }

    @Async
    public Long deleteProperty(Long id) {
        Long propId = 0L;

        Optional<PropertyDAO> oPropertyDAO = propRepo.findById(id);

        if (oPropertyDAO.isPresent()) {
            propId = oPropertyDAO.get().getId();
            propRepo.deleteById(propId);
        }

        return propId;
    }

}
