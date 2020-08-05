package ru.wawulya.CBTicket.model;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class Session {
    public UUID uuid;

    public UUID getUuid() {

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }
}
