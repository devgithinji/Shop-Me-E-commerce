$(document).ready(function () {
    $('#logoutLink').on("click", function (e) {
        e.preventDefault();
        document.logoutForm.submit();
    })

    // customizeDropDownMenu();
    customizeTabs();
})

function customizeDropDownMenu() {
    $(".dropdown > a").click(function () {
        location.href = this.href;
    })
}

function customizeTabs() {
    // Javascript to enable link to tab
    var url = document.location.toString();
    if (url.match('#')) {
        $('.nav-tabs a[href="#' + url.split('#')[1] + '"]').tab('show');
    }

    // Change hash for page-reload
    $('.nav-tabs a').on('shown.bs.tab', function (e) {
        window.location.hash = e.target.hash;
    })
}