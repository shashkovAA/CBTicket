package ru.wawulya.CBTicket.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ForbiddenException extends RuntimeException {
    private String message;
}
