package ru.wawulya.CBTicket.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.wawulya.CBTicket.data.*;
import ru.wawulya.CBTicket.enums.*;
import ru.wawulya.CBTicket.model.*;
import ru.wawulya.CBTicket.modelCache.Properties;
import ru.wawulya.CBTicket.modelCache.Users;
import ru.wawulya.CBTicket.modelDAO.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Users users;
    private Properties properties;

    private JpaCompletionCodeRepository compCodeRepo;
    private JpaPropertyRepository propRepo;
    private JpaTicketParamsRepository paramsRepo;
    private JpaTicketRepository ticketRepo;
    private JpaApiLogtRepository logRepo;
    private JpaUserRepository userRepo;

    @Autowired
    public DataService(JpaCompletionCodeRepository compCodeRepo,
                       JpaPropertyRepository propRepo,
                       JpaTicketParamsRepository paramsRepo,
                       JpaTicketRepository ticketRepo,
                       JpaApiLogtRepository logRepo,
                       JpaUserRepository userRepo,
                       Users users,
                       Properties properties) {
        this.compCodeRepo = compCodeRepo;
        this.propRepo = propRepo;
        this.paramsRepo = paramsRepo;
        this.ticketRepo = ticketRepo;
        this.logRepo = logRepo;
        this.userRepo = userRepo;
        this.users = users;
        this.properties = properties;

        this.initProperties();
        this.initCompletionCodes();
        this.initUsers();
    }

    //<editor-fold desc="CompletionCode section">

    public void initCompletionCodes() {

        log.info("Initialize completion codes ...");

        Optional<CompletionCodeDAO> oCompletionCodeDAO = null;

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.NOT_CALLED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.NOT_CALLED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.NOT_CALLED, CompCodeSysnameEnum.NOT_CALLED, "Обратный вызов не совершался", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.DIALING);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.DIALING + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.DIALING, CompCodeSysnameEnum.DIALING, "Обратный вызов в процессе набора", false));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.COMPLETED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.COMPLETED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.COMPLETED, CompCodeSysnameEnum.COMPLETED, "Обратный вызов завершен", false));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.CANCELED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.CANCELED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.CANCELED, CompCodeSysnameEnum.CANCELED, "Обратный вызов отменен", false));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.BUSY);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.BUSY + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.BUSY, CompCodeNameEnum.BUSY, "Обратный вызов не завершен. Получен сигнал занятости абонента", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.NETWORK_BUSY);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.NETWORK_BUSY + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.NETWORK_BUSY, CompCodeSysnameEnum.NETWORK_BUSY, "Обратный вызов не завершен. Получен сигнал занятости не сети", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.NO_ANSWER);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.NO_ANSWER + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.NO_ANSWER, CompCodeSysnameEnum.NO_ANSWER, "Обратный вызов не завершен. Нет ответа абонента", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.DROP_BY_OPERATOR);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.DROP_BY_OPERATOR + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.DROP_BY_OPERATOR, CompCodeSysnameEnum.DROP_BY_OPERATOR, "Обратный вызов не завершен. Отменено оператором", true));
        }

        oCompletionCodeDAO = compCodeRepo.findBySysname(CompCodeSysnameEnum.UNDEFINED);
        if (!oCompletionCodeDAO.isPresent()) {
            log.debug("CompletionCode [" + CompCodeSysnameEnum.UNDEFINED + "] not found in database and will be added");
            compCodeRepo.save(new CompletionCodeDAO(CompCodeNameEnum.UNDEFINED, CompCodeSysnameEnum.UNDEFINED, "Статус обратного вызова не определен", true));
        }
    }

    public CompletionCode getOrCreateCompCode(String sysname) {

        CompletionCode completionCode = null;
        Optional<CompletionCodeDAO> oCompletionCodeDAO = compCodeRepo.findBySysname(sysname);

        if (oCompletionCodeDAO.isPresent())
            completionCode = oCompletionCodeDAO.get().toCompletionCode();
        else if (!sysname.isEmpty()) {
            CompletionCodeDAO completionCodeDAO = new CompletionCodeDAO(sysname,sysname,"Auto generated",false);
            completionCode = compCodeRepo.save(completionCodeDAO).toCompletionCode();
        }

        return completionCode;
    }

    public List<CompletionCode> getAllCompCodes() {

        List<CompletionCodeDAO> CompletionCodeDAO = compCodeRepo.findAll();

        List<CompletionCode> completionCodeList = CompletionCodeDAO
                .stream()
                .map(c -> new CompletionCode(c))
                .collect(Collectors.toList());

        return completionCodeList;
    }

    public CompletionCode findCompCodeBySysname (String name) {

        CompletionCode completionCode = null;

        Optional<CompletionCodeDAO> oCompletionCodeDAO = compCodeRepo.findBySysname(name);
        if (oCompletionCodeDAO.isPresent()) {
            completionCode = oCompletionCodeDAO.get().toCompletionCode();
        }
        return completionCode;
    }

    public CompletionCodeDAO findCompCodeDAOBySysname (String name) {

        CompletionCodeDAO completionCodeDAO = null;

        Optional<CompletionCodeDAO> oCompletionCodeDAO = compCodeRepo.findBySysname(name);
        if (oCompletionCodeDAO.isPresent()) {
            completionCodeDAO = oCompletionCodeDAO.get();
        }
        return completionCodeDAO;
    }

    //</editor-fold>


    //<editor-fold desc="Ticket section">

    public TicketDAO save (TicketDAO ticketDAO) {
        return ticketRepo.save(ticketDAO);
    }

    public Ticket getTicketById (Long id) {
        Ticket ticket = null;

        Optional<TicketDAO> oTicketDAO = ticketRepo.findById(id);

        if (oTicketDAO.isPresent())
            ticket = new Ticket(oTicketDAO.get());

        return ticket;
    }

    public List<Ticket> getTicketsByNumber (String number) {
        List<Ticket> tikets = new ArrayList<>();

        List<TicketDAO> ticketDAOs = ticketRepo.findAllByCbNumber(number);

        if(ticketDAOs.size() != 0) {
            tikets = ticketDAOs
                        .stream()
                        .map(t -> new Ticket(t))
                        .collect(Collectors.toList());
        }

        return tikets;
    }

    public List<Ticket> getTicketsForCallBack (Timestamp currentTime, int count) {

        List<Ticket> tikets = new ArrayList<>();

        CompletionCodeDAO complCodeCalling = this.findCompCodeDAOBySysname(CompCodeSysnameEnum.DIALING);

        List<TicketDAO> jobList = ticketRepo.findCallBackList(currentTime)
                .stream()
                .filter(t -> t.getAttemptCount() < t.getTicketParamsDAO().getCbMaxAttempts())
                .limit(count)
                .peek(t -> t.setAttemptCount(t.getAttemptCount() + 1))
                .peek(t -> t.setCompletionCodeDAO(complCodeCalling))
                .collect(Collectors.toList());

        jobList.forEach(ticketDAO -> {
            AttemptDAO attemptDAO = new AttemptDAO(ticketDAO,complCodeCalling);
            ticketDAO.addAttemptDAO(attemptDAO);
            ticketRepo.save(ticketDAO);
        });

        tikets = jobList
                    .stream()
                    .map(t -> new Ticket(t))
                    .collect(Collectors.toList());

        return tikets;
    }

    public Ticket updateTicket(Ticket ticket, Timestamp currentTime) {

        CompletionCodeDAO lastComplCodeDAO = new CompletionCodeDAO(ticket.getLastCompletionCode());

        int lastAttemptIndex = ticket.getAttemptCount() - 1;
        Attempt lastAttempt = ticket.getAttempts().get(lastAttemptIndex);

        TicketDAO ticketDAO = ticketRepo.getOne(ticket.getId());
        ticketDAO.setCompletionCodeDAO(lastComplCodeDAO);

        if (!lastComplCodeDAO.isRecall())
            ticketDAO.setFinished(true);

        if (ticket.getAttemptCount() >= ticket.getTicketParams().getCbMaxAttempts())
            ticketDAO.setFinished(true);
        else {
            //Timestamp curCbDate = ticket.getCbDate();
            Timestamp nextCbDate = currentTime;
            int timeout = ticket.getTicketParams().getCbAttemptsTimeout();
            nextCbDate.setTime(currentTime.getTime() + TimeUnit.MINUTES.toMillis(timeout));

            ticketDAO.setCbDate(nextCbDate);
        }

        AttemptDAO attemptDAO = ticketDAO.getAttemptDAOs().get(lastAttemptIndex);
        attemptDAO.update(lastAttempt);
        attemptDAO.setAttempt_stop(currentTime);
        attemptDAO.setCompletionCodeDAO(lastComplCodeDAO);

        ticketRepo.save(ticketDAO);

        return new Ticket(ticketDAO);
    }

    public Ticket cancelTicketById(Long id) {

        Ticket ticket = null;
        TicketDAO ticketDAO = null;

        CompletionCodeDAO completionCodeDAO = this.findCompCodeDAOBySysname(CompCodeSysnameEnum.CANCELED);

        Optional<TicketDAO> opTicketDAO = ticketRepo.findById(id);

        if (opTicketDAO.isPresent()) {
            ticketDAO = opTicketDAO.get();
            ticketDAO.setCompletionCodeDAO(completionCodeDAO);
            ticketDAO.setFinished(true);
            ticketRepo.save(ticketDAO);

        }

        return new Ticket(ticketDAO);

    }

    public Ticket deleteTicketById(Long id) {

        Ticket ticket = null;

        Optional<TicketDAO> opTicketDAO = ticketRepo.findById(id);

        if (!opTicketDAO.isPresent()) {
            return ticket;
        }

        TicketDAO ticketDAO = opTicketDAO.get();

        if (ticketDAO.isFinished()) {
            ticket = new Ticket(ticketDAO);
            ticketRepo.delete(ticketDAO);
        }

        return ticket;

    }

    public Ticket getOneTicketInDialingState() {

        Ticket ticket = null;

        CompletionCodeDAO complCodeCalling = this.findCompCodeDAOBySysname(CompCodeSysnameEnum.DIALING);

        TicketDAO ticketDAO = ticketRepo.findCallBackListInDialingState(complCodeCalling.getId());

        if (ticketDAO !=null)
            ticket = new Ticket(ticketDAO);

        return ticket;
    }

    public List<Ticket> findAllByCbNumber(String cbNumber) {

        List<TicketDAO> uncompleteTickets= ticketRepo.findAllByCbNumber(cbNumber);
        List<Ticket> tickets = uncompleteTickets.stream().filter(t->!t.isFinished()).map(t->new Ticket(t)).collect(Collectors.toList());

        return tickets;
    }


    //</editor-fold>


    //<editor-fold desc="Properties section">

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

   /* public List<PropertyDAO> insertPropertyToDB(UUID sessionId , Path path) {

        List<PropertyDAO> propertyDAOS = new ArrayList<>();

        int [] indexFieldArray = {-1, -1, -1, -1, -1};

        String readlineFromFile = null;

        int count = 0;

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(path.toString()), "UTF-8"))) {

            while ((readlineFromFile = buffer.readLine()) != null) {

                if (count == 0) {
                    String[] fields = readlineFromFile.split(",");

                    for (int i = 0; i < fields.length ; i++) {

                        log.debug(fields[i].toLowerCase());

                        switch (fields[i].toLowerCase().trim()) {

                            case PropertyFieldsEnum.NAME :      indexFieldArray[i] = 0;
                                break;
                            case PropertyFieldsEnum.VALUE :     indexFieldArray[i] = 1;
                                break;
                            case PropertyFieldsEnum.DESCRIPTION : indexFieldArray[i] = 2;
                                break;
                            case PropertyFieldsEnum.EDITABLE :  indexFieldArray[i] = 3;
                                break;
                            case PropertyFieldsEnum.REMOVABLE : indexFieldArray[i] = 4;
                                break;
                        }

                    }

                    if (indexFieldArray[0] == -1 || indexFieldArray[1] == -1) {
                        throw new IllegalArgumentException("Not enough property fields. Fields [name] and [value] are mandatory.");
                    }

                } else {
                    log.debug(sessionId + " | Parsing property line :" + readlineFromFile);
                    String[] fields = readlineFromFile.split(",");

                    String name = "";
                    String value = "";
                    String description = "";
                    String editable = "true";
                    String removable = "true";

                    for (int i = 0; i < indexFieldArray.length ; i++) {

                        switch (indexFieldArray[i]) {

                            case 0 :    name = fields[i];
                                break;
                            case 1 :    value = fields[i];
                                break;
                            case 2 :    description = fields[i];
                                break;
                            case 3 :    editable = fields[i];
                                break;
                            case 4 :    removable = fields[i];
                                break;
                        }
                    }

                    propertyDAOS.add(new PropertyDAO(
                            name,
                            value,
                            description,
                            Boolean.valueOf(editable),
                            Boolean.valueOf(removable)));
                }

                count++;
            }
        } catch (FileNotFoundException e) {
            log.error(sessionId + " | " + e.getMessage());
        } catch (IOException e) {
            log.error(sessionId + " | " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error(sessionId + " | " + e.getMessage());
        }

        List<PropertyDAO> listInsertedPropertyDOA = new ArrayList<>();

        propertyDAOS.forEach( p -> {
            try {
                propRepo.save(p);
                listInsertedPropertyDOA.add(p);
            }
            catch (Exception ex) {
                log.error(ex.getMessage());
            }
        });

        return listInsertedPropertyDOA;
    }*/

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

   /* public List<Property> findAllProperties(Pageable pageRequest) {
        Page<PropertyDAO> propertyDAOS = propRepo.findAll(pageRequest);
        List<Property > propList = propertyDAOS.stream().map(p ->p.toProperty()).collect(Collectors.toList());
        return propList;
    }*/

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
            propertyDAO.setRemovable(property.isRemovable());
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



    //</editor-fold>


    //<editor-fold desc="Logging section">

    @Async
    public void saveLog(String sessionId, String level, String logMethod, String logApiUrl, String requestBody, String responseBody, String status) {
        logRepo.save(new ApiLogDAO(sessionId, LogLevel.INFO,logMethod,logApiUrl, requestBody,responseBody, status));
    }

    //</editor-fold>

    //<editor-fold desc="User section">

    public void initUsers() {

        log.info("Initializing users ...");

        List<User> userList = getAllUsers();
        log.debug("Loaded " + userList.size()+ " users from DB.");

        userList.forEach(user-> users.addUser(user));
        log.debug(users.toString());

        users.addUser(createDefaultUser());

    }

    public List<User> getAllUsers() {
        List<UserDAO> usersDAO = userRepo.findAll();

        List<User> users = usersDAO
                .stream()
                .map(u -> u.toUser())
                .collect(Collectors.toList());

        return users;
    }

    public User addUser(User user) {
        UserDAO userDAO;
        User usr = null;
        try {
            String encrytedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encrytedPassword);
            userDAO = userRepo.save(user.toUserDAO());
            usr = userDAO.toUser();
        } catch (Exception except) {
            log.error("Inserting user with non-unique username is not allowed!");
        }

        return usr;
    }

    public User createDefaultUser() {
        User defaultUser = new User();
        defaultUser.setId(0L);
        defaultUser.setUsername("partner");
        defaultUser.setPassword("$2a$10$4Qk1ZhxOwlitmE63mljScOx9oKSY9jtMurwYdDV8aRNXV76sLrE2i");
        defaultUser.setEnabled(true);
        return defaultUser;
    }

    @Async
    public User updateUser(User user) {

        UserDAO userDAO= null;
        Optional<UserDAO> oUserDAO = userRepo.findById(user.getId());

        if (oUserDAO.isPresent()) {

            userDAO = oUserDAO.get();

            userDAO.setUsername(user.getUsername());
            userDAO.setPassword(passwordEncoder.encode(user.getPassword()));
            userDAO.setFullname(user.getFullname());
            userDAO.setEnabled(user.isEnabled());

        }
        userDAO = userRepo.save(userDAO);

        return userDAO.toUser();
    }

    @Async
    public Long deleteUser(Long id) {
        Long userId = 0L;

        Optional<UserDAO> oUserDAO = userRepo.findById(id);

        if (oUserDAO.isPresent()) {
            userId = oUserDAO.get().getId();
            userRepo.deleteById(userId);
        }

        return userId;
    }

    //</editor-fold>



}
