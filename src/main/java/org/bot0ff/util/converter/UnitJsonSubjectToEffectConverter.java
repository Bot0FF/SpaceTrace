package org.bot0ff.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.entity.unit.UnitEffect;

import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true)
public class UnitJsonSubjectToEffectConverter implements AttributeConverter<Map<Long, UnitEffect>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Long, UnitEffect> unitFightEffect) {
        try {
            return objectMapper.writeValueAsString(unitFightEffect);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Long, UnitEffect> convertToEntityAttribute(String unitJson) {
        try {
            return objectMapper.readValue(unitJson, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
