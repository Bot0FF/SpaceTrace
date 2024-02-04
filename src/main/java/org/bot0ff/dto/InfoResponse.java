package org.bot0ff.dto;

import lombok.Data;

@Data
public class InfoResponse {
    private String info;
    private int status;

    public InfoResponse(String info) {
        this.info = info;
        this.status = 2;
    }
}
