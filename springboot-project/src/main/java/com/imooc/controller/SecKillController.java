package com.imooc.controller;

import com.imooc.VO.ResultVO;
import com.imooc.dataobject.UserInfo;
import com.imooc.dto.OrderDTO;
import com.imooc.dto.PartnerDTO;
import com.imooc.service.SecKillService;
import com.imooc.service.UserInfoService;
import com.imooc.utils.ResultVOUtil;
import com.lly835.bestpay.rest.type.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by 廖师兄
 * 2017-08-06 23:16
 */
@RestController
@RequestMapping("/skill")
@Slf4j
public class SecKillController {

    @Autowired
    private SecKillService secKillService;

    @Autowired
    private UserInfoService userInfoService;
    /**
     * 查询秒杀活动特价商品的信息
     * @param productId
     * @return
     */
    @GetMapping("/query/{productId}")
    public String query(@PathVariable String productId)throws Exception
    {
        return secKillService.querySecKillProductInfo(productId);
    }


    /**
     * 秒杀，没有抢到获得"哎呦喂,xxxxx",抢到了会返回剩余的库存量
     * @param productId
     * @return
     * @throws Exception
     */
    @GetMapping("/order/{productId}")
    public String skill(@PathVariable String productId)throws Exception
    {
        log.info("@skill request, productId:" + productId);
        secKillService.orderProductMockDiffUser(productId);
        return secKillService.querySecKillProductInfo(productId);
    }

    /**
    * 查询用户推广信息
    * */
    //订单详情
    @PostMapping("/attention")
    public ResultVO<OrderDTO> detail(@RequestParam("openid") String openid) {

        List<UserInfo> userInfo = userInfoService.finaByPartner(openid);
        List<UserInfo> userInfo1 = userInfoService.finaByPartnerTom(openid);
        List<String> userInfo2 = userInfoService.finaByPartnerYx(openid);
         String zsy=userInfoService.finaByPartnerZsy(openid);
         String zrsy=userInfoService.finaByPartnerZrsy(openid);
         String sysy=userInfoService.finaByPartnerSysy(openid);
         String ktx=userInfoService.finaByPartnerKtx(openid);
        PartnerDTO partnerDTO=new PartnerDTO();
        partnerDTO.setGzrs(userInfo.size());
        partnerDTO.setYxrs(userInfo2.size());
        partnerDTO.setJrxz(userInfo1.size());
        partnerDTO.setZsy(Double.valueOf(zsy==null?"0":zsy));
        partnerDTO.setZrsy(Double.valueOf(zrsy==null?"0":zrsy));
        partnerDTO.setSysy(Double.valueOf(sysy==null?"0":sysy));
        partnerDTO.setKtx(Double.valueOf(ktx==null?"0":ktx));
        return ResultVOUtil.success(partnerDTO);
    }
}
