package org.bot0ff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bot0ff.entity.Library;
import org.bot0ff.repository.LibraryRepository;
import org.bot0ff.util.JsonProcessor;
import org.bot0ff.util.ResponseBuilder;
import org.bot0ff.util.ResponseStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final JsonProcessor jsonProcessor;

    public String getAllTypes() {
        var allItems = Map.of(
                "Существа", "/api/library/enemy",
                "Ресурсы", "/api/library/resource",
                "Сражения", "/api/library/battle",
                "Развитие", "/api/library/evolution"
        );
        var response = ResponseBuilder.builder()
                .content(allItems)
                .status(ResponseStatus.SUCCESS);

        return jsonProcessor.toJson(allItems);
    }

    public String getTypeInfo(String type) {
        List<Library> selectItem = libraryRepository.findLibraryByType(type);
        Map<String, String> types = new HashMap<>();
        for(Library item: selectItem) {
            types.put(String.valueOf(item.getId()), item.getDescription());
        }
        var response = ResponseBuilder.builder()
                .content(types)
                .status(ResponseStatus.SUCCESS);
        return jsonProcessor.toJson(response);
    }
}
