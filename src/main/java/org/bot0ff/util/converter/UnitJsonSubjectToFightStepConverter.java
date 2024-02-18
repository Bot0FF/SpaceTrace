package org.bot0ff.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.unit.UnitFightStep;

import java.util.List;

@Converter(autoApply = true)
public class UnitJsonSubjectToFightStepConverter implements AttributeConverter<List<UnitFightStep>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<UnitFightStep> fightStep) {
        if(fightStep == null) return null;
        try {
            return objectMapper.writeValueAsString(fightStep);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UnitFightStep> convertToEntityAttribute(String fightStep) {
        if(fightStep == null) return null;
        try {
            return objectMapper.readValue(fightStep, new TypeReference<List<UnitFightStep>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
