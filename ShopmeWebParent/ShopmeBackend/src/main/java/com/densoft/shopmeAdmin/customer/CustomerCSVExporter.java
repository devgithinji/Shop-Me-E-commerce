package com.densoft.shopmeAdmin.customer;

import com.densoft.shopmeAdmin.AbstractExporter;
import com.densoft.shopmecommon.entity.Customer;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CustomerCSVExporter extends AbstractExporter {

    public void export(List<Customer> customerList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "customers_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Customer ID", "First Name", "Last Name", "City", "State", "Country"};
        String[] fieldMapping = {"id", "firstName", "lastName", "city", "state", "country"};
        csvBeanWriter.writeHeader(csvHeader);

        for (Customer customer : customerList) {
            csvBeanWriter.write(customer, fieldMapping);
        }
        csvBeanWriter.close();

    }
}
