let buttonLoad;
let dropDownCountry;
let buttonAddCountry;
let buttonUpdateCountry;
let buttonDeleteCountry;
let labelCountryName;
let fieldCountryName;
let fieldCountryCode;


$(document).ready(function () {
    buttonLoad = $("#buttonLoad");
    dropDownCountry = $("#dropDownCountries")
    buttonAddCountry = $("#buttonAddCountry")
    buttonUpdateCountry = $("#buttonUpdateCountry")
    buttonDeleteCountry = $("#buttonDeleteCountry")
    labelCountryName = $("#labelCountryName")
    fieldCountryName = $("#fieldCountryName")
    fieldCountryCode = $("#fieldCountryCode")


    buttonLoad.click(function () {
        loadCountries();
    })

    dropDownCountry.on("change", function () {
        changeFormStateToSelectedCountry();
    })

    buttonAddCountry.click(function () {
        if (buttonAddCountry.val() === "Add") {
            addCountry();
        } else {
            changeFormStateToNewCountry();
        }

    })

    buttonUpdateCountry.click(function () {
        updateCountry();
    })

    buttonDeleteCountry.click(function () {
        deleteCountry();
    })
})

function deleteCountry() {
    let optionValue = dropDownCountry.val();
    let countryId = optionValue.split("-")[0];

    let url = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: 'DELETE', url: url, beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
    }).done(function (countryId) {
        $("#dropDownCountries option[value='" + optionValue + "']").remove();
        changeFormStateToNewCountry();
        showToast("Country has been deleted successfully");
    }).fail(function () {
        showToast("Error: something went wrong");
    });
}

function updateCountry() {
    let url = contextPath + "countries/save";
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let countryId = dropDownCountry.val().split("-")[0]

    let jsonData = {id: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST', url: url, beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        $("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
        $("#dropDownCountries option:selected").text(countryName);
        showToast("Country has been updated");

        changeFormStateToNewCountry();
    }).fail(function () {
        showToast("Error: something went wrong");
    });
}

function addCountry() {
    let url = contextPath + "countries/save";
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();

    let jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST', url: url, beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        selectNewAddedCountry(countryId, countryCode, countryName)
        showToast("A new country has been added");
    }).fail(function () {
        showToast("Error: something went wrong");
    });
}

function selectNewAddedCountry(countryId, countryCode, countryName) {
    let optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);

    $("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);
    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function changeFormStateToNewCountry() {
    buttonAddCountry.val("Add");
    labelCountryName.text("Country Name")
    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);

    fieldCountryName.val("").focus();
    fieldCountryCode.val("")
}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected Country");
    let selectedCountryName = $("#dropDownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    let countryCode = dropDownCountry.val().split("-")[1]
    fieldCountryCode.val(countryCode);

}


function loadCountries() {
    let url = contextPath + "countries/list";
    $.get(url, function (responseJSON) {
        dropDownCountry.empty();
        $.each(responseJSON, function (index, country) {
            let optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
        })
    }).done(function () {
        buttonLoad.val("Refresh Country list");
        showToast("All countries has been loaded");
    }).fail(function () {
        showToast("Error: something went wrong");
    });
}

function showToast(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}