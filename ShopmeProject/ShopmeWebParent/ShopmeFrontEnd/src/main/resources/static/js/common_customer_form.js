
var dropDownCountry;
var dataListState;
var fieldState;

$(document).ready(function(){
  dropDownCountry = $("#country");
  dataListState= $("#listState");
  fieldState = $("#state");
  dropDownCountry.on("change",function(){
     loadStateForCountry();
     fieldState.val("").focus();
  });
  
  function loadStateForCountry(){
     selectedCountry = $("#country option:selected");
     countryId = selectedCountry.val();
     
     url = contextPath + "settings/list_states_by_country/" + countryId;
     
     $.get(url, function(response){
        dataListState.empty();
        
        $.each(response, function(index, state){
           $("<option>").val(state.name).text(state.name).appendTo(dataListState)
        });
        
     });
  }
  
});

function checkPasswordMatch(confirmPassword){
   if(confirmPassword.value != $("#password").val()){
     confirmPassword.setCustomValidity("password do not match");
   }else{
     confirmPassword.setCustomValidity("");
   }
}
