package com.ucas.seckill.vo;

import com.ucas.seckill.pojo.TGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author qingjiusanliangsan
 * create 2022-06-06-16:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends TGoods {

    /**
     * 秒杀价格
     **/
//    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    /**
     * 剩余数量
     **/
//    @ApiModelProperty("剩余数量")
    private Integer stockCount;

    /**
     * 开始时间
     **/
//    @ApiModelProperty("开始时间")
    private Date startDate;

    /**
     * 结束时间
     **/
//    @ApiModelProperty("结束时间")
    private Date endDate;


}
