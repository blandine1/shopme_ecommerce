$(document).ready(function(){
    $("#products").on("click", "#linkAddProduct", function(e){
	  e.preventDefault();
	  link = $(this);
	  url = link.attr("href");
	  
	  $("#addProductModal").on("shown.bs.modal", function(){
		$(this).find("iframe").attr("src", url);
   	  });
	  
	  $("#addProductModal").modal();
	
    });
});

function addProduct(productId,productName){
	 getShippingCost(productId);
}

function getShippingCost(productId){
	selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();
	state = $("#state").val();
	
	if(state.length == 0){
		state = $("#city").val();
	}
	
	requestUrl = contextPath + "get_shipping_cost";
	params = {productId : productId, countryId : countryId, state:state};
	
	$.ajax({
		type : 'POST',
		url : requestUrl,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data:params,
	}).done(function(shippingCost){
		 getProductInfo(productId, shippingCost);
	}).fail(function(err){
		showWarningModal(err.responseJSON.message);
		shippingCost = 0.0;
		 getProductInfo(productId, shippingCost);
	}).always(function(){
		 $("#addProductModal").modal("hide");
	});
}

function getProductInfo(productId, shippingCost){
	requestUrl = contextPath + "products/get/" + productId;
	
	$.get(requestUrl, function(productJson){
		console.log(productJson);
		productName = productJson.name;
		mainImagePath =contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
		productCost = $.number(productJson.cost, 2);
		productPrice = $.number(productJson.price, 2);
		shippingCost = $.number(shippingCost, 2);
		
		htmlCode = generateProductCode(productId, productName, mainImagePath,shippingCost, productCost, productPrice);
        $("#productList").append(htmlCode);
        
	}).fail(function(err){
		showWarningModal(err.responseJSON.message);
	});
}

function generateProductCode(productId, productName, mainImagePath, shippingCost, productCost, productPrice){
	nextCount = $(".hiddenProductId").length + 1;
	quantityId = "quantity" + nextCount;
	priceId = "price" + nextCount;
	subtotalId = "subtotal" + nextCount;
	
	htmlCode = `
	         <div class="border rounded">
           <input type="hidden" name="productId" value="${productId}" class="hiddenProductId"/>
             <div class="row">
               <div class="col-1 ml-1">
                  <div>${nextCount}</div>
               </div>
               <div class="col-3 pt-1">
                  <img  src="${mainImagePath}" class="img-fluid" />
               </div>
             </div>
             
             <div class="row m-2">
                <b>${productName}</b>                
             </div>
             
             <div class="row m-2">
               <table>
                  <tr>
                     <td>Product coast : </td>
                     <td>
                       <input type="text" required class="form-control  cost-input" 
                              rowNumber="${nextCount}" 
                              value="${productCost}" style="max-width: 140px"/>
                     </td>
                  </tr>
                  <tr>
                     <td>Quantity : </td>
                     <td>
                       <input type="number" step="1" min="1" mx="5" required class="form-control quantity-input"
                             rowNumber="${nextCount}" 
                             id="${quantityId}"
                             value="1" style="max-width: 140px"/>
                     </td>
                  </tr>
                  <tr>
                     <td>Unit Price : </td>
                     <td>
                       <input type="text" required class="form-control price-input" 
                             id="${priceId}"
                             rowNumber="${nextCount}" 
                             value="${productPrice}" style="max-width: 140px"/>
                     </td>
                  </tr>
                  <tr>
                     <td>SubTotal : </td>
                     <td>
                       <input type="text" readonly class="form-control subtotal-output"
                             id="${subtotalId}" 
                             value="${productPrice}" style="max-width: 140px"/>
                     </td>
                  </tr>
                  <tr>
                     <td>Shipping cost : </td>
                     <td>
                       <input type="text" required class="form-control ship-input" 
                             value="${shippingCost}" style="max-width: 140px" />
                     </td>
                  </tr>
               </table>
             </div>

           </div>
           <div>&nbsp;</div>
	`;
	
	return htmlCode;
	
}

function isProductAlreadyAdded(productId){
	
	productExists = false;
	$(".hiddenProductId").each(function(e){
		aProductId = $(this).val();
		
		if(aProductId == productId){
			productExists = true;
			return;
		}
	});
	
	return productExists;
}
