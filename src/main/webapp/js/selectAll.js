function initSelectAllCheckbox(checkboxId) {
    var selectAllCheckbox = document.getElementById(checkboxId);
    if (!selectAllCheckbox) return;

    var table = selectAllCheckbox.closest('table');
    var checkboxes = table.querySelectorAll('tbody input[type="checkbox"]');

    // Update select all state when individual checkboxes change
    function updateSelectAllState() {
        var allEnabledChecked = true;
        var hasEnabled = false;

        for (var i = 0; i < checkboxes.length; i++) {
            if (!checkboxes[i].disabled) {
                hasEnabled = true;
                if (!checkboxes[i].checked) {
                    allEnabledChecked = false;
                    break;
                }
            }
        }

        selectAllCheckbox.checked = hasEnabled && allEnabledChecked;
    }

    // Attach change listener to individual checkboxes
    for (var i = 0; i < checkboxes.length; i++) {
        if (!checkboxes[i].disabled) {
            checkboxes[i].addEventListener('change', updateSelectAllState);
        }
    }

    // Attach change listener to select all checkbox
    selectAllCheckbox.onchange = function() {
        for (var i = 0; i < checkboxes.length; i++) {
            if (!checkboxes[i].disabled) {
                checkboxes[i].checked = this.checked;
            }
        }
    };

    // Initial state check
    updateSelectAllState();
}

function initAllSelectAllCheckboxes() {
    initSelectAllCheckbox('selectAllCheckbox');
    initSelectAllCheckbox('selectAllAddCheckbox');
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAllSelectAllCheckboxes);
} else {
    initAllSelectAllCheckboxes();
}
