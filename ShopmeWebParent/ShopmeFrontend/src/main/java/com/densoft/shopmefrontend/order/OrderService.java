package com.densoft.shopmefrontend.order;

import com.densoft.shopmecommon.entity.Address;
import com.densoft.shopmecommon.entity.CartItem;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.order.*;
import com.densoft.shopmecommon.entity.product.Product;
import com.densoft.shopmefrontend.checkout.CheckOutInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    public static final int ORDERS_PER_PAGE = 5;

    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Customer customer, Address address, List<CartItem> cartItemList, PaymentMethod paymentMethod, CheckOutInfo checkOutInfo) {
        Order order = new Order();
        order.setOrderTime(new Date());

        if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.NEW);
        }


        order.setCustomer(customer);
        order.setProductCost(checkOutInfo.getProductCost());
        order.setSubTotal(checkOutInfo.getProductTotal());
        order.setShippingCost(checkOutInfo.getShippingCostTotal());
        order.setTax(0);
        order.setTotal(checkOutInfo.getPaymentTotal());
        order.setPaymentMethod(paymentMethod);
        order.setDeliverDays(checkOutInfo.getDeliverDays());
        order.setDeliveryDate(checkOutInfo.getDeliverDate());

        if (address == null) {
            order.copyAddressFromCustomer();
        } else {
            order.copyShippingAddress(address);
        }

        setOrderDetails(cartItemList, order);

        OrderTrack track = getOrderTrack(order);

        order.getOrderTracks().add(track);

        return orderRepository.save(order);
    }

    private OrderTrack getOrderTrack(Order order) {
        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus(OrderStatus.NEW);
        track.setNotes(OrderStatus.NEW.defaultDescription());
        track.setUpdatedTime(new Date());
        return track;
    }

    private void setOrderDetails(List<CartItem> cartItemList, Order order) {
        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (CartItem cartItem : cartItemList) {
            Product product = cartItem.getProduct();
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubTotal(cartItem.getSubTotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());
            orderDetails.add(orderDetail);
        }
    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNUm, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNUm - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findAll(keyword, customer.getId(), pageable);
        }

        return orderRepository.findAll(customer.getId(), pageable);
    }

    public Order getOrder(Integer id, Customer customer) {
        return orderRepository.findByIdAndCustomer(id, customer);
    }
}
