package com.imooc.menu;

import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

public class WenXinUntil {
    public static Menu initMenu(){
        Menu menu = new Menu();
        ViewButton button11 = new ViewButton();
        button11.setName("星球通道");
        button11.setType("view");
        button11.setUrl("http://www.luluxingqiu.com");

        ViewButton button21 = new ViewButton();
        button21.setName("订阅");
        button21.setType("view");
        button21.setUrl("http://www.luluxingqiu.com/view/adviceMessage.html");//我这里测试使用百度网站

        ViewButton button31 = new ViewButton();
        button31.setName("加入我们");
        button31.setType("view");
        button31.setUrl("http://www.luluxingqiu.com/view/order/attention.html");

//        ClickButton button32 = new ClickButton();
//        button32.setName("地理位置");
//        button32.setType("location_select");
//        button32.setKey("32");

//        Button button = new Button();
//        button.setName("菜单");
//        button.setSub_button(new Button[]{button31,button32});

        menu.setButton(new Button[]{button11,button21,button31});
        return menu;

    }
    public static int createMenu(String token,String menu) throws ParseException, IOException {
        int result = 0;
        String url = " https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+token;
        String jsonObject = sendHttpByPost(url, menu);
        System.out.println(jsonObject);
        return result;

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
