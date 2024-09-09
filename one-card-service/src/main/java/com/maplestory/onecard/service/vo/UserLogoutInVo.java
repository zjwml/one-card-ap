package com.maplestory.onecard.service.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;

@Data
@Component
public class UserLogoutInVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 算了，用户名没有用，只要昵称
     */
    @NotBlank(message = "用户名称不能为空")
    private String userName;

    /**
     * 房间号
     */
    @NotBlank(message = "房间号不能为空")
    @Pattern(regexp = "^\\d{4}$", message = "房间号只能为4位数字")
    private String roomNumber;
}
