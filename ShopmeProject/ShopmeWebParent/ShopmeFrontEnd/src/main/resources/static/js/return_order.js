
var returnModal;
var modalTitle;
var fieldNote;
var orderId;
var divReason;
var divMessage;
var firstButton;
var secondButton;

$(document).ready(function(){
  
  returnModal = $("#returnOrderModal");
  modalTitle = $("#returnOrderModelTitle");
  fieldNote = $("#returnedNote");
  divReason = $("#divReason");
  divMessage = $("#divMessage");
  firstButton = $("#firstButton");
  secondButton = $("#scondButton");
  
   handleReturnOrderLink();
});


function showReturnModalDialog(link){
	divMessage.hide();
	divReason.show();
	firstButton.show();
	secondButton.text("Cancel");
	fieldNote.val("");
	
	orderId = link.attr("orderId");
	returnModal.modal("show");
	modalTitle.text("Order Id #" + orderId);
}

function showMessageModal(message){
	divReason.hide();
	firstButton.hide();
	secondButton.text("Close");
	divMessage.text(message);
	
	divMessage.show();
}

function handleReturnOrderLink(){
	 $(".linkReturnedOrder").on("click",function(e){
		e.preventDefault();
		showReturnModalDialog($(this));
	});
}

function submitreturnOrderForm(){
	reason = $("input[name='returnReason']:checked").val();
	note = fieldNote.val();
	sendRequestOrderRequest(reason, note);
	
	return false;
}

function sendRequestOrderRequest(reason, note){
	requestUrl = contextPath + "orders/return";
	requestBody = {orderId: orderId, note: note};
	
	$.ajax({
		type: "POST",
		url: requestUrl,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(requestBody),
		contentType: "application/json"
	}).done(function(returnResponse){
		showMessageModal("Return request has ben send");
		updateStatusTextAndReturnButton(orderId);
	}).fail(function(err){
		showMessageModal(err.responseText);
	});
}

function updateStatusTextAndReturnButton(orderId){
	$(".textOrderStatus" + orderId).each(function(index){
		$(this).text("RETURN_REQUESTED");
	});
	
	$(".linkReturn" + orderId).each(function(index){
		$(this).hide();
	});
}
