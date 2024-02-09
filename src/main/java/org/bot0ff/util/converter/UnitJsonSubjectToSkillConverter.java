package org.bot0ff.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.bot0ff.entity.unit.UnitArmor;
import org.bot0ff.entity.unit.UnitSkill;

@Converter(autoApply = true)
public class UnitJsonSubjectToSkillConverter implements AttributeConverter<UnitSkill, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(UnitSkill unitSkill) {
        try {
            return objectMapper.writeValueAsString(unitSkill);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnitSkill convertToEntityAttribute(String unitSkill) {
        try {
            return objectMapper.readValue(unitSkill, UnitSkill.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
