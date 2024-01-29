package org.bot0ff.dto;

import lombok.Data;

@Data
public class NavigateResponse {
    String info;
    int status;

    public NavigateResponse(String info) {
        this.info = info;
        this.status = 3;
    }
}
