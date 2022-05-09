package com.densoft.shopmeAdmin.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class AbstractReportService {

    protected DateFormat dateFormatter;

    public List<ReportItem> getReportDataLast7Days(ReportType reportType) {
        return getReportDataLastXDays(7, reportType);
    }

    public List<ReportItem> getReportDataLast28Days(ReportType reportType) {
        return getReportDataLastXDays(28, reportType);
    }

    public List<ReportItem> getReportDataLast6Months(ReportType reportType) {
        return getReportDataLastXMonths(6, reportType);
    }


    public List<ReportItem> getReportDataLastYear(ReportType reportType) {
        return getReportDataLastXMonths(12, reportType);
    }


    protected List<ReportItem> getReportDataLastXMonths(int months, ReportType reportType) {
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
        return getReportDataByRange(startTime, endTime, reportType);
    }


    protected List<ReportItem> getReportDataLastXDays(int days, ReportType reportType) {
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
        return getReportDataByRange(startTime, endTime, reportType);
    }


    protected abstract List<ReportItem> getReportDataByRange(Date startTime, Date endTime, ReportType reportType);
}
