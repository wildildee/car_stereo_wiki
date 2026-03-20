$(() => {
    $(".gallery-thumbnail").on("click", (e) => {
        const src = $(e.target).attr("src");
        $(".gallery-big-image").attr("src", src);
        $(".gallery-thumbnail").removeClass("is-focused");
        $(e.target).addClass("is-focused");
    });
})