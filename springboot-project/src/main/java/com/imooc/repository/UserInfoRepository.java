package com.imooc.repository;

import com.imooc.dataobject.SellerInfo;
import com.imooc.dataobject.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    UserInfo findByOpenid(String openid);

    @Query(value = "select  * from user_info where  partner_id=?1",nativeQuery = true)
    List<UserInfo> finaByPartner(String openId);
    @Query(value = "select  * from user_info where  partner_id=?1 and to_days(email_verified_at)=to_days(now())",nativeQuery = true)
    List<UserInfo> finaByPartnerTom(String openId);
    @Query(value = "SELECT  buyer_openid from order_master  where pay_status=1 and PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( create_time, '%Y%m' ) ) =1 and buyer_openid in" +
            "(select b.openid from user_info b where b.partner_id=?1) GROUP BY buyer_openid",nativeQuery = true)
    List<String> finaByPartnerYx(String openId);
    @Query(value = "SELECT  SUM(order_amount) from order_master  where pay_status=1 and buyer_openid " +
            "in(select b.openid from user_info b where b.partner_id=?1)",nativeQuery = true)
    String finaByPartnerZsy(String openId);
    @Query(value = "SELECT  SUM(order_amount) from order_master  where pay_status=1 and to_days(create_time)=to_days(now()) and buyer_openid " +
            "in(select b.openid from user_info b where b.partner_id=?1)",nativeQuery = true)
    String finaByPartnerZrsy(String openId);
    @Query(value = "SELECT  SUM(order_amount) from order_master  where pay_status=1 and PERIOD_DIFF( date_format( now( ) , '%Y%m' ) , date_format( create_time, '%Y%m' ) ) =1 and buyer_openid " +
            "in(select b.openid from user_info b where b.partner_id=?1)",nativeQuery = true)
    String finaByPartnerSysy(String openId);
    @Query(value = "SELECT  SUM(order_amount) from order_master  where pay_status=1 and  DATE_FORMAT( create_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' ) and buyer_openid " +
            "in(select b.openid from user_info b where b.partner_id=?1)",nativeQuery = true)
    String finaByPartnerBysy(String openId);
}
