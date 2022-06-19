var buttonLoadCountries4States;
var dropdownCountries4State;
var dropdownStates;
var labelStateName;
var fieldStateName;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;

$(document).ready(function(){
	buttonLoadCountries4States= $("#buttonLoadCountriesForStates");
	dropdownCountries4State = $("#dropdownCountriesForState");
	dropdownStates = $("#dropdownStates");
	labelStateName = $("#labelStateName");
	fieldStateName = $("#fieldStateName");
	buttonAddState = $("#buttonAddState");
	buttonUpdateState = $("#buttonUpdateState");
	buttonDeleteState = $("#buttonDeleteState");
	
	buttonLoadCountries4States.click(function(){
		 loadCountries4State();
	});
	
	dropdownCountries4State.on("change", function(){
		loadState4Country();
	});
	
	dropdownStates.on("change", function(){
		changeFormStateToSelectedState();
	});
	
	buttonAddState.click(function(){
		if(buttonAddState.val() == "Add"){
			addState();
		}else{
			changeFormState();
		}
	});
	
	buttonUpdateState.click(function(){
		updateState();
	});
	
	buttonDeleteState.click(function(){
		deleteState();
	});
});

 function updateState(){
	url = contextPath + "states/save";
	stateId = dropdownStates.val();
	stateName = fieldStateName.val();
	
	countrySelected = $("#dropdownCountriesForState option:selected");
	countryId = countrySelected.val();
	countryName = countrySelected.text();
	
	jsonData = {id: stateId,name : stateName, country : {id: countryId, name:countryName }};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data : JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(){
		$("#dropdownStates option:selected").text(stateName);
		changeFormState();
		showTostMessage("updataed successfully");
	}).fail(function(){
		showTostMessage("ERROR: could not connect to the server or the server encountered an error while processing your request");
	})
}

function deleteState(){
	stateId = $("#dropdownStates option:selected").val();
	url = contextPath + "states/delete/" + stateId;
	$.ajax({
		type : 'DELETE',
		url : url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
		
	}).done(function(){
		$("#dropdownStates option[value='" + stateId+"']").remove();
		changeFormState();
		showTostMessage("state has been successfully deleted");
	}).fail(function(){
		showTostMessage("ERROR: could not connect to the server or the server encountered an error while processing your request");
	});
}

 function addState(){
	url = contextPath + "states/save";
	selectedCountry = $("#dropdownCountriesForState option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();
	stateName = fieldStateName.val();
	
	jsonData = {name : stateName, country : {id: countryId, name: countryName}};
	
	$.ajax({
		type : 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId, stateName){
		selectNewlyAddedSate(stateId,stateName );
		showTostMessage("state has been successfully added");
	}).fail(function(){
		showTostMessage("ERROR: could not connect to the server or the server encountered an error while processing your request");
	});
}

function selectNewlyAddedSate (stateId, stateName){
	
	$("<option>").val(stateId).text(stateName).appendTo(dropdownStates);
	
	$("#dropdownStates option[value ='"+ stateId +"']").prop("selected", true);

	fieldStateName.val("").focus();
}

function changeFormStateToSelectedState(){
	buttonAddState.val("New");
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);
	
	labelStateName.text("selected state/province");
	selectedStateName = $("#dropdownStates option:selected").text();
	fieldStateName.val(selectedStateName);
}
 
function loadState4Country(){
	selectedCountry = $("#dropdownCountriesForState option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "states/list_by_country/"+ countryId;
	
	$.get(url, function(response){
		dropdownStates.empty();
		
		$.each(response, function(index, state){
			$("<option>").val(state.id).text(state.name).appendTo(dropdownStates);
		});
	}).done(function(){
		changeFormState();
		showTostMessage("all states for "+ selectedCountry.text() +" has been loaded successfully");
	}).fail(function(){
		showTostMessage("ERROR: could not connect to the server or the server encountered an error while processing your request");
	});
}

//ici on charge touts les villes par pays (dabord cette method avant tout)
function loadCountries4State(){
	url = contextPath + "countries/list";
	
	$.get(url, function(response){
		dropdownCountries4State.empty();
		
		$.each(response, function(index, country){
			$("<option>").val(country.id).text(country.name).appendTo(dropdownCountries4State);
		});
	}).done(function(){
		buttonLoadCountries4States.val("refresh country list");
		showToastMessage("all countries has been loaded");
	}).fail(function(){
		showToastMessage("Error: could not connect to the server or the server encountered and error while processing to your request ");
	});
}
function changeFormState(){
	buttonAddState.val("Add");
	fieldStateName.val("").focus();
	
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);
}

function showTostMessage(message){
 $("#toastMessage").text(message);
 $(".toast").toast('show');	
}