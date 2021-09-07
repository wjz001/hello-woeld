package com.imooc.controller;

import com.imooc.VO.ResultVO;
import com.imooc.dataobject.OrderMaster;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.repository.OrderMasterRepository;
import com.imooc.service.OrderService;
import com.imooc.service.PayService;
import com.imooc.utils.JsonUtil;
import com.imooc.utils.ResultVOUtil;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SqMax on 2018/3/26.
 */
@RestController
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayService payService;
    @Autowired
    OrderMasterRepository orderMasterRepository;

    @GetMapping("/pay")
    public ModelAndView index(@RequestParam("openid") String openid,
                              @RequestParam("orderId") String orderId,
                              @RequestParam("returnUrl") String returnUrl,
                              Map<String, Object> map) {
        log.info("openid={}", openid);
        //1.查询订单
//        String orderId="1234563";
        OrderDTO orderDTO = orderService.findOne(orderId);
        if (orderDTO == null) {
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }
        //2.发起支付
        orderDTO.setBuyerOpenid(openid);
        PayResponse payResponse = payService.create(orderDTO);

        map.put("payResponse", payResponse);
        map.put("returnUrl", returnUrl + "?productIcon=" + orderDTO.getBuyerPhone());

        return new ModelAndView("pay/create", map);
    }

    @RequestMapping("/create")
    @ResponseBody
    public ResultVO create(@RequestParam("orderId") String orderId,
                           @RequestParam("returnUrl") String returnUrl,
                           Map<String,Object> map){
        //1.查询订单
        OrderDTO orderDTO = orderService.findOne(orderId);
        if (orderDTO == null) {
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }
        List <OrderMaster> list= orderMasterRepository.findByBuyerOpenidAndBuyerNameAndPayStatus
                (orderDTO.getBuyerOpenid(), orderDTO.getBuyerName(), 1);
        if (!list.isEmpty()){
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);

        }
        //2.发起支付
        PayResponse payResponse = payService.create(orderDTO);

        map.put("payResponse", JsonUtil.toJson(payResponse));
        map.put("returnUrl", returnUrl + "?productIcon=" + orderDTO.getBuyerPhone());

//        return new ModelAndView("pay/create",map);

        ResultVO resultVO= ResultVOUtil.success(payResponse);

        return resultVO;
//        return "redirect:"+returnUrl+"?"+payResponse;

    }

    /**
     * 微信异步通知
     * @param notifyData
     */
    @PostMapping("/notify")
    public ModelAndView notify(@RequestBody String notifyData){

        log.info("notifyData:{}",notifyData);
        payService.notify(notifyData);

        //返回给微信处理结果
//        String string="";
        return new ModelAndView("pay/success");
    }

}
