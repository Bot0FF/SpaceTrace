package org.bot0ff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bot0ff.dto.Response;

public class JsonProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(Response response) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
