package com.maplestory.onecard.service.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PlayCardInVo  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户昵称不能为空")
    private String userName;

    @NotBlank(message = "卡不能为空")
    private String cardId;

    @NotBlank(message = "房间号不能为空")
    private String roomNumber;

    private String chooseId;
}
