
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