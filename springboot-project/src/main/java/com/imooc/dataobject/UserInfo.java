package com.imooc.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imooc.enums.ProductStatusEnum;
import com.imooc.utils.EnumUtil;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
public class UserInfo implements Serializable {
    @Id
    @GeneratedValue
    private  String id;
    //用户id
    /**名字**/
    private String name;

    /**用户邮箱**/
    private String email;

    /**用户密码**/
    private String password;

    /**微信用户唯一标示码**/
    private String openid;

    /**用户头像**/
    private String headimgurl;


    /**性别 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知**/
    private Integer sex;
    private Integer is_frz;//0 开启 1 关闭
    private String partnerId;
    private Integer subscribe;
    private Date email_verified_at;

}
