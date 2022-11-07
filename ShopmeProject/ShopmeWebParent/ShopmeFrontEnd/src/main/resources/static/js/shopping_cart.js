decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';
thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.';

$(document).ready(function(){
	$(".linkMinus").on("click",function(evt){
		evt.preventDefault();
		decreaseQuantity( $(this));
	});
	
	$(".linkPlus").on("click",function(evt){
			evt.preventDefault();
	  increaseQuantity( $(this));
	});
	
	$(".linkRemove").on("click",function(evt){
			evt.preventDefault();
			removeProduct($(this));
	});

});

function decreaseQuantity(link){
	productId =link.attr("pid");
    quantityInput = $("#quantity"+ productId);
    newQuantity = parseInt(quantityInput.val()) - 1;
	 if(newQuantity > 0){
	    quantityInput.val(newQuantity);
	    updateQuantity(productId, newQuantity);
	 }else{
		showWarningModal("minimun quantity is 1");
	 }
}

function increaseQuantity(link){
	productId = link.attr("pid");
	quantityInput = $("#quantity"+ productId);
	newQuantity = parseInt(quantityInput.val()) + 1;
	   if(newQuantity <=5 ){
		  quantityInput.val(newQuantity);
		   updateQuantity(productId, newQuantity);
	    }else{
		  showWarningModal("maximum quantity is 5");
		}
}

function updateQuantity(productId, quantity){
	url = contextPath+ "cart/update/"+ productId +"/"+ quantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubTotal){
		updateSubTotal(updatedSubTotal, productId);
		updateTotal();
	}).fail(function(){
		showErorModal("Error while updating product quantity");
	});
}

function updateSubTotal(updatedSubTotal, productId){
	formatedSubTotal = $.number(updatedSubTotal, 2);
	$("#subtotal" + productId).text(formatedSubTotal);
}

function updateTotal(){
	total = 0.0;
	
	$(".subtotal").each(function(index, element){
		total += parseFloat(clearcCurrencyFormat(element.innerHTML));
	});
	
	$("#total").text(formatCurrency(total));
}

function removeProduct(link){
	url = link.attr("href");
	 
	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response){
		rowNumber = link.attr("rowNumber");
		removeHtml(rowNumber);
		updateTotal();
		
		updateCountNumbers();
		
		showModalDialog("Shopping cart ", response);
	}).fail(function(){
		showErorModal("Error while removing  product ");
	});
}

function removeHtml(rowNumber){
	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();
}

function updateCountNumbers(){
	$(".divCount").each(function(index, element){
		element.innerHTML = ""+ (index + 1);
	});
}

function formatCurrency(amount){
	return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}

function clearcCurrencyFormat(numberString){
	result = numberString.replaceAll(thousandsSeparator, "");
	return result.replaceAll(decimalSeparator, '.');
}

