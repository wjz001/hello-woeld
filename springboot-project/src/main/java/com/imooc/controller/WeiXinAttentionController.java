package com.imooc.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.imooc.service.PushMessageService;
import net.sf.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/attention")
public class WeiXinAttentionController {

@Autowired
    PushMessageService pushMessageService;


    @RequestMapping(value = "/weixinAttention", method = RequestMethod.POST)
    @ResponseBody
    public String init(@RequestParam("body") String body, HttpServletRequest request, HttpServletResponse response) {
        String erweima = null;
        try {
            String asscssToken = pushMessageService.getAccessTokes();
//            JSONObject bodyXml = JSONObject.fromObject(body);
            HttpPost httppost = new HttpPost("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + asscssToken);

            String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + asscssToken;
            Map<String, Map<String, Integer>> sceneMap = new HashMap<String, Map<String, Integer>>();
            Map<String, Integer> sceneIdMap = new HashMap<String, Integer>();
//            sceneIdMap.put("scene_id", (Integer) bodyXml.get("sceneId"));
            sceneIdMap.put("scene_id", 1);
            sceneMap.put("scene", sceneIdMap);

            JSONObject attentionXml = new JSONObject();
            attentionXml.put("expire_seconds", 25900);
            attentionXml.put("action_name", "QR_LIMIT_SCENE");
            attentionXml.put("action_info", sceneMap);
            System.out.println("atten=====" + attentionXml.toString());
            System.out.println("body=====" + body);
            try {

                StringEntity se = new StringEntity(attentionXml.toString());

                httppost.setEntity(se);

                System.out.println("executing request" + httppost.getRequestLine());

//                CloseableHttpResponse responseEntry = httpclient.execute(httppost);
//                RestTemplate restTemplate=new RestTemplate();
//                //headers
//                HttpHeaders requestHeaders = new HttpHeaders();
//                requestHeaders.add("api-version", "1.0");
//                //body
//                MultiValueMap requestBody = new LinkedMultiValueMap<>();
//                //HttpEntity
//                HttpEntity<MultiValueMap> requestEntity = new HttpEntity<MultiValueMap>(requestBody, requestHeaders);
//                //post
//                ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
//                System.out.println(responseEntity.getBody());

                String data = "{\"expire_seconds\": 2592000, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\":\""+body+"\"}}}";
                String result = sendHttpByPost(url, data);
                System.out.println(" 带参数的二维码 : " + result);
                if (!StringUtils.isEmpty(result)) {
                    com.alibaba.fastjson.JSONObject userInfoMap = JSON.parseObject(result);

//                    JSONObject jsonObject = JSONObject.(result);
                    String string = userInfoMap.getString("ticket");
                    System.out.println("ticket = " + string);
                    erweima = getTicket(string);
                }
            } finally {
//                httpclient.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
//            JSONObject result = new JSONObject();
//            result.put("status","error");
//            result.put("msg",e.getMessage());
//            return result;
        }
        return erweima;
    }
    public static String getTicket(String data) {
        String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+data;
        String result = sendHttpByGet(url);
        System.out.println(result);
        return url;

    }
    public static String sendHttpByGet(String url){

        try {
            URL urlGet = new URL(url);
            URLConnection urlConnection = urlGet.openConnection();
            InputStream is = urlConnection.getInputStream();
            int size ;
            byte[] jsonBytes = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            while ((size=is.read(jsonBytes))!=-1){
                stringBuilder.append(new String(jsonBytes,0,size));
            }
            is.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String sendHttpByPost(String url ,String data ){

        try {
            URL urlPost = new URL(url);
            URLConnection urlConnection = urlPost.openConnection();
            //要发送数据出去，必须设置为可发送状态
            urlConnection.setDoOutput(true);
            //获取输出流
            OutputStream outputStream = urlConnection.getOutputStream();
            //写出数据
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();

            //获取输入流
            InputStream is = urlConnection.getInputStream();

            int size ;
            byte[] jsonBytes = new byte[1024];
            StringBuilder stringBuilder = new StringBuilder();
            while ((size=is.read(jsonBytes))!=-1){
                stringBuilder.append(new String(jsonBytes,0,size));
            }
            is.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}