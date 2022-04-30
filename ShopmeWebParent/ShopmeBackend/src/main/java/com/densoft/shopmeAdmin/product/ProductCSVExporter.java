package com.densoft.shopmeAdmin.product;

import com.densoft.shopmeAdmin.AbstractExporter;
import com.densoft.shopmecommon.entity.Customer;
import com.densoft.shopmecommon.entity.Product;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ProductCSVExporter extends AbstractExporter {

    public void export(List<Product> productList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "products_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Product ID", "Name", "Brand", "Category", "Enabled"};
        String[] fieldMapping = {"id", "name", "brand", "category", "enabled"};
        csvBeanWriter.writeHeader(csvHeader);

        for (Product product : productList) {
            csvBeanWriter.write(product, fieldMapping);
        }
        csvBeanWriter.close();

    }
}
