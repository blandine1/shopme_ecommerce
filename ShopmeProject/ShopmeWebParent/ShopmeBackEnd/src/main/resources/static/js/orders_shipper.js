var iconNames= {
	'PICKED': 'fa-people-carry',
	'SHIPPING': 'fa-shipping-fast',
	'DELIVERED': 'fa-box-open',
	'RETURNED': 'fa-undo'
};

var confirmText;
var confirmModalDialog;
var yesButton;
var noButton;

$(document).ready(function(){
	confirmText = $("#confirmText");
	confirmModalDialog = $("#confirmModal");
	yesButton = $("#yesButton");
	noButton = $("#noButton");
	
    $(".linkUpdateStatus").on("click", function(e){
	   e.preventDefault();
	   link = $(this);
	   showUpdateConfirmModal(link);
    });
    
    addEventHandlerForYesButton();
});

function addEventHandlerForYesButton(){
	yesButton.click(function(e){
		e.preventDefault();
		sendRequestToUpdateOrderStatus($(this));
	})
}

function sendRequestToUpdateOrderStatus(button){
		requestUrl = button.attr("href");
		
		$.ajax({
		type : 'POST',
		url : requestUrl,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
	}).done(function(response){
		showMessageModal("Order is updated successfully");
		updateStatusIconColor(response.orderId, response.status);
	}).fail(function(err){
		showMessageModal("Error updating Order status");
	})
}

function updateStatusIconColor(orderId, status){
	link = $("#link" + status + orderId);
	link.replaceWith("<i class='fas " + iconNames[status] +" fa-2x icon-green'></i>");
}

function showUpdateConfirmModal(link){
	noButton.text("no");
	yesButton.show();
	
	orderId = link.attr("orderId");
	status = link.attr("status");
	yesButton.attr("href", link.attr("href"));
	
	confirmText.text("are you sure you want to update status of order ID #" +orderId + " to "+ status + "?");
	
	confirmModalDialog.modal();
}

function showMessageModal(message){
	noButton.text("close");
	yesButton.hide();
	confirmText.text(message);
}