package com.imooc.menu;
import com.imooc.service.PushMessageService;
import com.imooc.service.impl.PushMessageServiceImpl;
import net.sf.json.JSONObject;

public class WeixinTest {
    public static void main(String[] args){
        try {
            PushMessageService pushMessageService=new PushMessageServiceImpl();
//            AccessToken token = WenXinUntil.getAccessToken();
            String access_token= pushMessageService.getAccessTokes();

            String menu = JSONObject.fromObject(WenXinUntil.initMenu()).toString();
            int result = WenXinUntil.createMenu(access_token, menu);
            if(result == 0){
                System.out.println("创建菜单成功");
            }else{
                System.out.println("创建菜单失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}