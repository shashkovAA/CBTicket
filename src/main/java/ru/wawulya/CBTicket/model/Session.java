package ru.wawulya.CBTicket.model;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.util.UUID;


public class Session {
    public UUID uuid;

    public UUID getUuid() {

        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }
}
