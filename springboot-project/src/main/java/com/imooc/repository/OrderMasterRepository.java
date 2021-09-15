package com.imooc.repository;

import com.imooc.dataobject.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by SqMax on 2018/3/18.
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> {

    Page<OrderMaster> findByBuyerOpenid(String buyerOpenid, Pageable pageable);

    @Query(value = "select  * from order_master where  pay_status=?1 and create_time>?2 ORDER BY ?#{#pageable}",nativeQuery = true)
    Page<OrderMaster> findAlls(Integer paystatus, Date date,Pageable pageable);

    List <OrderMaster> findByBuyerOpenidAndBuyerNameAndPayStatus(String buyerOpenid, String buyerName,Integer payStatus);

}
