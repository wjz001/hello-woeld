package com.imooc.service;

import com.imooc.VO.ResultVO;
import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.OrderDTO;

/**
 * 推送消息
 * Created by SqMax on 2018/4/2.
 */
public interface PushMessageService {

    /**
     * 订单状态变更消息
     * @param orderDTO
     */
    void orderStatus(OrderDTO orderDTO);
    /**
     * 订单发布消息
     * @param productInfo
     */
    void orderPush(ProductInfo productInfo);
    /**
     * 订单发布消息
     * @param productInfo
     */
    void orderPushOther(ProductInfo productInfo);

    public  boolean judgeIsFollow(String openid,String access_token);
    public  String getAccessTokes();
}
