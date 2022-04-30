function clearFilter(){
	window.location = moduleUrl;
}

function showDeleteConfirmModal(link, entityName){
	entityId = link.attr("entityId");
	
	$("#yesButton").attr("href", link.attr("href"));
	$("#confirmText").text("are you sure you want to delete this "+entityName+ " ID " +entityId+" ?");
	
	$("#confirmModal").modal();
}