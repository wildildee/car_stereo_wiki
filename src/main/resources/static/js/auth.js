$(() => {
    // Query for a user
    $.get("/api/user", (data) => {
        $("#user").text(data.name);
        $(".loggedout").hide();
        $(".loggedin").show();
    });
});