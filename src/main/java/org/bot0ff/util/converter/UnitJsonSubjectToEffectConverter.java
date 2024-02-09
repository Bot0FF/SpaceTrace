package org.bot0ff.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.entity.unit.UnitEffect;

import java.util.List;

@Converter(autoApply = true)
public class UnitJsonSubjectToEffectConverter implements AttributeConverter<List<UnitEffect>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<UnitEffect> unitFightEffect) {
        try {
            return objectMapper.writeValueAsString(unitFightEffect);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UnitEffect> convertToEntityAttribute(String unitJson) {
        try {
            return objectMapper.readValue(unitJson, new TypeReference<List<UnitEffect>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
