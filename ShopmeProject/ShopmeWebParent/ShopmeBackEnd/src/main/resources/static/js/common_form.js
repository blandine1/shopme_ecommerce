$(document).ready(function(){
     $("#buttonCAncel").on("click", function(){
       window.location =moduleUrl;
       
     });
     
     $("#fileImage").change(function(){
         
         if(!checkFileSize(this)){
	        return;
           }
           showImageThumnail(this);
   });
});

function showImageThumnail(fileInput){
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e){
       $("#thumbnail").attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
} 

function checkFileSize(fileInput){
	 fileSize = fileInput.files[0].size;
         
  if(fileSize >  MAX_FILE_SIZE ){
   fileInput.setCustomValidity("faudra choisir un fichier qui pese moin de "+SIZE +" kbs");
   fileInput.reportValidity();
   return false;
    }else{
    fileInput.setCustomValidity("");
    return true;
    }
}


function showModalDialog(title, message){
   $("#modalTitle").text(title);
   $("#modalBody").text(message);
   $("#modalDialog").modal();
}

function showErorModal(message){
  showModalDialog("Error", message);
}

function showWarningModal(message){
  showModalDialog("Warning", message);
}