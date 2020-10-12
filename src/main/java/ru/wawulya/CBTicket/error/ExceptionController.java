package ru.wawulya.CBTicket.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    private ApiError sendNotFoundResponse(NotFoundException except) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, except.getMessage());
        return apiError;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ApiError sendBadRequestResponse(BadRequestException except) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, except.getMessage());
        return apiError;
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    private ApiError sendForbiddenResponse(ForbiddenException except) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, except.getMessage());
        return apiError;
    }
}
