package com.densoft.shopmefrontend.checkout;

import com.densoft.shopmecommon.entity.CartItem;
import com.densoft.shopmecommon.entity.ShippingRate;
import com.densoft.shopmecommon.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckOutService {

    public static final int DIM_DIVISOR = 139;

    public CheckOutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckOutInfo checkOutInfo = new CheckOutInfo();
        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;

        checkOutInfo.setProductCost(productCost);
        checkOutInfo.setProductTotal(productTotal);
        checkOutInfo.setShippingCostTotal(shippingCostTotal);
        checkOutInfo.setPaymentTotal(paymentTotal);

        checkOutInfo.setDeliverDays(shippingRate.getDays());
        checkOutInfo.setCodSupported(shippingRate.isCodSupported());
        return checkOutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0f;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            float dimWeight = (product.getLength() * product.getWeight() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();
            item.setShippingCost(shippingCost);
            shippingCostTotal += shippingCost;
        }
        return shippingCostTotal;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0f;
        for (CartItem item : cartItems) {
            total += item.getSubTotal();
        }
        return total;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0f;
        for (CartItem item : cartItems) {
            cost += item.getQuantity() * item.getProduct().getCost();
        }
        return cost;
    }
}
