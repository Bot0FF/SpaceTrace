package org.bot0ff.model;

import lombok.Data;

@Data
public class NavigateResponse {
    int status;

    public NavigateResponse() {
        this.status = 3;
    }
}
