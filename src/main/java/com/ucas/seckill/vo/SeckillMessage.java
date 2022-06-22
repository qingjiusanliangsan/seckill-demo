package com.ucas.seckill.vo;

import com.ucas.seckill.pojo.TUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qingjiusanliangsan
 * create 2022-06-22-22:31
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private TUser tUser;

    private Long goodsId;
}
