
extraImageCount = 0;
 
$(document).ready(function(){
 
   $("input[name='extraImage']").each(function(index){
	extraImageCount++;
	  $(this).change(function(){
		
		 if(!checkFileSize(this)){
	        return;
           }
		 showExtraImageThumnail(this, index);
	});
   });
   
   $("a[name='linkRemoveExtraImage']").each(function(index){
	  $(this).click(function(){
		 removeExtraImage(index);
	});
   });
  
});

function showExtraImageThumnail(fileInput, index){
	 var file = fileInput.files[0];
	 
	 fileName = file.name;
	 imageNameHiddenFile = $("#imageName" + index);
	 
	 if(imageNameHiddenFile.length){
		imageNameHiddenFile.val(fileName);
	 }
	 
    var reader = new FileReader();
    reader.onload = function(e){
       $("#extraThumbnail" + index).attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
    
    if(index >= extraImageCount - 1){
      addNextExtraImageThumbnailSection(index + 1);
    }
}

function addNextExtraImageThumbnailSection(index){
	htmlExtraImage = `
	    <div class="col border m-3 p-2" id="divExtraImage${index}">
         <div id="extraImageHeader${index}">
            <label>Extra image #${index + 1}</label>
         </div>
         <div class="m-2">
            <img alt="Extra image #${index + 1} preview" id="extraThumbnail${index}" src="${defaultImageThumbnailSrc}" class="imag-fluid" style="width: 100px"/>
         </div>
         <div>
           <input type="file"  name="extraImage" 
              onchange="showExtraImageThumnail(this, ${index})"
             accept="image/jpeg, image/jfif, image/png"/>
         </div>
      </div>
	`;
	
	htmlLinkRemove = `
	               <a class="btn fas fa-times-circle fa-2x icon-dark float-right"
	                href="javascript:removeExtraImage(${index - 1})"
	                title="remove this extra image"></a>
	               `;
	
	$("#divProductImages").append(htmlExtraImage);
	
    $("#extraImageHeader" +(index -1)).append(htmlLinkRemove);
    
    extraImageCount++;
}

function removeExtraImage(index){
	$("#divExtraImage" + index).remove();
}
