package org.bot0ff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.entity.UnitJson;

@Converter(autoApply = true)
public class UnitJsonConverter implements AttributeConverter<UnitJson, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UnitJson unitJson) {
        try {
            return objectMapper.writeValueAsString(unitJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnitJson convertToEntityAttribute(String unitJson) {
        try {
            return objectMapper.readValue(unitJson, UnitJson.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
