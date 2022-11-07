
var dropdownStates;
var dropdowCountries;

$(document).ready(function(){
    dropdowCountries= $("#country");
    dropdownStates = $("#listStates");
    
	dropdowCountries.on("change", function(){
		loadState4Country();
		$("#state").val("").focus();
	});
    loadState4Country();
});

function loadState4Country(){
	selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "states/list_by_country/"+ countryId;
	
	$.get(url, function(response){
		dropdownStates.empty();
		
		$.each(response, function(index, state){
			$("<option>").val(state.id).text(state.name).appendTo(dropdownStates);
		});
	}).fail(function(){
		showToastMessage("ERROR: could not connect to the server or the server encountered an error while processing your request");
	});
}