package com.imooc.controller;

import com.imooc.dataobject.UserInfo;
import com.imooc.repository.UserInfoRepository;
import com.imooc.service.UserInfoService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RestController
public class TokenController{
    private Logger logger = LoggerFactory.getLogger(TokenController.class);
    private static final String TOKEN = "sjyx";

    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserInfoRepository userInfoRepository;
//    /**
//     * 微信验证token
//     * @param signature
//     * @param timestamp
//     * @param nonce
//     * @param echostr
//     * @return
//     */
//    @GetMapping(value = "/checkToken",produces = "text/html;charset=utf-8")
//    public String checkToken(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
//                             @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr) {
//        //排序
//        String[] arr = {TOKEN, timestamp, nonce};
//        Arrays.sort(arr);
//
//        StringBuilder content = new StringBuilder();
//        for (int i = 0; i < arr.length; i++) {
//            content.append(arr[i]);
//        }
//
//        //sha1Hex 加密
//        MessageDigest md = null;
//        String temp = null;
//        try {
//            md = MessageDigest.getInstance("SHA-1");
//            byte[] digest = md.digest(content.toString().getBytes());
//            temp = byteToStr(digest);
//            logger.info("加密后的token:"+temp);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        if ((temp.toLowerCase()).equals(signature)){
//            return echostr;
//        }
//        return null;
//    }

    private static String byteToStr(byte[] byteArray){
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    private static String byteToHexStr(byte mByte){
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A','B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4)& 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }
    /**
     * 微信验证回调
     * @param request
     * @param response
     *
     *
     * <xml><ToUserName><![CDATA[gh_2a5540516edb]]></ToUserName>
    <FromUserName><![CDATA[odwP2jt6aTBu_Dl1ypoUJ8pN9UOs]]></FromUserName>
    <CreateTime>1425633941</CreateTime>
    <MsgType><![CDATA[event]]></MsgType>
    <Event><![CDATA[SCAN]]></Event>
    <EventKey><![CDATA[0]]></EventKey>
    <Ticket><![CDATA[gQGK8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2RIVlBsRVBsdEhITk9jQzRaMXV6AAIEgXL5VAMECAcAAA==]]></Ticket>
    </xml>
     */
    @RequestMapping(value="/checkToken",method={RequestMethod.GET,RequestMethod.POST} )
    public void checkToken(@RequestBody(required=false) String body, HttpServletRequest request, HttpServletResponse response) {

        System.out.println(body.toString());
        System.out.println("================================微信URL回调测试=========================");
        SAXReader saxReader = new SAXReader();
        Document document;
        try {
            document = saxReader.read(new ByteArrayInputStream(body.toString().getBytes("UTF-8")));
            Element rootElt = document.getRootElement();
            System.out.println("FromUserName===" + rootElt.elementText("FromUserName"));
            System.out.println("EventKey===" + rootElt.elementText("EventKey"));
            UserInfo userInfo=userInfoService.findUserInfoByOpenid(rootElt.elementText("FromUserName"));
            if("subscribe".equals(rootElt.elementText("Event"))){
                if(userInfo==null) {
                    userInfo=new UserInfo();
                    userInfo.setOpenid(rootElt.elementText("FromUserName"));
                    userInfo.setEmail("123456");
                    userInfo.setName("nickname");
                    userInfo.setSex(0);
                    userInfo.setPassword("123");
                    userInfo.setHeadimgurl("headimgurl");
                    userInfo.setIs_frz(0);
                    userInfo.setPartnerId(rootElt.elementText("EventKey").substring(8));
                    userInfoService.create(userInfo);
                }else {
                    userInfo.setSubscribe(0);
                    userInfoRepository.save(userInfo);
                }
            }
            if("unsubscribe".equals(rootElt.elementText("Event"))) {
                userInfo.setSubscribe(1);
                userInfoRepository.save(userInfo);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        /*
	 * 微信回调验证
	 *
	 * String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");

		String token = "dmx";

		PrintWriter out;
		try {
			out = response.getWriter();
			out.println(echostr);
			out.close();
			response.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
}
