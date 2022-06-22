package com.ucas.seckill.vo;

import com.ucas.seckill.pojo.TOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qingjiusanliangsan
 * create 2022-06-11-16:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeatilVo {

    private TOrder tOrder;

    private GoodsVo goodsVo;
}
