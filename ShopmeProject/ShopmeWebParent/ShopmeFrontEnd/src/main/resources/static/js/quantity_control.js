$(document).ready(function(){
	$(".linkMinus").click(function(evt){
		evt.preventDefault();
		productId = $(this).attr("pid");
		quantityInput = $("#quantity"+ productId);
		newQuantity = parseInt(quantityInput.val()) - 1;
		if(newQuantity > 0){
			quantityInput.val(newQuantity);
		}else{
			showWarningModal("minimun quantity is 1");
		}
	});
	
	$(".linkPlus").click(function(evt){
			evt.preventDefault();
		productId = $(this).attr("pid");
		quantityInput = $("#quantity"+ productId);
		newQuantity = parseInt(quantityInput.val()) + 1;
		if(newQuantity <=5 ){
			quantityInput.val(newQuantity);
		}else{
			showWarningModal("maximum quantity is 5");
		}
	});
});