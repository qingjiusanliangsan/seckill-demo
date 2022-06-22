package com.ucas.seckill.vo;

import com.ucas.seckill.pojo.TUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qingjiusanliangsan
 * create 2022-06-09-23:23
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private TUser tUser;

    private GoodsVo goodsVo;

    private int secKillStatus;

    private int remainSeconds;

}