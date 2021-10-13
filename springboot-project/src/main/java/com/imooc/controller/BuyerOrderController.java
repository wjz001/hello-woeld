package com.imooc.controller;

import com.imooc.VO.ResultVO;
import com.imooc.converter.OrderForm2OrderDTOConverter;
import com.imooc.dataobject.OrderMaster;
import com.imooc.dataobject.ProductInfo;
import com.imooc.dataobject.UserInfo;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.form.OrderForm;
import com.imooc.repository.OrderMasterRepository;
import com.imooc.service.BuyerService;
import com.imooc.service.OrderService;
import com.imooc.service.ProductService;
import com.imooc.service.UserInfoService;
import com.imooc.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SqMax on 2018/3/20.
 */
@RestController
@RequestMapping("/buyer/order")
@Slf4j
public class BuyerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderMasterRepository orderMasterRepository;


    //    //创建订单
//    @PostMapping("/create")
//    public ResultVO<Map<String,String>> create(@Valid OrderForm orderForm ,
//                                               BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            log.error("【创建订单】参数不正确，orderForm={}",orderForm);
//            throw new SellException(ResultEnum.PARAM_ERROR.getCode(),
//                    bindingResult.getFieldError().getDefaultMessage());
//        }
//        OrderDTO orderDTO= OrderForm2OrderDTOConverter.convert(orderForm);
//        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
//            log.error("【创建订单】购物车不能为空");
//            throw new SellException(ResultEnum.CART_EMPTY);
//        }
//        OrderDTO createResult=orderService.create(orderDTO);
//
//        Map<String,String> map=new HashMap<>();
//        map.put("orderId",createResult.getOrderId());
//
//        return ResultVOUtil.success(map);
//
//    }
    //创建订单
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@RequestParam("openid") String openid, @RequestParam("productId") String productId) {
        if (StringUtils.isEmpty(openid)) {
            log.error("【查询订单列表】openid为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        if (StringUtils.isEmpty(productId)) {
            log.error("【查询订单列表】productId为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        Date date=new Date();
        UserInfo userInfo = userInfoService.findUserInfoByOpenid(openid);
        ProductInfo productInfo = productService.findOne(productId);
        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setBuyerOpenid(userInfo.getOpenid());
        orderMaster.setBuyerName(productInfo.getProductId());
        orderMaster.setOrderAmount(productInfo.getProductPrice());
        orderMaster.setPayStatus(0);
        orderMaster.setCreateTime(date);
        orderMaster.setUpdateTime(date);
        orderMaster.setOrderStatus(1);
        orderMaster.setBuyerAddress(productInfo.getProductDescription());
        orderMaster.setBuyerPhone(userInfo.getName());
        orderMasterRepository.save(orderMaster);
        return ResultVOUtil.success(orderMaster);

    }

    //订单列表
    @GetMapping("/list")
    public ResultVO<List<OrderDTO>> list(@RequestParam("openid") String openid,
                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (StringUtils.isEmpty(openid)) {
            log.error("【查询订单列表】openid为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        PageRequest request = new PageRequest(page, size);
        Page<OrderDTO> orderDTOPage = orderService.findList(openid, request);

        return ResultVOUtil.success(orderDTOPage.getContent());
//        return ResultVOUtil.success();
    }

    //订单详情
    @GetMapping("/detail")
    public ResultVO<OrderDTO> detail(@RequestParam("openid") String openid,
                                     @RequestParam("orderId") String orderId) {

        OrderDTO orderDTO = buyerService.findOrderOne(openid, orderId);
        return ResultVOUtil.success(orderDTO);
    }

    //取消订单
    @PostMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid") String openid,
                           @RequestParam("orderId") String orderId) {
        buyerService.cancelOrder(openid, orderId);
        return ResultVOUtil.success();
    }

    //支付状态
    @PostMapping("/payStatus")
    public ResultVO payStatus(@RequestParam("openid") String openid,
                              @RequestParam("orderId") String orderId) {
        List<OrderMaster> list = orderMasterRepository.findByBuyerOpenidAndBuyerNameAndPayStatus(openid, orderId, 1);
        String flag = "true";
        if (!list.isEmpty()) {
            flag = "false";
        }
        return ResultVOUtil.success(flag);
    }
    //支付状态
    @PostMapping("/PushpayStatus")
    public ResultVO PushpayStatus(
                              @RequestParam("orderId") String orderId) {
        OrderMaster orderMaster = orderMasterRepository.findOne(orderId);
        orderMaster.setPayStatus(1);
        orderMasterRepository.save(orderMaster);
        log.error("主动修改PushpayStatus--------+++++++++++++++++++++++++++++++=-----------修改orderMaster支付状态");
        return ResultVOUtil.success();
    }

    //消息推送
    @PostMapping("/advice1")
    public ResultVO userStatus1(@RequestParam("openid") String openid
    ) {
        userInfoService.updataByOpenId1(openid);
        return ResultVOUtil.success();
    }

    //消息推送
    @PostMapping("/advice2")
    public ResultVO userStatus2(@RequestParam("openid") String openid
    ) {
        userInfoService.updataByOpenId2(openid);
        return ResultVOUtil.success();
    }

    //订阅页面
    @PostMapping("/userAdvice")
    public ResultVO userAdvice(@RequestParam("openid") String openid
    ) {
        UserInfo userInfo = userInfoService.findUserInfoByOpenid(openid);

        return ResultVOUtil.success(userInfo);
    }
}
