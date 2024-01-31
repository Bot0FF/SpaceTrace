package org.bot0ff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.dto.unit.UnitEffect;

@Converter(autoApply = true)
public class UnitJsonConverter implements AttributeConverter<UnitEffect, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UnitEffect unitEffect) {
        try {
            return objectMapper.writeValueAsString(unitEffect);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnitEffect convertToEntityAttribute(String unitJson) {
        try {
            return objectMapper.readValue(unitJson, UnitEffect.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
