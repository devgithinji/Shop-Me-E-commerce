$(document).ready(function () {
    $("#buttonCancel").on('click', function () {
        console.log("clicked")
        window.location = moduleURL;
    })

    $("#fileImage").change(function () {
        let fileSize = this.files[0].size;
        if (fileSize > (1024 * 1024)) {
            this.setCustomValidity("you must choose an image less than 1MB!");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbNail(this);
        }
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
