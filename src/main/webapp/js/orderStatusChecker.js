const OrderStatusChecker = (function() {
    let pollingInterval = null;
    let currentOrderId = null;
    let currentStatus = null;
    let isPollingActive = false;
    const POLLING_INTERVAL = 3000;
    const MIN_POLLING_INTERVAL = 2000;

    function init(orderId, initialStatus, intervalMs) {
        if (!orderId) return;
        currentOrderId = orderId;
        currentStatus = initialStatus;
        const interval = (intervalMs && intervalMs >= MIN_POLLING_INTERVAL) ? intervalMs : POLLING_INTERVAL;
        startPolling(interval);
        setupVisibilityChange();
    }

    function startPolling(intervalMs) {
        if (isPollingActive) return;
        isPollingActive = true;
        fetchStatus();
        pollingInterval = setInterval(fetchStatus, intervalMs);
    }

    function stopPolling() {
        if (pollingInterval) {
            clearInterval(pollingInterval);
            pollingInterval = null;
        }
        isPollingActive = false;
    }

    function setupVisibilityChange() {
        document.addEventListener('visibilitychange', function() {
            if (document.hidden) {
                stopPolling();
            } else {
                if (currentOrderId) {
                    fetchStatus();
                    if (!pollingInterval) {
                        pollingInterval = setInterval(fetchStatus, POLLING_INTERVAL);
                        isPollingActive = true;
                    }
                }
            }
        });
    }

    function getContextPath() {
        const pathParts = window.location.pathname.split('/');
        let contextPath = '';
        for (let i = 0; i < pathParts.length; i++) {
            if (pathParts[i] === 'orderDetail' || pathParts[i] === 'order') {
                break;
            }
            if (pathParts[i]) {
                contextPath = '/' + pathParts[i];
            }
        }
        return contextPath;
    }

    function fetchStatus() {
        if (!currentOrderId) return;

        var xhr = new XMLHttpRequest();
        xhr.open('GET', window.location.origin + getContextPath() + '/api/order-status?id=' + currentOrderId + '&t=' + new Date().getTime(), true);
        xhr.setRequestHeader('Cache-Control', 'no-cache');

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                try {
                    var data = JSON.parse(xhr.responseText);
                    if (!data.error && data.status !== currentStatus) {
                        handleStatusChange(data);
                    }
                    if (data.lastUpdated) {
                        updateLastChecked(data.lastUpdated);
                    }
                } catch (e) {}
            }
        };

        xhr.send();
    }

    function handleStatusChange(data) {
        const oldStatus = currentStatus;
        currentStatus = data.status;

        showStatusUpdateToast(data);

        updateStatusBadge(data);

        updateProgressBar(data.status);

        updateOrderListStatus(data.id, data.status, data.statusText);

        dispatchCustomEvent('orderStatusChanged', {
            orderId: data.id,
            oldStatus: oldStatus,
            newStatus: data.status,
            statusText: data.statusText
        });
    }

    function showStatusUpdateToast(data) {
        const toastId = 'status-update-toast-' + data.id;
        let toast = document.getElementById(toastId);

        if (toast) {
            toast.remove();
        }

        toast = document.createElement('div');
        toast.id = toastId;
        toast.className = 'toast show status-update-toast' + (data.status === 7 ? ' order-cancelled' : ' status-changed');
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'polite');

        const iconClass = data.status === 7 ? 'bi-x-circle-fill' : 'bi-check-circle-fill';
        const bgClass = data.status === 7 ? 'bg-danger' : 'bg-success';
        const message = data.status === 7
            ? 'Đơn hàng của bạn đã bị hủy'
            : 'Trạng thái đơn hàng đã được cập nhật';

        toast.innerHTML =
            '<div class="toast-body d-flex align-items-center gap-3 ' + bgClass + ' text-white rounded">' +
                '<div class="toast-icon bg-white rounded-circle">' +
                    '<i class="bi ' + iconClass + ' text-' + (data.status === 3 ? 'danger' : 'success') + '"></i>' +
                '</div>' +
                '<div class="flex-grow-1">' +
                    '<div class="fw-bold">' + message + '</div>' +
                    '<div class="small opacity-75">' + data.statusText + '</div>' +
                '</div>' +
                '<button type="button" class="btn-close btn-close-white" onclick="this.closest(\'.toast\').remove()"></button>' +
            '</div>';

        document.body.appendChild(toast);

        setTimeout(function() {
            toast.classList.add('toast-hiding');
            setTimeout(function() {
                if (toast.parentNode) {
                    toast.remove();
                }
            }, 400);
        }, 5000);
    }

    function updateStatusBadge(data) {
        var badge = document.querySelector('.status-badge-realtime');
        if (badge) {
            badge.className = 'badge ' + getStatusBadgeClass(data.status);
            badge.innerHTML = '<i class="bi bi-' + data.statusIcon + '"></i> ' + data.statusText;
        }

        var pageHeaderBadge = document.querySelector('.section-pagetop .badge');
        if (pageHeaderBadge) {
            pageHeaderBadge.className = 'badge ' + getBootstrapBadgeClass(data.status) + ' fs-6 px-3 py-2';
            pageHeaderBadge.innerHTML = '<i class="bi bi-' + data.statusIcon + ' me-1"></i> ' + data.statusText;
        }
    }

    function updateProgressBar(status) {
        var steps = document.querySelectorAll('.progress-step');
        if (!steps.length) return;

        if (status === 7) {
            steps.forEach(function(step) {
                step.classList.remove('completed', 'active', 'pending');
                step.classList.add('cancelled');
            });
            var progressFill = document.querySelector('.progress-fill');
            if (progressFill) {
                progressFill.style.width = '0%';
            }
            return;
        }

        var currentStep = 1;
        if (status >= 2 && status <= 6) {
            currentStep = status;
        }

        var totalSteps = 6;
        steps.forEach(function(step, index) {
            step.classList.remove('completed', 'active', 'pending', 'cancelled');
            var stepNumber = index + 1;

            if (stepNumber < currentStep) {
                step.classList.add('completed');
            } else if (stepNumber === currentStep) {
                step.classList.add('active');
            } else {
                step.classList.add('pending');
            }
        });

        var progressFill = document.querySelector('.progress-fill');
        if (progressFill) {
            var percentage = ((currentStep - 1) / (totalSteps - 1)) * 90;
            progressFill.style.width = percentage + '%';
        }
    }

    function updateOrderListStatus(orderId, status, statusText) {
        var orderRow = document.querySelector('tr[data-order-id="' + orderId + '"]');
        if (orderRow) {
            var statusCell = orderRow.querySelector('.order-status-cell');
            if (statusCell) {
                statusCell.innerHTML = '<span class="badge ' + getBootstrapBadgeClass(status) + ' px-2 py-1">' +
                    '<i class="bi bi-' + getStatusIcon(status) + ' me-1"></i> ' + statusText +
                    '</span>';
            }
        }
    }

    function updateLastChecked(time) {
        var indicator = document.querySelector('.realtime-indicator .last-checked');
        if (indicator) {
            indicator.textContent = 'Cập nhật lúc ' + time;
        }
    }

    function getStatusBadgeClass(status) {
        switch (status) {
            case 1: return 'bg-warning text-dark status-badge-realtime status-pending';
            case 2: return 'bg-primary status-badge-realtime status-confirmed';
            case 3: return 'bg-primary status-badge-realtime status-picked-up';
            case 4: return 'bg-primary status-badge-realtime status-shipping';
            case 5: return 'bg-info status-badge-realtime status-delivering';
            case 6: return 'bg-success status-badge-realtime status-delivered';
            case 7: return 'bg-danger status-badge-realtime status-cancelled';
            default: return 'bg-secondary status-badge-realtime';
        }
    }

    function getBootstrapBadgeClass(status) {
        switch (status) {
            case 1: return 'bg-warning text-dark';
            case 2: return 'bg-primary';
            case 3: return 'bg-primary';
            case 4: return 'bg-primary';
            case 5: return 'bg-info';
            case 6: return 'bg-success';
            case 7: return 'bg-danger';
            default: return 'bg-secondary';
        }
    }

    function getStatusIcon(status) {
        switch (status) {
            case 1: return 'bag-check';
            case 2: return 'check2-all';
            case 3: return 'box-seam';
            case 4: return 'truck';
            case 5: return 'geo-alt';
            case 6: return 'check-circle';
            case 7: return 'x-circle';
            default: return 'question-circle';
        }
    }

    function dispatchCustomEvent(eventName, detail) {
        var event = new CustomEvent(eventName, { detail: detail });
        document.dispatchEvent(event);
    }

    function destroy() {
        stopPolling();
        currentOrderId = null;
        currentStatus = null;
    }

    return {
        init: init,
        destroy: destroy,
        stopPolling: stopPolling,
        startPolling: startPolling,
        fetchStatus: fetchStatus
    };
})();

function initializeOrderStatusChecker() {
    var orderIdElement = document.getElementById('currentOrderId');
    var initialStatusElement = document.getElementById('initialOrderStatus');

    if (orderIdElement && initialStatusElement) {
        var orderId = parseInt(orderIdElement.value, 10);
        var initialStatus = parseInt(initialStatusElement.value, 10);

        if (!isNaN(orderId) && !isNaN(initialStatus)) {
            OrderStatusChecker.init(orderId, initialStatus);
        }
    }
}

document.addEventListener('DOMContentLoaded', function() {
    if (window.location.pathname.indexOf('orderDetail') !== -1) {
        initializeOrderStatusChecker();
    }
});
