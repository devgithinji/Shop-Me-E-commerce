package com.densoft.shopmeAdmin.category.exporter;

import com.densoft.shopmeAdmin.AbstractExporter;
import com.densoft.shopmecommon.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryCSVExporter extends AbstractExporter {

    public void export(List<Category> categoryList, HttpServletResponse response) throws IOException {

        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Category ID", "Category Name"};
        String[] fieldMapping = {"id", "name"};
        csvBeanWriter.writeHeader(csvHeader);

        for (Category category : categoryList) {
            category.setName(category.getName().replace("--", "  "));
            csvBeanWriter.write(category, fieldMapping);
        }
        csvBeanWriter.close();

    }
}
