package com.imooc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.imooc.config.WechatAccountConfig;
import com.imooc.dataobject.ProductInfo;
import com.imooc.dataobject.UserInfo;
import com.imooc.dto.OrderDTO;
import com.imooc.exception.SellException;
import com.imooc.service.PushMessageService;
import com.imooc.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by SqMax on 2018/4/2.
 */
@Service
@Slf4j
public class PushMessageServiceImpl implements PushMessageService {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WechatAccountConfig accountConfig;

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public void orderStatus(OrderDTO orderDTO) {

        WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        templateMessage.setTemplateId(accountConfig.getTemplateId().get("orderStatus"));//模板id:"GoCullfix05R-rCibvoyI87ZUg50cyieKA5AyX7pPzo"
        templateMessage.setToUser(orderDTO.getBuyerOpenid());//openid:"ozswp1Ojl2rA57ZK97ntGw2WQ2CA"

        List<WxMpTemplateData> data = Arrays.asList(
                new WxMpTemplateData("first", "亲，记得收货"),
                new WxMpTemplateData("keyword1", "微信点餐"),
                new WxMpTemplateData("keyword2", "15712363915"),
                new WxMpTemplateData("keyword3", orderDTO.getOrderId()),
                new WxMpTemplateData("keyword4", orderDTO.getOrderStatusEnum().getMessage()),
                new WxMpTemplateData("keyword5", "￥" + orderDTO.getOrderAmount()),
                new WxMpTemplateData("remark", "欢迎再次光临"));
        templateMessage.setData(data);

        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            log.error("【微信模板消息】发送失败，{}", e);
        }
    }

    @Override
    public void orderPush(ProductInfo productInfo) {
        WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        templateMessage.setTemplateId(accountConfig.getTemplateId().get("orderPush"));//模板id:"GoCullfix05R-rCibvoyI87ZUg50cyieKA5AyX7pPzo
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd :HH-mm-ss");    //格式化规则
        Date date = productInfo.getCreateTime();         //获得你要处理的时间 Date型
        String datetime = sdf.format(date); //格式化成yyyy-MM-dd格式的时间字符串

//        templateMessage.setToUser(resultVO.getBuyerOpenid());//openid:"ozswp1Ojl2rA57ZK97ntGw2WQ2CA"
        List<WxMpTemplateData> data = Arrays.asList(
                new WxMpTemplateData("first", "你好，订阅的资讯已送达"),
                new WxMpTemplateData("Date", datetime),
                new WxMpTemplateData("Source", productInfo.getProductName(), "#ff0000"),
                new WxMpTemplateData("Description", productInfo.getProductDescription(), "#0000ff"),
                new WxMpTemplateData("remark","如被打扰，请点击订阅取消推送")
        );
        templateMessage.setData(data);
        templateMessage.setUrl("http://www.luluxingqiu.com/app/view/detail.html?productId=" + productInfo.getProductId());
        try {
            List<UserInfo> list = userInfoService.finaAll();
            if (list != null) {
//                for (int i=0;i< list.size();i++){
               String access_token= getAccessTokes();
                for (UserInfo user : list) {
                    if (0 == (user.getIs_frz())&&judgeIsFollow(user.getOpenid(),access_token)) {
                        templateMessage.setToUser(user.getOpenid());//openid:"ozswp1Ojl2rA57ZK97ntGw2WQ2CA"
                        wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                    }
                }
                log.error("【微信模板消息】发送成功，{}");

//                }
            }
        } catch (WxErrorException e) {
            log.error("【微信模板消息】发送失败，{}", e);
        }
        try {
//            doPostWith2(productInfo);
        }catch (SellException e){
            e.printStackTrace();
        }

    }
    /**
     * 以post方式请求第三方http接口 postForEntity
     * @param productInfo
     * @return
     */
    public String doPostWith2(ProductInfo productInfo){
        String body = restTemplate.postForObject("http://www.enxian.online/sell/seller/product/orderPush", productInfo, String.class);
        return body;
    }
    @Override
    public void orderPushOther(ProductInfo productInfo) {
        WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        templateMessage.setTemplateId(accountConfig.getTemplateId().get("orderPush"));//模板id:"GoCullfix05R-rCibvoyI87ZUg50cyieKA5AyX7pPzo
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd :HH-mm-ss");    //格式化规则
        Date date = productInfo.getCreateTime();         //获得你要处理的时间 Date型
        String datetime = sdf.format(date); //格式化成yyyy-MM-dd格式的时间字符串

//        templateMessage.setToUser(resultVO.getBuyerOpenid());//openid:"ozswp1Ojl2rA57ZK97ntGw2WQ2CA"
        List<WxMpTemplateData> data = Arrays.asList(
                new WxMpTemplateData("first", "你好，订阅的资讯已送达"),
                new WxMpTemplateData("Date", datetime),
                new WxMpTemplateData("Source", productInfo.getProductName(), "#ff0000"),
                new WxMpTemplateData("Description", productInfo.getProductDescription(), "#0000ff"),
                new WxMpTemplateData("remark","如被打扰，请点击订阅取消推送")
        );
        templateMessage.setData(data);
        templateMessage.setUrl("http://www.enxian.online/view/detail.html?productId=" + productInfo.getProductId());
        try {
            List<UserInfo> list = userInfoService.finaAll();
            if (list != null) {
//                for (int i=0;i< list.size();i++){
                String access_token= getAccessTokes();
                for (UserInfo user : list) {
                    if (0 == (user.getIs_frz())&&judgeIsFollow(user.getOpenid(),access_token)) {
                        templateMessage.setToUser(user.getOpenid());//openid:"ozswp1Ojl2rA57ZK97ntGw2WQ2CA"
                        wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                    }
                }
                log.error("【微信模板消息】发送成功，{}");

//                }
            }
        } catch (WxErrorException e) {
            log.error("【微信模板消息】发送失败，{}", e);
        }

    }
    public  String getAccessTokes() {
        String access_token = "";
        String grant_type = "client_credential";// 获取access_token填写client_credential
        String AppId = "wx8ea6fc446e2448ea";// 第三方用户唯一凭证
        String secret = "470e1c82572b02fdb4de4cc518080025";// 第三方用户唯一凭证密钥，即appsecret

// 这个url链接地址和参数皆不能变
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=" + grant_type + "&appid=" + AppId + "&secret="
                + secret;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            access_token = demoJson.getString("access_token");
            System.out.println("getAccessToke------------------JSON字符串：" + demoJson);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_token;
    }

    public  boolean judgeIsFollow(String openid,String access_token){
//      String access_token=  getAccessTokes();
        Integer subscribe = 0;
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            System.out.println("JSON字符串："+demoJson);
            subscribe = demoJson.getIntValue("subscribe"); // 此字段为关注字段  关注为1 未关注为0
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1==subscribe?true:false;
    }

}
