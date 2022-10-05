$(document).ready(function(){
	$("#productList").on("click",".linkRemove",function(e){
		e.preventDefault();
		
		if(doesOrderHaveOnlyOneProduct()){
			showWarningModal("could not be remove. The Order should Have at least One product");
		}else{
			removeProduct($(this));
			updateOrderAmounts();
		}
		
	});
});

function removeProduct(link){
	rowNumber = link.attr("rowNumber");
	$("#row" + rowNumber).remove();
	$("#blankLine").remove();
	
	$(".divCount").each(function(index, element){
		element.innerHTML = "" + (index + 1);
	});
}

function doesOrderHaveOnlyOneProduct(){
	productCount = $(".hiddenProductId").length;
	return productCount == 1;
}