package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.ForbiddenOperationException;
import ru.practicum.shareit.exception.NotFoundException;

@SpringBootTest
public class ErrorHandlerTest {
    ErrorHandler errorHandler = new ErrorHandler();
    ErrorResponse response;

    @Test
    public void conditionsNotMetExceptionWhenThrownShouldBeReturnedErrorResponseWithProperErrorMessage() {
        ErrorResponse response = errorHandler.handleConditionsNotMet(new ConditionsNotMetException("ConditionsNotMetException"));
        Assertions.assertEquals("ConditionsNotMetException", response.getError());
    }

    @Test
    public void forbiddenOperationExceptionWhenThrownShouldBeReturnedErrorResponseWithProperErrorMessage() {
        response = errorHandler.handleForbiddenOperation(new ForbiddenOperationException("ForbiddenOperationException"));
        Assertions.assertEquals("ForbiddenOperationException", response.getError());

    }

    @Test
    public void notFoundExceptionWhenThrownShouldBeReturnedErrorResponseWithProperErrorMessage() {
        response = errorHandler.handleNotFound(new NotFoundException("NotFoundException"));
        Assertions.assertEquals("NotFoundException", response.getError());
    }

    @Test
    public void whenThrownDataIntegrityViolationExceptionWithMessageContainingText_idx_users_email_ShouldBeReturnedErrorResponseWithProperErrorMessage() {
        response = errorHandler.handleEmailConstraintViolation(new DataIntegrityViolationException("idx_users_email"));
        Assertions.assertEquals("Указанный email уже используется", response.getError());
    }

    @Test
    public void whenThrownDataIntegrityViolationExceptionWithMessageNotContainingText_idx_users_email_ShouldBeReturnedErrorResponseWithProperErrorMessage() {
        response = errorHandler.handleEmailConstraintViolation(new DataIntegrityViolationException("EmailConstraintViolationException"));
        Assertions.assertEquals("Произошла непредвиденная ошибка", response.getError());
    }
}
