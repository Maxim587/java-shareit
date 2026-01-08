package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;

@SpringBootTest
public class ValidationTest {

    @Test
    public void test() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleConditionsNotMet(new ConditionsNotMetException("ConditionsNotMetException"));
        Assertions.assertEquals("ConditionsNotMetException", response.getError());

        response = errorHandler.handleEmailConstraintViolation(new Throwable("EmailConstraintViolationException"));
        Assertions.assertEquals("Произошла непредвиденная ошибка", response.getError());

        response = errorHandler.handleEmailConstraintViolation(new DataIntegrityViolationException("idx_users_email"));
        Assertions.assertEquals("Указанный email уже используется", response.getError());

        response = errorHandler.handleForbiddenOperation(new ForbiddenOperationException("ForbiddenOperationException"));
        Assertions.assertEquals("ForbiddenOperationException", response.getError());

        response = errorHandler.handleNotFound(new NotFoundException("NotFoundException"));
        Assertions.assertEquals("NotFoundException", response.getError());
    }


}
