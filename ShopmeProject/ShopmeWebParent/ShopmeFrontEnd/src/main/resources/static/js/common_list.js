function clearFilter(){
	window.location = moduleUrl;
}

function handleDetailLink(linkClass, modalId){
	$(linkClass).on("click",function(e){
		e.preventDefault();
		linkDetailURL = $(this).attr("href");
		$(modalId).modal("show").find(".modal-content").load(linkDetailURL);
	});
}