package com.imooc.repository;

import com.imooc.dataobject.ProductInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by SqMax on 2018/3/17.
 */
public interface ProductInfoRepository extends JpaRepository<ProductInfo,String>{

    List<ProductInfo> findByProductStatusAndCreateTimeIsAfterOrderByCreateTimeDesc(Integer productStatus, Date date);

    @Query(value = "select  * from product_info where create_time>?1 ORDER BY ?#{#pageable}",nativeQuery = true)
    Page<ProductInfo> findAlls(Date date, Pageable pageable);

    ProductInfo findByProductIcon(String productIcon);
}
