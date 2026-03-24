$(() => {
    $('#addTagForm').on('submit', (e) => {
        e.preventDefault();
        
        const csrfToken = $("meta[name='_csrf']").attr("content");
        const csrfHeader = $("meta[name='_csrf_header']").attr("content");

        const data = {
            name: $('#name').val(),
            type: $('#type').val(),
            color: $('#color').val() || null,
            description: $('#description').val()
        };

        $.ajax({
            url: '/api/tag/add',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            beforeSend: (xhr) => {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: () => {
                window.location.href = '/';
            },
            error: (xhr) => {
                alert('Error adding tag: ' + xhr.responseText);
            }
        });
    });
});
