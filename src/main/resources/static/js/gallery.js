$(() => {
    $(".gallery-thumbnail").on("click", (e) => {
        const src = $(e.target).attr("src");
        $(".gallery-big-image").attr("src", src);
        $(".gallery-thumbnail").removeClass("is-focused");
        $(e.target).addClass("is-focused");
    });

    $(".gallery-big-image").on("click", () => {
        const src = $(".gallery-big-image").attr("src");
        $("#modal-image").attr("src", src);
        $("#image-modal").addClass("is-active");
        $("html").addClass("is-clipped");
    });

    $(".modal-background, .modal-close").on("click", () => {
        $("#image-modal").removeClass("is-active");
        $("html").removeClass("is-clipped");
    });
})