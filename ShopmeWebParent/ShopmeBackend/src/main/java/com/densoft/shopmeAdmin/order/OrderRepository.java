package com.densoft.shopmeAdmin.order;

import com.densoft.shopmecommon.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Integer> {
}
