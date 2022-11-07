$(document).ready(function(){
   $(".linkVoteReview").on("click",function(e){
	   e.preventDefault();
	   voteReview($(this));
   });
});


function voteReview(currentLink){
	requestUrl = currentLink.attr("href");
	
	
	$.ajax({
		type: "POST",
		url: requestUrl,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(voteResult){
		console.log(voteResult);
		
		if(voteResult.success){
			$("#modalDialog").on("hide.bs.modal", function(e){
			 updateVoteCountAndIcons(currentLink, voteResult);
		    });
		}
		
		
		showModalDialog("Vote Review", voteResult.message);
	}).fail(function(){
		showErorModal("Error voting Review");
	});
}

function updateVoteCountAndIcons(currentLink, voteResult){
	reviewId = currentLink.attr("reviewId");
	voteUpLink = $("#linkVoteUp-" + reviewId);
	voteDownLink = $("#linkVoteDown-" + reviewId);
	
	$("#voteCount-" + reviewId).text(voteResult.voteCount + " vote(s)");
	
	message = voteResult.message;
	
	if(message.includes("successfully voted up")){
		highlightVoteUpIcon(currentLink, voteDownLink);
	}else if(message.includes("successfully voted down")){
		highlightVoteDownIcon(currentLink, voteUpLink);
	}else if(message.includes("unvoted down")){
		unhighlightVoteDownIcon(voteDownLink);
	}else if(message.includes("unvoted up")){
		unhighlightVoteUpIcon(voteUpLink);
	}
}

function highlightVoteUpIcon(voteUpLink, voteDownLink){
	voteUpLink.removeClass("far").addClass("fas");
	voteUpLink.attr("title", "undo vote up this review");
	
	voteDownLink.removeClass("fas").addClass("far");
}

function highlightVoteDownIcon(voteDownLink, voteUpLink){
	voteDownLink.removeClass("far").addClass("fas");
	voteDownLink.attr("title", "undo vote down this review");
	
	voteUpLink.removeClass("fas").addClass("far");
}

function unhighlightVoteDownIcon(voteDownLink){
	voteDownLink.attr("title", "voted down this review");
	voteDownLink.removeClass("fas").addClass("far");
}

function unhighlightVoteUpIcon(voteUpLink){
	voteUpLink.attr("title", "voted up this review");
	voteUpLink.removeClass("fas").addClass("far");
}


