let currentType = '';
let timeout = null;

function prepareModal(type) {
    currentType = type;
    const searchInput = document.getElementById('searchInput');
    const modalTitle = document.getElementById('selectionModalLabel');
    const resultList = document.getElementById('searchResultList');

    searchInput.value = '';
    resultList.innerHTML = '<li class="text-muted text-center py-3">Nhập từ khóa tìm kiếm...</li>';

    modalTitle.innerText = type === 'category' ? 'Tìm kiếm danh mục' : 'Tìm kiếm sản phẩm';
    searchInput.placeholder = type === 'category' ? 'Nhập tên danh mục...' : 'Nhập tên sản phẩm...';
}

function addToList(type, id, name, img) {
    const listId = type === 'category' ? 'selected-categories' : 'selected-products';
    const inputName = type === 'category' ? 'categoryIds' : 'productIds';
    const listContainer = document.getElementById(listId);

    if (listContainer.querySelector(`input[value="${id}"]`)) return;

    const emptyMsg = listContainer.querySelector('.text-muted');
    if (emptyMsg) emptyMsg.remove();

    const safeImg = (img && img.trim() !== "" && img !== "null") ? img : 'default.png';
    const imgSrc = `${contextPath}/images/${safeImg}`;

    const li = document.createElement('li');
    li.className = 'list-group-item d-flex align-items-center justify-content-between py-2 rounded mb-1 shadow-sm bg-white';

    li.innerHTML = `
        <div class="d-flex align-items-center">
            <img src="${imgSrc}" 
                 onerror="this.onerror=null; this.src='${contextPath}/images/default.png';"
                 class="rounded me-2" style="width:30px; height:30px; object-fit:cover;">
            <span class="small fw-bold">${name}</span>
            <input type="hidden" name="${inputName}" value="${id}">
        </div>
        <button type="button" class="btn btn-sm btn-outline-danger border-0" onclick="this.closest('li').remove()">
             Xóa <i class="bi bi-trash3 ms-1"></i>
        </button>
    `;
    listContainer.appendChild(li);
}

document.getElementById('searchInput').addEventListener('input', function() {
    clearTimeout(timeout);
    const keyword = this.value.trim();
    const resultList = document.getElementById('searchResultList');

    if (keyword.length === 0) {
        resultList.innerHTML = '<li class="text-muted text-center py-3">Nhập từ khóa để tìm kiếm...</li>';
        return;
    }

    timeout = setTimeout(() => {
        resultList.innerHTML = `<div class="text-center py-3"><div class="spinner-border spinner-border-sm text-secondary"></div></div>`;

        fetch(`${contextPath}/admin/voucherManager/create?type=${currentType}&query=${encodeURIComponent(keyword)}`)
            .then(res => res.json())
            .then(data => {
                resultList.innerHTML = '';
                if (data.length === 0) {
                    resultList.innerHTML = '<li class="text-center py-3 text-danger">Không tìm thấy kết quả</li>';
                    return;
                }
                data.forEach(item => {
                    const btn = document.createElement('button');
                    btn.type = "button";
                    btn.className = 'list-group-item list-group-item-action d-flex align-items-center';
                    btn.innerHTML = `
                        <img src="${contextPath}/images/${item.imageName || 'default.png'}" class="rounded me-3" style="width:40px; height:40px; object-fit:cover;">
                        <div class="flex-fill text-start">
                            <div class="fw-bold small">${item.name}</div>
                        </div>
                        <i class="bi bi-plus-circle text-primary"></i>`;
                    btn.onclick = () => addToList(currentType, item.id, item.name, item.imageName);
                    resultList.appendChild(btn);
                });
            })
            .catch(err => {
                resultList.innerHTML = '<li class="text-center py-3 text-danger">Lỗi tải dữ liệu</li>';
            });
    }, 500);
});

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('voucherForm');
    const methodSelect = document.getElementById('calculationMethod');
    const valueInput = document.getElementById('voucherValue');
    const startInput = document.getElementById('startDate');
    const endInput = document.getElementById('endDate');

    function validateLogic() {
        const val = parseFloat(valueInput.value);
        if (methodSelect.value === '0') {
            if (val > 100) {
                valueInput.setCustomValidity('Tối đa 100%');
            } else if (val <= 0) {
                valueInput.setCustomValidity('Phải > 0');
            } else {
                valueInput.setCustomValidity('');
            }
        } else {
            if (val <= 0) {
                valueInput.setCustomValidity('Phải > 0');
            } else {
                valueInput.setCustomValidity('');
            }
        }

        const start = new Date(startInput.value);
        const end = new Date(endInput.value);
        if (startInput.value && endInput.value && end <= start) {
            endInput.setCustomValidity('Ngày kết thúc phải sau ngày bắt đầu');
        } else {
            endInput.setCustomValidity('');
        }
    }

    form.addEventListener('submit', function (event) {
        validateLogic();
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add('was-validated');
    }, false);

    [methodSelect, valueInput, startInput, endInput].forEach(el => {
        el.addEventListener('change', validateLogic);
    });
});