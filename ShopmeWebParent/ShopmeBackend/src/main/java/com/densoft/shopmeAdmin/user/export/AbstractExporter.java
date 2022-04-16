package com.densoft.shopmeAdmin.user.export;

import com.densoft.shopmecommon.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbstractExporter {

    public void setResponseHeader(HttpServletResponse response, String contentType, String extension) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormatter.format(new Date());
        String fileName = "users_" + timeStamp + extension;

        response.setContentType(contentType);
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
    }
}
