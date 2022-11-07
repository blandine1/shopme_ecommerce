package com.shopme.admin.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.order.OrderDetailRepository;
import com.shopme.common.entity.order.OrderDetail;

@Service
public class OrderDetailReportService extends AbstractReportService {
  
	@Autowired private OrderDetailRepository orderRepository;
	
   
	@Override
	protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) {
		List<OrderDetail> listOrderDatails = null;
		
		if(reportType.equals(ReportType.CATEGORY)){
			listOrderDatails = orderRepository.findWithCategoryAndTimeBetween(startDate, endDate);
		}else if(reportType.equals(ReportType.PRODUCT)) {
			listOrderDatails = orderRepository.findWithProductAndTimeBetween(startDate, endDate);
		}
		
		//printRawData(listOrderDatails);
		
		List<ReportItem> listReportItems = new ArrayList<>();
		
		for(OrderDetail detail : listOrderDatails) {
			String identifier = "";
			if(reportType.equals(ReportType.CATEGORY)) {
				identifier = detail.getProduct().getCategory().getName();
			}else if(reportType.equals(ReportType.PRODUCT)) {
				identifier = detail.getProduct().getShortName();
			}
			
			ReportItem reportItem = new ReportItem(identifier);
			
			float grossSales = detail.getSubtotal() - detail.getShippingCost();
			float netSales = detail.getSubtotal() - detail.getProductCost();
			
			int itemIndex = listReportItems.indexOf(reportItem);
			
			if(itemIndex >= 0) {
				reportItem = listReportItems.get(itemIndex);
				reportItem.addGrossSale(grossSales);
				reportItem.addNetSales(netSales);
				reportItem.increaseProductCount(detail.getQuantity());
			}else {
				listReportItems.add(new ReportItem(identifier, grossSales, netSales, detail.getQuantity()));
			}
		}
		
		//printReportdata(listReportItems);
		
		return listReportItems;
	}

	private void printReportdata(List<ReportItem> listReportItems) {
		
		for(ReportItem item : listReportItems) {
			System.out.printf("%-20s, %10.2f, %10.2f, %d \n",
					item.getIdentifier(), item.getGrossSales(), item.getNetSales(), item.getProductsCount());
		}
		
	}


	private void printRawData(List<OrderDetail> listOrderDatails) {
		
		for(OrderDetail o : listOrderDatails) {
			System.out.printf("%d, %-20s, %10.2f, %10.2f, %10.2f \n",
					o.getQuantity(), o.getProduct().getShortName().substring(0, 20), o.getSubtotal(), o.getProductCost(),
					o.getShippingCost());
		}
	}

}
