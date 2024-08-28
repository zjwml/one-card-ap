package com.maplestory.onecard.service.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class BattleStartInvo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String roomNumber;
}
