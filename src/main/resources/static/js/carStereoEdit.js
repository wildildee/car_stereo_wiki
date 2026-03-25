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
                    <div class="file is-info">
                        <label class="file-label">
                            <input class="file-input upload-image" type="file" name="imageFile" accept="image/*">
                            <span class="file-cta">
                                <span class="file-icon">
                                    <i class="fas fa-upload"></i>
                                </span>
                                <span class="file-label">Upload</span>
                            </span>
                        </label>
                    </div>
                </div>
                <div class="control">
                    <button type="button" class="button is-danger remove-image">Remove</button>
                </div>
            </div>
        `);
        galleryContainer.append(newInput);
    });

    galleryContainer.on('change', '.upload-image', (e) => {
        const fileInput = e.target;
        if (fileInput.files.length === 0) return;

        const file = fileInput.files[0];
        const formData = new FormData();
        formData.append('file', file);

        const csrfToken = $("meta[name='_csrf']").attr("content");
        const csrfHeader = $("meta[name='_csrf_header']").attr("content");

        const control = $(fileInput).closest('.control');
        const inputField = control.closest('.field').find('input[name="galleryImageUrls"]');
        const uploadBtn = control.find('.file-cta');
        const originalText = uploadBtn.find('.file-label').text();

        uploadBtn.find('.file-label').text('Uploading...');
        fileInput.disabled = true;

        $.ajax({
            url: '/api/upload',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            beforeSend: (xhr) => {
                if (csrfHeader && csrfToken) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                }
            },
            success: (data) => {
                inputField.val(data.url);
                uploadBtn.find('.file-label').text('Success!');
                setTimeout(() => {
                    uploadBtn.find('.file-label').text(originalText);
                    fileInput.disabled = false;
                }, 2000);
            },
            error: (err) => {
                console.error('Upload failed:', err);
                alert('Upload failed: ' + (err.responseJSON ? err.responseJSON.error : 'Unknown error'));
                uploadBtn.find('.file-label').text('Failed');
                setTimeout(() => {
                    uploadBtn.find('.file-label').text(originalText);
                    fileInput.disabled = false;
                }, 2000);
            }
        });
    });

    galleryContainer.on('click', '.remove-image', (e) => {
        $(e.target).closest('.field').remove();
        if (galleryContainer.children().length === 0) {
            addBtn.trigger('click');
        }
    });

    const resourceContainer = $('#resource-inputs');
    const addResourceBtn = $('#add-resource');

    addResourceBtn.on('click', () => {
        const newInput = $(`
            <div class="field has-addons">
                <div class="control">
                    <input class="input" type="text" name="resourceIcon" placeholder="fas fa-file-pdf">
                </div>
                <div class="control is-expanded">
                    <input class="input" type="text" name="resourceName" placeholder=".pdf">
                </div>
                <div class="control is-expanded">
                    <input class="input" type="text" name="resourceLink" placeholder="https://example.com/resource.pdf">
                </div>
                <div class="control">
                    <button type="button" class="button is-danger remove-resource">Remove</button>
                </div>
            </div>
        `);
        resourceContainer.append(newInput);
    });

    resourceContainer.on('click', '.remove-resource', (e) => {
        $(e.target).closest('.field').remove();
        if (resourceContainer.children().length === 0) {
            addResourceBtn.trigger('click');
        }
    });
});
