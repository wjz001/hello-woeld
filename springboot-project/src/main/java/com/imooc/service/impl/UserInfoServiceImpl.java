package com.imooc.service.impl;

import com.imooc.dataobject.SellerInfo;
import com.imooc.dataobject.UserInfo;
import com.imooc.repository.UserInfoRepository;
import com.imooc.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoRepository userInfoRepository;
    @Override
    public UserInfo findUserInfoByOpenid(String openid) {
        return userInfoRepository.findByOpenid(openid);
    }

    @Override
    public UserInfo create(UserInfo userInfo) {
        userInfo.setEmail_verified_at(new Date());
        userInfo.setSubscribe(0);
        return userInfoRepository.save(userInfo);
    }

    @Override
    public List<UserInfo> finaAll() {
        return userInfoRepository.findAll();
    }

    @Override
    public UserInfo updataByOpenId1(String openid) {
      UserInfo userInfo=  userInfoRepository.findByOpenid(openid);
        userInfo.setIs_frz(0);
        userInfoRepository.save(userInfo);
        return null;
    }
    @Override
    public UserInfo updataByOpenId2(String openid) {
        UserInfo userInfo=  userInfoRepository.findByOpenid(openid);
        userInfo.setIs_frz(1);
        userInfoRepository.save(userInfo);
        return null;
    }

    //关注人数
    @Override
    public List<UserInfo> finaByPartner(String openid) {
        return userInfoRepository.finaByPartner(openid);

    }
    //有效关注人数
    @Override
    public List<String> finaByPartnerYx(String openid) {
        Date date = new Date();
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(date);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -30);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时
        return userInfoRepository.finaByPartnerYx(openid);

    }

    @Override
    public String finaByPartnerZsy(String openid) {

        return userInfoRepository.finaByPartnerZsy(openid);
    }

    @Override
    public String finaByPartnerZrsy(String openid) {
        Date date = new Date();
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(date);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时
        return userInfoRepository.finaByPartnerZrsy(openid);
    }

    @Override
    public String finaByPartnerSysy(String openid) {
        Date date = new Date();
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(date);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -30);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时
        return userInfoRepository.finaByPartnerSysy(openid);
    }

    @Override
    public String finaByPartnerKtx(String openid) {
        return userInfoRepository.finaByPartnerBysy(openid);
    }

    //昨天关注人数
    @Override
    public List<UserInfo> finaByPartnerTom(String openid) {
        Date date = new Date();
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(date);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        dBefore = calendar.getTime();   //得到前一天的时
        return userInfoRepository.finaByPartnerTom(openid);

    }

}
