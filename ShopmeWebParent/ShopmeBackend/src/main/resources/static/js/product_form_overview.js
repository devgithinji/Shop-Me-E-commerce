let dropDownBrands = $("#brand");
let dropDownCategories = $("#category");

$(document).ready(function () {
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropDownBrands.change(function () {
        dropDownCategories.empty();
        getCategories();
    })
    getCategoriesForNewForm();
})

function getCategoriesForNewForm() {
    let catIdField = $("#categoryId");
    let editMode = false;
    if (catIdField.length) {
        editMode = true;
    }

    if (!editMode) getCategories();
}


function getCategories() {
    let brandId = dropDownBrands.val();
    let url = brandsModuleURL + "/" + brandId + "/categories";

    $.get(url, function (responseJson) {
        $.each(responseJson, function (index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropDownCategories);
        })
    })
}


function checkUnique(form) {
    let productId = $("#id").val();
    let productName = $("#name").val();
    let csrfValue = $("input[name='_csrf']").val();

    let params = {id: productId, name: productName, _csrf: csrfValue}

    $.post(checkUniqueUrl, params, function (response) {
        if (response === "OK") {
            form.submit();
        } else if (response === "Duplicate") {
            showWarningModal("There is another product having the name " + productName);
        } else {
            showErrorModal("Unknown response from server")
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server")
    })

    return false;
}