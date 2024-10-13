package ru.practicum.shareit.error;

import java.util.List;

public record ValidationErrorResponse(List<String> error) {

}
