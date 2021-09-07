package com.imooc.service;

import com.imooc.dataobject.SellerInfo;
import com.imooc.dataobject.UserInfo;

import java.util.List;

public interface UserInfoService {
    UserInfo findUserInfoByOpenid(String openid);
    UserInfo create(UserInfo userInfo);
    List<UserInfo> finaAll();
    UserInfo updataByOpenId1(String openid);
    UserInfo updataByOpenId2(String openid);

}
