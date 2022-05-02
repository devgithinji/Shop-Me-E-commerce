let decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';
let thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.';

$(document).ready(function () {
    $(".linkMinus").on("click", function (e) {
        e.preventDefault();
        decreaseQuantity($(this));
    })

    $(".linkPlus").on("click", function (e) {
        e.preventDefault();
        increaseQuantity($(this))
    })


    $(".linkRemove").on("click", function (evt) {
        evt.preventDefault();
        removeProduct($(this));
    });
})

function decreaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) - 1;

    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("minimum quantity is 1");
    }
}

function increaseQuantity(link) {
    let productId = link.attr("pid");
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) + 1;

    if (newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal("maximum quantity is 5");
    }
}

function updateQuantity(productId, quantity) {
    let url = contextPath + "cart/update/" + productId + "/" + quantity;
    $.ajax({
        type: "POST", url: url, beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue)
        }
    }).done(function (updatedSubTotal) {
        updateSubTotal(updatedSubTotal, productId)
        updateTotal();
    }).fail(function () {
        showErrorModal("Error while updating product quantity");
    })
}

function updateSubTotal(updatedSubTotal, productId) {
    let formattedSubTotal = $.number(updatedSubTotal, 2);
    $("#subTotal" + productId).text(formattedSubTotal);
}

function updateTotal() {
    let total = 0.0;
    let productCount = 0;
    $(".subtotal").each(function (index, element) {
        productCount++;
        total = total + parseFloat(clearCurrencyFormat(element.innerHTML));
    })

    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        let formattedTotal = $.number(total, 2);
        $("#total").text(formattedTotal);
    }
}

function showEmptyShoppingCart() {
    $("#sectionTotal").hide();
    $("#sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
    let url = link.attr("href");

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        let rowNumber = link.attr("rowNumber");
        removeProductHTML(rowNumber);
        updateTotal();
        updateCountNumbers();

        showModal("Shopping Cart", response);

    }).fail(function () {
        showErrorModal("Error while removing product.");
    });
}


function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
    $(".divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}


function clearCurrencyFormat(numberString) {
    let result = numberString.replaceAll(thousandsSeparator, "");
    return result.replaceAll(decimalSeparator, ".");
}