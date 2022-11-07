package com.shopme.admin.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportRestController {
	
	@Autowired private MasterOrderReportService masterOrderReportService;
	@Autowired private OrderDetailReportService detailReportService;
	
	@GetMapping("/reports/sales_by_date/{period}")
	public List<ReportItem> getReportDataByDatePeriod(@PathVariable("period") String period){
		
		System.out.println("printing period : " + period );
		
		switch(period) {
		case  "last_7_days":
			return masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
		
		case  "last_28_days":
			return masterOrderReportService.getReportDataLast28Days(ReportType.DAY);
			
		case  "last_6_months":
			return masterOrderReportService.getReportDataLast6Months(ReportType.MOUNTH);
			
		case  "last_year":
			return masterOrderReportService.getReportDataLastYear(ReportType.MOUNTH);
			
		
		default :
			return masterOrderReportService.getReportDataLast7Days(ReportType.DAY);
		}
	}
	
	@GetMapping("/reports/sales_by_date/{startDate}/{endDate}")
	public List<ReportItem> getReportDataByDatePeriod(@PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) throws ParseException{
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		Date endTime = dateFormatter.parse(endDate);
		
		return masterOrderReportService.getReportDataByDateRange(startTime, endTime, ReportType.DAY);
	}
	
	@GetMapping("/reports/{groupBy}/{startDate}/{endDate}")
	public List<ReportItem> getReportDataByCategoryOrProductDateRange(@PathVariable("groupBy") String groupBy, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) throws ParseException{
		ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse(startDate);
		Date endTime = dateFormatter.parse(endDate);
		
		return detailReportService.getReportDataByDateRange(startTime, endTime, reportType);
	}
	
	
	@GetMapping("/reports/{groupBy}/{period}")
	public List<ReportItem> getReportDataByCategoryOrProduct(@PathVariable("groupBy") String groupBy, @PathVariable("period") String period){
		ReportType reportType = ReportType.valueOf(groupBy.toUpperCase());
		
		switch(period) {
		     case  "last_7_days":
			     return detailReportService.getReportDataLast7Days(reportType);
		
		     case  "last_28_days":
			     return detailReportService.getReportDataLast28Days(reportType);
			
		     case  "last_6_months":
			     return detailReportService.getReportDataLast6Months(reportType);
			
		     case  "last_year":
			     return detailReportService.getReportDataLastYear(reportType);
		
		     default :
			     return detailReportService.getReportDataLast7Days(reportType);
		}
	}

}
