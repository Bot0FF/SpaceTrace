package org.bot0ff.dto;

import lombok.Data;

@Data
public class MistakeResponse {
    private String info;
    private int status;

    public MistakeResponse(String info) {
        this.info = info;
        this.status = 2;
    }
}
