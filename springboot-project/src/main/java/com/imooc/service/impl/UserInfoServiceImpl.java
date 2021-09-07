package com.imooc.service.impl;

import com.imooc.dataobject.SellerInfo;
import com.imooc.dataobject.UserInfo;
import com.imooc.repository.UserInfoRepository;
import com.imooc.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
