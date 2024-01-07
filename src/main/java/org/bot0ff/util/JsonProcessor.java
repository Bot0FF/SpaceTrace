package org.bot0ff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bot0ff.dto.response.MainBuilder;

public class JsonProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(MainBuilder mainBuilder) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mainBuilder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
