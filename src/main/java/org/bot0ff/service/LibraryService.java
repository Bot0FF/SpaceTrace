package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.repository.LibraryRepository;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final JsonProcessor jsonProcessor;

    public String getEntityType(String type) {
        var entities = libraryRepository.findByType(type);
        if(entities.isEmpty()) {
            var response = ResponseBuilder.builder()
                    .content("Список пуст")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдено ни одного типа сущностей в БД: {}", type);
            return jsonProcessor.toJson(response);
        }
        var response = ResponseBuilder.builder()
                .libraries(entities)
                .content(String.valueOf(entities.size()))
                .status(HttpStatus.OK)
                .build();
        return jsonProcessor.toJson(response);
    }

    public String getEntityInfo(String name) {
        var entity = libraryRepository.findByName(name);
        if(entity.isEmpty()) {
            var response = ResponseBuilder.builder()
                    .content("Описание не найдено")
                    .status(HttpStatus.NO_CONTENT)
                    .build();
            log.info("Не найдено описание сущности в БД: {}", name);
            return jsonProcessor.toJson(response);
        }
        var response = ResponseBuilder.builder()
                .content(entity.get().getDescription())
                .status(HttpStatus.OK)
                .build();
        return jsonProcessor.toJson(response);
    }
}
