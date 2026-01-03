package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ConditionsNotMetException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState of(final String state) {
        try {
            return valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConditionsNotMetException("Некорректное значение параметра state: " + state);
        }
    }
}
