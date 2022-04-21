package com.densoft.shopmeAdmin.brand;

import com.densoft.shopmeAdmin.AbstractExporter;
import com.densoft.shopmecommon.entity.Brand;
import com.densoft.shopmecommon.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandCSVExporter extends AbstractExporter {

    public void export(List<Brand> brandList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "brands_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Brand ID", "Brand Name", "Categories"};
        String[] fieldMapping = {"id", "name", "categories"};
        csvBeanWriter.writeHeader(csvHeader);

        for (Brand brand : brandList) {
            csvBeanWriter.write(brand, fieldMapping);
        }
        csvBeanWriter.close();

    }
}
