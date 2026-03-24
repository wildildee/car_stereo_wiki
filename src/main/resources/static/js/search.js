function setSort(field) {
    const sortByInput = document.getElementById('sortBy');
    const sortDirInput = document.getElementById('sortDir');
    const currentSort = sortByInput.value;
    const currentDir = sortDirInput.value;

    if (currentSort === field) {
        sortDirInput.value = currentDir === 'asc' ? 'desc' : 'asc';
    } else {
        sortByInput.value = field;
        sortDirInput.value = 'asc';
    }
    document.getElementById('searchForm').submit();
}

// Close dropdown when clicking outside
window.addEventListener('click', function(e) {
    const dropdown = document.getElementById('sortDropdown');
    if (dropdown && !dropdown.contains(e.target)) {
        dropdown.classList.remove('is-active');
    }
});

function resetSelect(button) {
    button.closest('.field').querySelector('select').selectedIndex = -1;
}
