$(document).ready(function () {
    $("a[name='linkRemoveDetail']").each(function (index) {
        $(this).click(function () {
            removeDetailsSectionByIndex(index);
        })
    })
})


function addNextDetailSection() {

    let allDivDetails = $("[id^='divDetail']");
    let allDivDetailsCount = allDivDetails.length;


    let htmlDetailSection = `
    <div class="form-inline" id="divDetail${allDivDetailsCount}">
        <input type="hidden" name="detailIDs" value="0">
        <label class="m-3">Name:</label>
        <input type="text" class="form-control w-25" name="detailNames" maxlength="255">
        <label class="m-3">Value:</label>
        <input type="text" class="form-control w-25" name="detailValues" maxlength="255">
    </div>
`;

    $("#divProductDetails").append(htmlDetailSection);

    let previousDivDetailSection = allDivDetails.last();
    let previousDivDetailID = previousDivDetailSection.attr("id");

    let htmlLinkRemove = `
        <a class='btn fas fa-times-circle fa-2x icon-dark' 
        href="javascript: removeDetailsSectionById('${previousDivDetailID}')"
         title="remove this detail"></a>
    `;


    previousDivDetailSection.append(htmlLinkRemove);

    $("input[name='detailNames']").last().focus();

}

function removeDetailsSectionById(id) {
    $("#" + id).remove();
}

function removeDetailsSectionByIndex(index) {
    $("#divDetail" + index).remove();
}