package org.bot0ff.util.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.entity.unit.UnitArmor;

@Converter(autoApply = true)
public class UnitJsonSubjectToArmorConverter implements AttributeConverter<UnitArmor, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UnitArmor unitArmor) {
        if(unitArmor == null) return null;
        try {
            return objectMapper.writeValueAsString(unitArmor);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnitArmor convertToEntityAttribute(String unitArmor) {
        if(unitArmor == null) return null;
        try {
            return objectMapper.readValue(unitArmor, UnitArmor.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}
