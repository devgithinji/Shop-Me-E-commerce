package com.densoft.shopmeAdmin.order;

import com.densoft.shopmecommon.entity.order.OrderDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    public void testFindWithCategoryAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");
        List<OrderDetail> orderDetails = orderDetailRepository.findWithCategoryAndTimeBetween(startTime, endTime);
        assertThat(orderDetails.size()).isGreaterThan(0);
        for (OrderDetail orderDetail : orderDetails) {
            System.out.printf("%-30s | %d | %.2f \t | %.2f \t | %.2f \n",
                    orderDetail.getProduct().getCategory().getName(), orderDetail.getQuantity(), orderDetail.getProductCost(), orderDetail.getShippingCost(), orderDetail.getSubTotal());
        }
    }


    @Test
    public void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormatter.parse("2021-08-01");
        Date endTime = dateFormatter.parse("2021-08-31");
        List<OrderDetail> orderDetails = orderDetailRepository.findWithProductAndTimeBetween(startTime, endTime);
        assertThat(orderDetails.size()).isGreaterThan(0);
        for (OrderDetail orderDetail : orderDetails) {
            System.out.printf("%-30s | %d | %.2f \t | %.2f \t | %.2f \n",
                    orderDetail.getProduct().getShortName(), orderDetail.getQuantity(), orderDetail.getProductCost(), orderDetail.getShippingCost(), orderDetail.getSubTotal());
        }
    }

}