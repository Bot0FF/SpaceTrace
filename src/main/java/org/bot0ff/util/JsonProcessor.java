package org.bot0ff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(ResponseBuilder responseBuilder) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(responseBuilder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
