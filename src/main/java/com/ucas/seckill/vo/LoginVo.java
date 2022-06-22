package com.ucas.seckill.vo;


import com.ucas.seckill.validator.IsMobile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 登录传参
 *
 * @author qingjiusanliangsan
 * create 2022-06-05-12:33
 * @ClassName: LoginVo
 */
@Getter
@Setter
@Data
@ToString
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;

}
