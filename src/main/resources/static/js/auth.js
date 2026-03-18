$(() => {
    // Query for a user
    $.get("/api/user", (data) => {
        if (data && data.name) {
            $("#username").text(data.name);
            $(".loggedout").hide();
            $(".loggedin").show();
        } else {
            $(".loggedout").show();
            $(".loggedin").hide();
        }
    });

    // Add logout button
    $("#logout").on("click", (e) => {
        e.preventDefault();
        const csrfToken = document.cookie
            .split("; ")
            .find(row => row.startsWith("XSRF-TOKEN="))
            ?.split("=")[1];

        $.ajax({
            url: "/logout",
            type: "POST",
            headers: {
                "X-XSRF-TOKEN": csrfToken
            },
            success: () => {
                $("#username").text("");
                $(".loggedout").show();
                $(".loggedin").hide();
                window.location.href = "/";
            }
        });
    });
});