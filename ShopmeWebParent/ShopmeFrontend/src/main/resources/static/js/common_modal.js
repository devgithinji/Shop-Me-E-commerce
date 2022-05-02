function showModal(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}

function showErrorModal(message) {
    showModal("Error", message)
}

function showWarningModal(message) {
    showModal("Warning", message)
}