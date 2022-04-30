

dropDownBrands = $("#brand");
 dropDownCategories = $("#category");
 
$(document).ready(function(){
 

   $("#shortDescription").richText();
   $("#fullDescription").richText();
   
   dropDownBrands.change(function(){
      dropDownCategories.empty();
      getCategories();
   });
   getCategoriesForNewForm();
   
});

function  getCategoriesForNewForm(){
	catIdFiled = $("#categoryId");
	editMode = false; //a ce niveau on appelle la fonction editMode par defaut qui est fausse
	
	if(catIdFiled.length){
		editMode = true;//celui ci dit qu'on est en edit mode puisque catIdFileel contient une valeur
		//alert(catIdFiled.length);
	}
	//alert(editMode); ici editMode est a vrai
	if(!editMode) getCategories();//si on est pas en mise a jour alors charge les categories
}

function getCategories(){
  brandId = dropDownBrands.val();
  url = brandsModuleUrl + "/" + brandId + "/categories";
  
  $.get(url, function(responseJson){
     $.each(responseJson, function(index, category){
        $("<option>").val(category.id).text(category.name).appendTo(dropDownCategories);
     });
  });
}

function  checkUnique(form){
      pName = $("#name").val();
      pId = $("#id").val();
      
      csrfValue = $("input[name='_csrf']").val();
      
      params = {id : pId, name : pName, _csrf: csrfValue};
      
      $.post(checkProductUniqueUrl, params, function(response){
         if(response == "ok"){
            form.submit();
         }else if(response =="Duplicate"){
            showWarningModal("this product with name "+pName+" is already in use");
         }else{
           showErorModal("unkwon error form server");
         }
      }).fail(function(){
         showErorModal("could not connect to the server ");
      });
      
      return false;
}