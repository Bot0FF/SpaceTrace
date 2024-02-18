package org.bot0ff.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.entity.unit.UnitFightEffect;

@Converter(autoApply = true)
public class UnitJsonSubjectToFightEffectConverter implements AttributeConverter<UnitFightEffect, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UnitFightEffect unitFightEffect) {
        if(unitFightEffect == null) return null;
        try {
            return objectMapper.writeValueAsString(unitFightEffect);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnitFightEffect convertToEntityAttribute(String unitJson) {
        if(unitJson == null) return null;
        try {
            return objectMapper.readValue(unitJson, UnitFightEffect.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
