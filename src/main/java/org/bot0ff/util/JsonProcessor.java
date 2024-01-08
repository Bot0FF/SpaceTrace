package org.bot0ff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bot0ff.dto.response.FightBuilder;
import org.bot0ff.dto.response.MainBuilder;

public class JsonProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJsonMainResponse(MainBuilder mainBuilder) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mainBuilder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJsonFightResponse(FightBuilder fightBuilder) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(fightBuilder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
