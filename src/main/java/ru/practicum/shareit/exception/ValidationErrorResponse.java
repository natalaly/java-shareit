package ru.practicum.shareit.exception;

import java.util.List;
import java.util.Map;

public record ValidationErrorResponse(Map<String, List<String>> error) {

}
