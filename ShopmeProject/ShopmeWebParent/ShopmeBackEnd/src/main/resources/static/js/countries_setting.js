var buttonLoad;
var dropDownCountry;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(function(){
	buttonLoad = $("#buttonLoadCountries"); 
	dropDownCountry = $("#dropDownCountries");
	buttonAddCountry = $("#buttonAddCountry");
	buttonUpdateCountry = $("#buttonUpdateCountry");
	buttonDeleteCountry = $("#buttonDeleteCountry");
	labelCountryName = $("#labelCountryName");
	fieldCountryName = $("#fieldCountryName");
	fieldCountryCode = $("#fieldCountryCode");
	
	buttonLoad.click(function(){
		 loadCountries();
	});
	
	dropDownCountry.on("change", function(){
		changeFormStateToSelectedCountry();
	});
	
	buttonAddCountry.click(function(){
		if(buttonAddCountry.val() == "Add"){
			addCountry();
		}else{
			changeFormStateToNew();
		}
	});
	
	buttonUpdateCountry.click(function(){
		updateCountry();
	});
	
	buttonDeleteCountry.click(function(){
		 deleteCountry();
	});
});

function deleteCountry(){
	optionValue = dropDownCountry.val();
	countryId = optionValue.split("-")[0];
	url = contextPath + "countries/delete/"+countryId;
	$.ajax({
		type : 'DELETE',
		url : url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
		
	}).done(function(){
		 $("#dropDownCountries option[value = '" + optionValue +"']").remove();
		  changeFormStateToNew();
		  buttonLoad.val("Refresh Load button");
		  showToastMessage("country has been deleted");
	}).fail(function(){
		showToastMessage("ERROR : could not connect to server or the server encountered an error while processing your request");
	});
}

function validateFormCountry(){
	formCountry = document.getElementById("formCountry");
	if(!formCountry.checkValidity()){
		formCountry.reportValidity();
		return false;
	}
	return true;
}


function updateCountry(){
	
	if(!validateFormCountry()) return;
	
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	
	 countryId  =dropDownCountry.val().split("-")[0];
	 
	jsonData = {id : countryId, name : countryName, code: countryCode};
	
	$.ajax({
		type : 'POST',
		url : url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId){
		 $("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
	     $("#dropDownCountries option:selected").text(countryName);
		showToastMessage("country "+ countryName +" has been updated");
		
		changeFormStateToNew();
	}).fail(function(){
		showToastMessage("ERROR : could not connect to server or the server encountered an error while processing your request");
	});
}


function addCountry(){
	
	if(!validateFormCountry()) return;
	
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	jsonData = {name : countryName, code: countryCode};
	
	$.ajax({
		type : 'POST',
		url : url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId){
		selectNewlyAddedCountry(countryId, countryName, countryCode);
		showToastMessage("country "+ countryName +" has been added");
	});
	
}

//on veut ici marquer par defaut le nouvel enregistrement
function selectNewlyAddedCountry(countryId, countryName, countryCode){
	optionValue = countryId + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);
	
	$("#dropDownCountries option[value = '" + optionValue +"']").prop("selected", true);
	
	fieldCountryCode.val("");
	fieldCountryName.val("").focus();
}


function changeFormStateToNew(){
	buttonAddCountry.val("Add");
	labelCountryName.text("country name");
	fieldCountryCode.val("");
	fieldCountryName.val("").focus();
	
	buttonUpdateCountry.prop("disabled", true);
	buttonDeleteCountry.prop("disabled", true);
}

function changeFormStateToSelectedCountry(){
	buttonAddCountry.prop("value", "new");
	buttonUpdateCountry.prop("disabled", false);
	buttonDeleteCountry.prop("disabled", false);
	
	labelCountryName.text("Selected Country :");
	
	selectTedCountryName = $("#dropDownCountries option:selected").text();
	fieldCountryName.val(selectTedCountryName);
	
	countryCode = dropDownCountry.val().split("-")[1];
	fieldCountryCode.val(countryCode);
}

function loadCountries(){
	
	url = contextPath + "countries/list";
	$.get(url, function(response){
		 dropDownCountry.empty();
		 
		 $.each(response, function(index, country){
			optionValue = country.id + "-" + country.code;
			
			$("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
		});
	}).done(function(){
		buttonLoad.val("Refresh Load button");
		showToastMessage("all countries have been loaded");
	}).fail(function(){
		showToastMessage("ERROR : could not connect to server or the server encountered an error while processing your request");
	});
}

function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}