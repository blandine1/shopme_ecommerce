$(document).ready(function(){
	$("#buttonAddToCart").on("click",function(e){
		addToCart();
	});
});

function addToCart(){
	quantity = $("#quantity" + productId).val();
	
	url = contextPath+ "cart/add/"+ productId +"/"+ quantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response){
		showModalDialog("Shopping cart", response);
	}).fail(function(){
		showErorModal("Error while adding product to your shopping cart");
	});
}