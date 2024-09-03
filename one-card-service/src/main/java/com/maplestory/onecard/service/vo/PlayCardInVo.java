package com.maplestory.onecard.service.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PlayCardInVo  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户昵称不能为空")
    private String userName;

    @NotBlank(message = "卡Id不能为空")
    @Pattern(regexp = "^\\d+$", message = "卡Id只能为数字")
    private String cardId;

    @NotBlank(message = "房间号不能为空")
    @Pattern(regexp = "^\\d{4}$", message = "房间号只能为4位数字")
    private String roomNumber;

    private String chooseId;
}
