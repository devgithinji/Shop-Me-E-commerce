let dropDownCountry;
let dataListState;
$(document).ready(function () {
    dropDownCountry = $("#country")
    dataListState = $("#listStates")

    dropDownCountry.on("change", function () {
        loadStatesForCountry();
        $("#state").val("").focus()
    })
})

function loadStatesForCountry() {
    let selectedCountry = $("#country option:selected");
    let countryId = selectedCountry.val();
    let url = contextPath + "settings/list_states_by_country/" + countryId;

    $.get(url, function (response) {
        dataListState.empty();
        $.each(response, function (index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dataListState)
        })
    })

}

function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value !== $("#password").val()) {
        confirmPassword.setCustomValidity("Passwords do not match");
    } else {
        confirmPassword.setCustomValidity("");
    }
}

function checkEmailUnique(form) {
    let url = contextPath + "customers/check_unique_email";
    let customerEmail = $("#email").val();
    let csrfValue = $("input[name='_csrf']").val();
    let params = {email: customerEmail, _csrf: csrfValue};

    $.post(url, params, function (response) {
        if (response === "OK") {
            form.submit();
        } else if (response === "Duplicated") {
            showWarningModal("There is another customer having the email " + customerEmail)
        } else {
            showErrorModal("Something went wrong")
        }
    }).fail(function () {
        showErrorModal("Couldn't connect to server")
    });
    return false;
}


