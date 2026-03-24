$(() => {
    // Query for a user
    $.get("/api/user", (data) => {
        if (data && data.name) {
            $("#username").text(data.name);
            $(".loggedout").addClass("is-hidden");
            $(".loggedin").removeClass("is-hidden");
            if (data.admin) {
                $(".admin-only").removeClass("is-hidden");
            }
        }
    });

    // Add logout button
    $("#logout").on("click", (e) => {
        e.preventDefault();
        const csrfToken = $("meta[name='_csrf']").attr("content");
        const csrfHeader = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/logout",
            type: "POST",
            beforeSend: (xhr) => {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: () => {
                window.location.href = "/";
            }
        });
    });

    // Bulma Navbar Burger Toggle
    $(".navbar-burger").on("click", function() {
        const target = $(this).data("target");
        $(this).toggleClass("is-active");
        $("#" + target).toggleClass("is-active");
    });
});