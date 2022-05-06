package com.densoft.shopmefrontend.order;

import com.densoft.shopmecommon.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Integer> {

}
