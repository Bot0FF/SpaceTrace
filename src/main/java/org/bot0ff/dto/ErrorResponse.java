package org.bot0ff.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String info;
    private int status;

    public ErrorResponse(String info) {
        this.info = info;
        this.status = 2;
    }
}
