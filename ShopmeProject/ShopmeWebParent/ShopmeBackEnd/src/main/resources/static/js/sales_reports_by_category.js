var data;
var chartOptions;




$(document).ready(function(){
  setupButtonEventHandler('_category', loadSalesReportByDateForCategory);
});

function loadSalesReportByDateForCategory(period){
	if(period == "custom"){
		startDate = $("#startDate_category").val();
		endDate = $("#endDate_category").val();
		
		requestUrl = contextPath + "reports/category/" + startDate + "/" + endDate;
	}else{
		requestUrl = contextPath + "reports/category/" + period;
	}
	
	
	$.get(requestUrl, function (responseJSON){
		prepareChartDataForSaleReportByCategory(responseJSON);
		customizeChartForSaleReportByCategory();
		formatChartData(data, 1, 2);
		drawChartForSaleReportByCategory(period);
		setSalesAmount(period,'_category', "Total Products");
	});
}

function prepareChartDataForSaleReportByCategory(responseJSON){
	data = new google.visualization.DataTable();
	data.addColumn('string', 'Category');
	data.addColumn('number', 'Gross Sales');
	data.addColumn('number', 'Net Sales');
	
    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalItems = 0;
	
	$.each(responseJSON,function(index, reportItem){
		data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
		totalGrossSales += parseFloat(reportItem.grossSales);
		totalNetSales += parseFloat(reportItem.netSales);
		totalItems += parseInt(reportItem.productsCount);
	});
}

function customizeChartForSaleReportByCategory(){
	chartOptions= {
		height: 360, legend: {position: 'right'}
	};
}

function drawChartForSaleReportByCategory(){
	var salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
	salesChart.draw(data, chartOptions);
	
}

