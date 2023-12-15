package org.bot0ff.dto;

import lombok.Data;

import java.io.Serializable;

public interface UserDTO {
    String getUsername();
    int getX();
    int getY();
    int getHp();
    int getMana();
}
