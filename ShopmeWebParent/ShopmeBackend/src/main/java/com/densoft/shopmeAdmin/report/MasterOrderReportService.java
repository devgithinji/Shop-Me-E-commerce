package com.densoft.shopmeAdmin.report;

import com.densoft.shopmeAdmin.order.OrderRepository;
import com.densoft.shopmecommon.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class MasterOrderReportService {

    @Autowired
    private OrderRepository orderRepository;

    private DateFormat dateFormatter;


    public List<ReportItem> getReportDataLast7Days() {
        System.out.println("getReportDataLast7days...");
        return getReportDataLastXDays(7);
    }

    public List<ReportItem> getReportDataLast28Days() {
        return getReportDataLastXDays(28);
    }

    public List<ReportItem> getReportDataLast6Months() {
        return getReportDataLastXMonths(6);
    }


    public List<ReportItem> getReportDataLastYear() {
        return getReportDataLastXMonths(12);
    }


    private List<ReportItem> getReportDataLastXMonths(int months) {
        Calendar startCalendar = Calendar.getInstance();
        //subtract 1 year due since data used is 1 year old from time of development
        startCalendar.add(Calendar.YEAR, -1);
        Date endTime = startCalendar.getTime();

        Calendar calendar = Calendar.getInstance();
        //subtract 1 year due since data used is 1 year old from time of development
        calendar.add(Calendar.YEAR, -1);
        calendar.add(Calendar.MONTH, -(months - 1));
        Date startTime = calendar.getTime();
        dateFormatter = new SimpleDateFormat("yyyy-MM");
        System.out.println("start time " + startTime);
        System.out.println("end time " + endTime);
        return getReportDataByRange(startTime, endTime, "months");
    }


    private List<ReportItem> getReportDataLastXDays(int days) {
        Calendar startCalendar = Calendar.getInstance();
        //subtract 1 year due since data used is 1 year old from time of development
        startCalendar.add(Calendar.YEAR, -1);
        Date endTime = startCalendar.getTime();

        Calendar calendar = Calendar.getInstance();
        //subtract 1 year due since data used is 1 year old from time of development
        calendar.add(Calendar.YEAR, -1);
        calendar.add(Calendar.DAY_OF_MONTH, -(days - 1));
        Date startTime = calendar.getTime();
        dateFormatter = new SimpleDateFormat("yyy-MM-dd");
        System.out.println("start time " + startTime);
        System.out.println("end time " + endTime);
        return getReportDataByRange(startTime, endTime, "days");
    }

    public List<ReportItem> getReportDataByRange(Date startTime, Date endTime) {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getReportDataByRange(startTime, endTime, "days");
    }

    public List<ReportItem> getReportDataByRange(Date startTime, Date endTime, String period) {
        List<Order> orderList = orderRepository.findByOrderTimeBetween(startTime, endTime);
        printRawData(orderList);
        List<ReportItem> reportItemList = createReportData(startTime, endTime, period);
        System.out.println();
        calculateSalesForReportData(orderList, reportItemList);
        printReportData(reportItemList);
        return reportItemList;
    }

    private void calculateSalesForReportData(List<Order> listOrders, List<ReportItem> listReportItems) {
        for (Order order : listOrders) {
            String orderDateString = dateFormatter.format(order.getOrderTime());

            ReportItem reportItem = new ReportItem(orderDateString);

            int itemIndex = listReportItems.indexOf(reportItem);

            if (itemIndex >= 0) {
                reportItem = listReportItems.get(itemIndex);

                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubTotal() - order.getProductCost());
                reportItem.increaseOrdersCount();
            }
        }
    }

    private List<ReportItem> createReportData(Date startTime, Date endTime, String period) {
        List<ReportItem> reportItemList = new ArrayList<>();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(startTime);

        Calendar endDate = Calendar.getInstance();
        endDate.setTime(endTime);

        Date currentDate = startDate.getTime();
        String dateString = dateFormatter.format(currentDate);

        reportItemList.add(new ReportItem(dateString));

        do {
            if (period.equals("days")) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            } else if (period.equals("months")) {
                startDate.add(Calendar.MONTH, 1);
            }

            currentDate = startDate.getTime();
            dateString = dateFormatter.format(currentDate);
            reportItemList.add(new ReportItem(dateString));
        } while (startDate.before(endDate));

        return reportItemList;
    }


    private void printReportData(List<ReportItem> listReportItems) {
        listReportItems.forEach(item -> {
            System.out.printf("%s, %10.2f, %10.2f, %d \n", item.getIdentifier(), item.getGrossSales(),
                    item.getNetSales(), item.getOrdersCount());
        });

    }

    private void printRawData(List<Order> listOrders) {
        listOrders.forEach(order -> {
            System.out.printf("%-3d | %s | %10.2f | %10.2f \n",
                    order.getId(), order.getOrderTime(), order.getTotal(), order.getProductCost());
        });
    }

}
