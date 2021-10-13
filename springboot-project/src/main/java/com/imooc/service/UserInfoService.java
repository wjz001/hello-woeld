package com.imooc.service;

import com.imooc.dataobject.SellerInfo;
import com.imooc.dataobject.UserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface UserInfoService {
    UserInfo findUserInfoByOpenid(String openid);
    UserInfo create(UserInfo userInfo);
    List<UserInfo> finaAll();
    UserInfo updataByOpenId1(String openid);
    UserInfo updataByOpenId2(String openid);
    List<UserInfo> finaByPartner(String openid);
    List<UserInfo> finaByPartnerTom(String openid);
    List<String> finaByPartnerYx(String openid);
    String finaByPartnerZsy(String openid);
    String finaByPartnerZrsy(String openid);
    String finaByPartnerSysy(String openid);
    String finaByPartnerKtx(String openid);

}
