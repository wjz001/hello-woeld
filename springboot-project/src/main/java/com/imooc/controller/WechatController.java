package com.imooc.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imooc.config.ProjectUrlConfig;
import com.imooc.dataobject.UserInfo;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.service.UserInfoService;
import com.imooc.utils.CookieUtil;
import com.imooc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * Created by SqMax on 2018/3/23.
 */
@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WxMpService wxOpenService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){
//        WxMpService wxMpService=new WxMpServiceImpl();
        //1. 配置
        //2.调用方法
        String url=projectUrlConfig.getWechatMpAuthorize()+"/sell/wechat/userInfo";
        String redirectUrl=wxMpService.oauth2buildAuthorizationUrl(url,WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl));
        log.info("【微信网页授权】获取code,redirectUrl={}",redirectUrl);
        return "redirect:"+redirectUrl;//重定向到下面一个方法
    }
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                         @RequestParam("state") String returnUrl) throws Exception {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken=new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken=wxMpService.oauth2getAccessToken(code);
            log.info("wxMpOAuth2AccessToken={}", JsonUtil.toJson(wxMpOAuth2AccessToken));
//            redisTemplate.opsForValue().set("AccessToken",wxMpOAuth2AccessToken.getAccessToken(),1000*60*60L, TimeUnit.MILLISECONDS);
//            log.info("redis---------------------access_token" +redisTemplate.opsForValue().get("access_token"));
        }catch (WxErrorException e){
            log.error("【微信网页授权】,{}",e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());
        }
        String openId=wxMpOAuth2AccessToken.getOpenId();

        log.info("【微信网页授权】获取openid,returnUrl={}",returnUrl);
        try {
            UserInfo userInfo1=userInfoService.findUserInfoByOpenid(openId);
            if(userInfo1==null) {
                //第四步(获取用户接口)
                String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + wxMpOAuth2AccessToken.getAccessToken()
                        + "&openid=" + openId
                        + "&lang=zh_CN";
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> userInfoEntity = restTemplate.getForEntity(infoUrl, String.class); // 乱码
                String userInfoStr = userInfoEntity.getBody();
                String userInfoMsg = new String(userInfoStr.getBytes("ISO-8859-1"), "UTF-8");

                JSONObject userInfoMap = JSON.parseObject(userInfoMsg);
                UserInfo userInfo=new UserInfo();
                userInfo.setOpenid(openId);
                userInfo.setEmail("123");
                userInfo.setName(userInfoMap.getString("nickname"));
                userInfo.setSex(userInfoMap.getInteger("sex"));
                userInfo.setPassword("123");
                userInfo.setHeadimgurl(userInfoMap.getString("headimgurl"));
                userInfo.setSex(userInfoMap.getInteger("sex"));
                userInfo.setIs_frz(0);
                userInfoService.create(userInfo);
                System.out.println(" 微信获取到的用户信息为userInfo------:" + userInfoMap.toString());
            }
        }catch (Exception e){
            log.error("【微信网页授权】,{}",e);
            throw new Exception();
        }
        return "redirect:"+ returnUrl+"?openid="+openId;

    }//以上两个方法是SDK方式微信网页授权的过程，
    // 访问http://sqmax.natapp1.cc/sell/wechat/authorize?returnUrl=http://www.imooc.com，
    //最终将会跳转到这个链接：http://www.imooc.com?openid={openid}

    //微信登陆
    @GetMapping("/qrAuthorize")
    public String qrAuthorize(@RequestParam("returnUrl") String returnUrl){
        String url=projectUrlConfig.getWechatOpenAuthorize()+"/sell/wechat/userInfo";
        String redirectUrl=wxOpenService.buildQrConnectUrl(url,WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN,URLEncoder.encode(returnUrl));
        return "redirect:"+redirectUrl;
    }
    @GetMapping("/qrUserInfo")
    public String qrUserInfo(@RequestParam("code") String code,
                             @RequestParam("state") String returnUrl){
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken=new WxMpOAuth2AccessToken();
        try{
            wxMpOAuth2AccessToken=wxOpenService.oauth2getAccessToken(code);
        }catch (WxErrorException e){
            log.error("【微信网页】{}",e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());
        }
        log.info("wxMpOAuth2AccessToken={}", JsonUtil.toJson(wxMpOAuth2AccessToken));
        String openId=wxMpOAuth2AccessToken.getOpenId();
        return "redirect:"+returnUrl+"?openid="+openId;
    }
    @GetMapping("/redirectGetUserInfo")
    public String getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {



        //如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
        //获取code  code作为换取access_token的票据
        String code = request.getParameter("code");
        System.out.println("授权返回code信息---------:" + code);
        //第二步：通过code换取网页授权access_token (获取openid接口)
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + "wx8ea6fc446e2448ea"
                + "&secret=" + "470e1c82572b02fdb4de4cc518080025"
                + "&code=" + code
                + "&grant_type=authorization_code";
        RestTemplate restTemplate=new RestTemplate();
        //发送请求 get提交 拿code凭证去获取openid和access_token
        ResponseEntity<String> restTemplateForEntity = restTemplate.getForEntity(url, String.class);
        String getAccessTokenBody = restTemplateForEntity.getBody();
        log.info("wxMpOAuth2AccessToken={}", JsonUtil.toJson(getAccessTokenBody));

//        HashMap<String, String> AccessTokenAndRefreshTokenMap = JsonUtil.string2Obj(getAccessTokenBody, new TypeReference<HashMap<String, String>>() {
//        });
            JSONObject AccessTokenAndRefreshTokenMap = JSON.parseObject(getAccessTokenBody);


        Object openid = AccessTokenAndRefreshTokenMap.get("openid");
        Object access_token = AccessTokenAndRefreshTokenMap.get("access_token");  // 2H
        Object refresh_token = AccessTokenAndRefreshTokenMap.get("refresh_token"); // 30天
//            log.info("将openid为：{},放入redis中！",openid);
        // 判断是否是第一次登录，假如是第一次登录就跳转注册网页,并保存openid
        // 判断用户是否是第一次登录本系统，这里放在了redis 假如你考虑到用户量过大，可以考虑换其他方式
//        if (!isNotFirstLogin) { //是第一次登录的用户
//            log.info("未从缓存中获取到openid，该用户是第一次登录！");
//            log.info("将openid为：{},放入redis中！",openid);
//            //获取用户拿到openid 和access_token去获取用户信息，在页面中进行业务处理，获取存储在数据库中:
//            //第四步(获取用户接口)
//            String infoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token
//                    + "&openid=" + openid
//                    + "&lang=zh_CN";
//
//            ResponseEntity<String> userInfoEntity = restTemplate.getForEntity(infoUrl, String.class); // 乱码
//            String userInfoStr = userInfoEntity.getBody();
//            String userInfoMsg = new String(userInfoStr.getBytes("ISO-8859-1"), "UTF-8");
//            HashMap<String, Object> userInfoMap = JsonMapper.string2Obj(userInfoMsg, new TypeReference<HashMap<String, Object>>() {
//            });
//            System.out.println(" 微信获取到的用户信息为userInfo------:" + userInfoMap.toString());
//            // 保存用户信息进数据库
//            userService.insertUser(userInfoMap);
//            // 设置session
            request.getSession().setAttribute("openid",openid);
//            // 跳转到用户注册页
//            response.sendRedirect("http://xxx.com/...");// todo 此处为你系统中的注册页面，用来让用户输入你需要的数据，如电话、住址等等业务数据。
//
//        }else{ // 不是第一次登录，只是session失效了
//            log.info("该用户不是第一次登录，将openid放入session中，并跳转回首页！");
            request.getSession().setAttribute("openid",openid);
        Cookie cookie = new Cookie("openid", openid.toString());
        cookie.setPath("/");
        response.addCookie(cookie);
        CookieUtil.set(response, "openid",openid.toString(),720000);

//        }

        return "openid";
    }
}
