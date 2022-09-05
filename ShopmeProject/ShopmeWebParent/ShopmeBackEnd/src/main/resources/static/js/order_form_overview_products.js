var fieldProductCost;
var fieldSubTotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

$(document).ready(function(){
	
	fieldProductCost= $("#productCoast");
	fieldSubTotal= $("#subtotal");
	fieldShippingCost= $("#shippingCost");
	fieldTax= $("#tax");
	fieldTotal= $("#total");
	
	formatOrderAmounts();
	formatProductAmount();
	
	$("#productList").on("change",".quantity-input", function(e){
		updateSubTotalWhenQuantityChange($(this));
		updateOrderAmounts();
	});
	
	$("#productList").on("change",".price-input", function(e){
		updateSubTotalWhenPriceChange($(this));
		updateOrderAmounts();
	});
	
	$("#productList").on("change",".cost-input", function(e){
		updateOrderAmounts();
	});
	
	$("#productList").on("change",".ship-input", function(e){
		updateOrderAmounts();
	});
});

function updateOrderAmounts(){
	totalCost = 0.0;
	
	$(".cost-input").each(function(e){
		costInputField = $(this);
		rowNumber = costInputField.attr("rowNumber");
		quantityValue = $("#quantity" +rowNumber).val();
		
		productCost = getNumberValueRemoveThousandSeparator(costInputField);
		totalCost += productCost * parseInt(quantityValue);
	});
	
	setAndFormatNumberForField("productCost", totalCost);
	
	orderSubTotal = 0.0;
	
	$(".subtotal-output").each(function(e){
		productSubTotal = getNumberValueRemoveThousandSeparator($(this));
		orderSubTotal += productSubTotal; 
	});
	
	setAndFormatNumberForField("subtotal", orderSubTotal);
	
	shippingCost = 0.0;
	
	$(".ship-input").each(function(e){
		productShip = getNumberValueRemoveThousandSeparator($(this));
		shippingCost += productShip; 
	});
	
	setAndFormatNumberForField("shippingCost", shippingCost);
	
	tax =getNumberValueRemoveThousandSeparator(fieldTax);
	orderTotal= orderSubTotal + tax + shippingCost; 
	setAndFormatNumberForField("total", orderTotal);
}

function setAndFormatNumberForField(fieldId, fieldValue){
	formatttedValue = $.number(fieldValue, 2),
	$("#" + fieldId).val(formatttedValue);
}

function getNumberValueRemoveThousandSeparator(fieldRef){
	fieldValue = fieldRef.val().replace(",", "");
	return parseFloat(fieldValue);
}

function updateSubTotalWhenPriceChange(input){
	priceValue = getNumberValueRemoveThousandSeparator(input);
	rowNumber = input.attr("rowNumber");
	
	quantityField = $("#quantity" + rowNumber);
	quantityValue = quantityField.val();
	newSubTotal= parseFloat(quantityValue) * priceValue;
	
	setAndFormatNumberForField("subtotal" + rowNumber, newSubTotal);
}

function updateSubTotalWhenQuantityChange(input){
	quantityValue = input.val();
	rowNumber = input.attr("rowNumber");
	priceField = $("#price" +rowNumber);
	priceValue = getNumberValueRemoveThousandSeparator($("#price" +rowNumber));
	newSubTotal = parseFloat(quantityValue) * priceValue;
	
	setAndFormatNumberForField("subtotal" + rowNumber, newSubTotal);
}

function formatProductAmount(){
	$(".cost-input").each(function(e){
		formatNumberForField($(this));
	});
	
	$(".price-input").each(function(e){
		formatNumberForField($(this));
	});
	
	$(".subtotal-output").each(function(e){
		formatNumberForField($(this));
	});
	
	$(".ship-input").each(function(e){
		formatNumberForField($(this));
	});
}

function formatOrderAmounts(){
	formatNumberForField(fieldProductCost);
	formatNumberForField(fieldSubTotal);
	formatNumberForField(fieldShippingCost);
	formatNumberForField(fieldTax);
	formatNumberForField(fieldTotal);
}

function formatNumberForField(fieldRef){
	fieldRef.val($.number(fieldRef.val(), 2));
}