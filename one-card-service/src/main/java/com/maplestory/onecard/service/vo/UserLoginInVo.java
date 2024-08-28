package com.maplestory.onecard.service.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;

@Data
@Component
public class UserLoginInVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */

    private String userId;

    /**
     * 算了，用户名没有用，只要昵称
     */
    @NotBlank(message = "用户名称不能为空")
    private String userName;

    /**
     * 房间号
     */
    @NotBlank(message = "房间号不能为空")
    @Size(min = 4, max = 4, message = "房间号长度必须为4")
    private String roomNumber;
}
