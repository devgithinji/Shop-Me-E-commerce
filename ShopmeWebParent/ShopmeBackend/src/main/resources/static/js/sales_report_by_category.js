// Sales Report by Category
let categoryData;
let categoryChartOptions;

$(document).ready(function() {
    setupButtonEventHandlers("_category", loadSalesReportByDateForCategory);
});

function loadSalesReportByDateForCategory(period) {
    let startDate;
    let endDate;
    let requestURL;
    if (period == "custom") {
        startDate = $("#startDate_category").val();
        endDate = $("#endDate_category").val();

        requestURL = contextPath + "reports/category/" + startDate + "/" + endDate;
    } else {
        requestURL = contextPath + "reports/category/" + period;
    }

    $.get(requestURL, function(responseJSON) {
        prepareChartDataForSalesReportByCategory(responseJSON);
        customizeChartForSalesReportByCategory();
        formatChartData(categoryData, 1, 2);
        drawChartForSalesReportByCategory(period);
        setSalesAmount(period, '_category', "Total Products");
    });
}

function prepareChartDataForSalesReportByCategory(responseJSON) {
    categoryData = new google.visualization.DataTable();
    categoryData.addColumn('string', 'Category');
    categoryData.addColumn('number', 'Gross Sales');
    categoryData.addColumn('number', 'Net Sales');

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;

    $.each(responseJSON, function(index, reportItem) {
        categoryData.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalItems += parseInt(reportItem.productsCount);
    });
}

function customizeChartForSalesReportByCategory() {
    categoryChartOptions = {
        height: 360, legend: {position: 'right'}
    };
}

function drawChartForSalesReportByCategory() {
    let salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
    salesChart.draw(categoryData, categoryChartOptions);
}