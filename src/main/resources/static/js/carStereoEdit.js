$(() => {
    const galleryContainer = $('#gallery-inputs');
    const addBtn = $('#add-image');

    addBtn.on('click', () => {
        const newInput = $(`
            <div class="field has-addons">
                <div class="control is-expanded">
                    <input class="input" type="text" name="galleryImageUrls" value="" placeholder="https://example.com/image.jpg">
                </div>
                <div class="control">
                    <button type="button" class="button is-danger remove-image">Remove</button>
                </div>
            </div>
        `);
        galleryContainer.append(newInput);
    });

    galleryContainer.on('click', '.remove-image', (e) => {
        $(e.target).closest('.field').remove();
        if (galleryContainer.children().length === 0) {
            addBtn.trigger('click');
        }
    });
});
