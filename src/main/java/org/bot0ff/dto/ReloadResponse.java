package org.bot0ff.dto;

import lombok.Data;

@Data
public class ReloadResponse {
    String info;
    int status;

    public ReloadResponse(String info) {
        this.info = info;
        this.status = 3;
    }
}
