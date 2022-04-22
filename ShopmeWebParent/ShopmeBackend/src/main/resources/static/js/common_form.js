$(document).ready(function () {
    $("#buttonCancel").on('click', function () {
        window.location = moduleURL;
    })

    $("#fileImage").change(function () {
        if(!checkFileSize(this)){
            return;
        }
        showImageThumbNail(this);
    })
});


function showImageThumbNail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    }
    reader.readAsDataURL(file);
}

function checkFileSize(fileInput) {
    let fileSize = fileInput.files[0].size;
    if (fileSize > MAX_FILE_SIZE) {
        fileInput.setCustomValidity("you must choose an image less than " + Math.round((MAX_FILE_SIZE / 1000) / 100) * 100 + " KB");
        fileInput.reportValidity();
        return false;
    } else {
        fileInput.setCustomValidity("");
        return true;
    }
}


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
