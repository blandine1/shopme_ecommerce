var data;
var chartOptions;




$(document).ready(function(){
  setupButtonEventHandler('_product', loadSalesReportByDateForProduct);
});

function loadSalesReportByDateForProduct(period){
	if(period == "custom"){
		startDate = $("#startDate_product").val();
		endDate = $("#endDate_product").val();
		
		requestUrl = contextPath + "reports/product/" + startDate + "/" + endDate;
	}else{
		requestUrl = contextPath + "reports/product/" + period;
	}
	
	
	$.get(requestUrl, function (responseJSON){
		prepareChartDataForSaleReportByProduct(responseJSON);
		customizeChartForSaleReportByProduct();
		formatChartData(data, 2, 3);
		drawChartForSaleReportByProduct(period);
		setSalesAmount(period,'_product', "Total Products");
	});
}

function prepareChartDataForSaleReportByProduct(responseJSON){
	data = new google.visualization.DataTable();
	data.addColumn('string', 'Product');
	data.addColumn('number', 'Quantity');
	data.addColumn('number', 'Gross Sales');
	data.addColumn('number', 'Net Sales');
	
    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;
	
	$.each(responseJSON,function(index, reportItem){
		data.addRows([[reportItem.identifier,reportItem.productsCount, reportItem.grossSales, reportItem.netSales]]);
		totalGrossSales += parseFloat(reportItem.grossSales);
		totalNetSales += parseFloat(reportItem.netSales);
		totalItems += parseInt(reportItem.productsCount);
	});
}

function customizeChartForSaleReportByProduct(){
	chartOptions= {
		height: 360, 
		width: '80%',
		showRowNumber: true,
		page: 'enabled',
		sortColumn: 2,
		sortAscending: false
	};
}

function drawChartForSaleReportByProduct(){
	var salesChart = new google.visualization.Table(document.getElementById('chart_sales_by_product'));
	salesChart.draw(data, chartOptions);
	
}

