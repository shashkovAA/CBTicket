package ru.wawulya.CBTicket.model;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
public class ApiMethodList {

    private static List<ApiMethodInfo> listApiPropertyMethodsInfo;
    private static List<ApiMethodInfo> listApiCBTicketMethodsInfo;

    static {
        /* API Properties methods Info*/
        listApiPropertyMethodsInfo = new ArrayList<>();
        listApiPropertyMethodsInfo.add(new ApiMethodInfo("GET", "/api/properties", "-", "List<Property>", "Property (id, name, value, description)"));
        listApiPropertyMethodsInfo.add(new ApiMethodInfo("GET", "/api/properties/{id}", "-", "Property ", "Property (id, name, value, description)"));
        listApiPropertyMethodsInfo.add(new ApiMethodInfo("POST", "/api/properties", "PropertyForm", "Property", "PropertyForm (id, name, value, description), Property (id, name, value, description)"));
        listApiPropertyMethodsInfo.add(new ApiMethodInfo("PUT", "/api/properties", "PropertyForm", "Property", "PropertyForm (id, name, value, description), Property (id, name, value, description)"));
        listApiPropertyMethodsInfo.add(new ApiMethodInfo("DELETE", "/api/properties/{id}", "-", "-", ""));

        /* API CBTicket methods Info*/
        listApiCBTicketMethodsInfo = new ArrayList<>();
        listApiCBTicketMethodsInfo.add(new ApiMethodInfo("POST", "/api/ticket/add", "JSON = {cbNumber, cbDate, cbOriginator, cbUrl, ucidOld, cbType, cbSource}", "Ticket", "Метод добавления заявки на обратный вызов. Допустимые параметры перечислены в столбце RequestBody. Параматр cbNumber является обязательным."));
        listApiCBTicketMethodsInfo.add(new ApiMethodInfo("GET", "/api/ticket/{id}", "-", "Ticket", "Метод возвращает запрос на обратный вызов по его ID."));
        listApiCBTicketMethodsInfo.add(new ApiMethodInfo("GET", "/api/ticket/find?number", "-", "List<Ticket>", "Метод возвраает список всех запросов на обратный вызов по номеру телефона."));
        listApiCBTicketMethodsInfo.add(new ApiMethodInfo("PUT", "/api/ticket/cancel/{id}", "-", "-", "Метод отменяет запрос на обратный вызов по его ID."));
        listApiCBTicketMethodsInfo.add(new ApiMethodInfo("DLETE", "/api/ticket/delete/{id}", "-", "-", "Метод удаляет из БД запрос на обратный вызов по его ID."));
        listApiCBTicketMethodsInfo.add(new ApiMethodInfo("PUT", "/api/ticket/update/", "Ticket", "-", "Метод обновляет данные для запроса на обратный вызов в БД."));
        listApiCBTicketMethodsInfo.add(new ApiMethodInfo("GET", "/api/ticket/job?count", "-", "List<Ticket>", "Метод возвращает список из <count> запросов на обратный вызов, удовлетворяющим критериям поиска для взятия в работу."));

    }

    public List<ApiMethodInfo> getListApiPropertyMethodsInfo() {
        return listApiPropertyMethodsInfo;
    }

    public List<ApiMethodInfo> getListApiCBTicketMethodsInfo() {
        return listApiCBTicketMethodsInfo;
    }


}
