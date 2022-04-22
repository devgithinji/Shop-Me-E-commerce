let extraImagesCount = 0;
let dropDownBrands = $("#brand");
let dropDownCategories = $("#category");

$(document).ready(function () {
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropDownBrands.change(function () {
        dropDownCategories.empty();
        getCategories();
    })
    getCategories();

    $("input[name = 'extraImage']").each(function (index) {
        extraImagesCount++;
        $(this).change(function () {
            showExtraImageThumbNail(this, index);
        })
    })
})

function showExtraImageThumbNail(fileInput, index) {
    let file = fileInput.files[0];
    let reader = new FileReader();

    reader.onload = function (e) {
        $("#extraThumbnail" + index).attr("src", e.target.result);
    }

    reader.readAsDataURL(file);

    if (index >= extraImagesCount - 1) {
        addNextExtraImageSection(index + 1);
    }
}

function addNextExtraImageSection(index) {
    let htmlExtraImage = `
         <div class="col border m-3 p-2" id="divExtraImage${index}">
            <div id="extraImageHeader${index}">
                <label>Extra Image #${index + 1}:</label>
            </div>
            <div class="m-2">
                <img id="extraThumbnail${index}" src="${defaultImageThumbnailSrc}" alt="Extra image #${index + 1} preview" class="img-fluid">
            </div>
            <div>
                <input type="file"  name="extraImage" onchange="showExtraImageThumbNail(this,${index})" accept="image/png, image/jpeg">
            </div>
        </div>          
    `;

    let htmlLinkRemove = `
        <a class='btn fas fa-times-circle fa-2x icon-dark float-right' href="javascript: removeExtraImage(${index - 1})" title="remove this image"></a>
    `;


    $("#divProductImages").append(htmlExtraImage);


    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);

    extraImagesCount++;
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
    //extraImagesCount--;
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