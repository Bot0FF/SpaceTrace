package org.bot0ff.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(Object o) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//    public Object fromJson(String json) {
//        return gson.fromJson(json, Object.class);
//    }
//
//    public String getElement(String json, String element) {
//        var jsonElement = JsonParser.parseString(json);
//        return jsonElement.getAsJsonObject().get(element).getAsString();
//    }
}
