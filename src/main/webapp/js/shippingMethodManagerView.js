/* =============================================
   SHIPPING METHOD MANAGER - JAVASCRIPT
   Modern E-Commerce Admin UI
   ============================================= */

(function() {
    'use strict';

    // Get context path
    function getContextPath() {
        var meta = document.querySelector('meta[name="sm-context-path"]');
        if (meta) return meta.getAttribute('content');
        meta = document.querySelector('meta[name="contextPath"]');
        if (meta) return meta.getAttribute('content');
        return '';
    }

    // === TAB SWITCHING ===
    function initTabs() {
        var buttons = document.querySelectorAll('.sm-tab-btn');
        var contents = document.querySelectorAll('.sm-tab-content');

        buttons.forEach(function(btn) {
            btn.addEventListener('click', function() {
                var tabId = this.getAttribute('data-tab');

                buttons.forEach(function(b) { b.classList.remove('active'); });
                contents.forEach(function(c) { c.classList.remove('active'); });

                this.classList.add('active');

                var target = document.getElementById('tab-' + tabId);
                if (target) target.classList.add('active');
            });
        });
    }

    // === MODAL ===
    function showModal(id) {
        var modal = document.getElementById(id);
        if (modal) {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        }
    }

    function hideModal(id) {
        var modal = document.getElementById(id);
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
        }
    }

    function initModals() {
        document.querySelectorAll('.sm-modal-close, .sm-btn-cancel').forEach(function(btn) {
            btn.addEventListener('click', function() {
                var modal = this.closest('.sm-modal-overlay');
                if (modal) modal.classList.remove('show');
                document.body.style.overflow = '';
            });
        });

        document.querySelectorAll('.sm-modal-overlay').forEach(function(overlay) {
            overlay.addEventListener('click', function(e) {
                if (e.target === overlay) {
                    overlay.classList.remove('show');
                    document.body.style.overflow = '';
                }
            });
        });
    }

    // === METHOD ACTIONS ===
    window.smEditMethod = function(id) {
        var row = document.querySelector('tr[data-method-id="' + id + '"]');
        if (!row) return;

        var nameVal = row.dataset.name || '';
        document.getElementById('sm-edit-id').value = id;
        document.getElementById('sm-edit-name').value = nameVal.charAt(0).toUpperCase() + nameVal.slice(1);
        document.getElementById('sm-edit-days').value = row.dataset.days || 3;
        document.getElementById('sm-edit-price').value = row.dataset.price || 20000;
        document.getElementById('sm-edit-provider').value = row.dataset.provider || 'GHN';
        document.getElementById('sm-edit-ghn-id').value = row.dataset.ghnServiceId || 2;
        document.getElementById('sm-edit-express').value = row.dataset.express === 'true' ? 'true' : 'false';
        document.getElementById('sm-edit-surcharge').value = row.dataset.surcharge || 1.5;
        document.getElementById('sm-edit-threshold').value = row.dataset.threshold || 0;

        showModal('modal-edit-method');
    };

    window.smDeleteMethod = function(id, name) {
        document.getElementById('sm-del-method-id').value = id;
        document.getElementById('sm-del-method-name').textContent = name || '';
        showModal('modal-delete-method');
    };

    // === ZONE ACTIONS ===
    window.smEditZone = function(id) {
        var row = document.querySelector('tr[data-zone-id="' + id + '"]');
        if (!row) return;

        document.getElementById('sm-edit-zone-id').value = id;
        document.getElementById('sm-edit-zone-name').value = (row.dataset.name || '').charAt(0).toUpperCase() + row.dataset.name.slice(1);
        document.getElementById('sm-edit-zone-type').value = row.dataset.zonetype || 'PROVINCIAL';
        document.getElementById('sm-edit-zone-base-fee').value = row.dataset.basefee || 20000;
        document.getElementById('sm-edit-zone-price-kg').value = row.dataset.pricekg || 5000;
        document.getElementById('sm-edit-zone-desc').value = row.dataset.desc || '';
        document.getElementById('sm-edit-zone-status').value = row.dataset.zonestatus || '1';

        showModal('modal-edit-zone');
    };

    window.smDeleteZone = function(id, name) {
        document.getElementById('sm-del-zone-id').value = id;
        document.getElementById('sm-del-zone-name').textContent = name || '';
        showModal('modal-delete-zone');
    };

    // === WEIGHT FEE ACTIONS ===
    window.smEditWeightFee = function(id) {
        var row = document.querySelector('tr[data-fee-id="' + id + '"]');
        if (!row) return;

        document.getElementById('sm-edit-fee-id').value = id;
        document.getElementById('sm-edit-fee-method').value = row.dataset.feemethod || 1;
        document.getElementById('sm-edit-fee-zone').value = row.dataset.feezone || 'PROVINCIAL';
        document.getElementById('sm-edit-fee-min-w').value = row.dataset.feeminw || 0;
        document.getElementById('sm-edit-fee-max-w').value = row.dataset.feemaxw || 5;
        document.getElementById('sm-edit-fee-base').value = row.dataset.feebase || 15000;
        document.getElementById('sm-edit-fee-perkg').value = row.dataset.feeperkg || 3000;

        showModal('modal-edit-weight-fee');
    };

    window.smDeleteWeightFee = function(id) {
        document.getElementById('sm-del-fee-id').value = id;
        showModal('modal-delete-weight-fee');
    };

    // === PROVINCE ACTIONS ===
    window.smEditProvince = function(id) {
        var row = document.querySelector('tr[data-province-id="' + id + '"]');
        if (!row) return;

        document.getElementById('sm-edit-province-id').value = id;
        document.getElementById('sm-edit-province-code').value = row.dataset.provcode || '';
        document.getElementById('sm-edit-province-name').value = (row.dataset.provname || '').charAt(0).toUpperCase() + row.dataset.provname.slice(1);
        document.getElementById('sm-edit-province-type').value = row.dataset.provtype || 'tinh';
        document.getElementById('sm-edit-province-region').value = row.dataset.provregion || 'MienNam';
        document.getElementById('sm-edit-province-metro').value = row.dataset.provmetro === 'true' ? 'true' : 'false';

        showModal('modal-edit-province');
    };

    // === STATISTICS EXPORT ===
    window.smExportStats = function(period) {
        var p = period || document.getElementById('stats-filter-period').value || '7days';
        var ctx = getContextPath();
        window.location.href = ctx + '/admin/shippingMethod?statsExport=true&period=' + p;
    };

    // === LOAD STATISTICS BY PERIOD (AJAX) ===
    window.smLoadStatsByPeriod = function(period) {
        var ctx = getContextPath();
        var url = ctx + '/admin/shippingMethod?stats=true&period=' + (period || '7days');

        var xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        xhr.setRequestHeader('Accept', 'application/json');

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    try {
                        var data = JSON.parse(xhr.responseText);
                        if (data.error) {
                            console.error('Stats error:', data.error);
                            return;
                        }
                        updateStatisticsUI(data);
                    } catch (e) {
                        console.error('Failed to parse stats JSON:', e);
                    }
                } else {
                    console.error('Failed to load stats. Status:', xhr.status);
                }
            }
        };
        xhr.send();
    };

    function updateStatisticsUI(data) {
        // Update stat cards
        setStatValue('stat-total-orders', data.totalShippingOrders);
        setStatValue('stat-shipping-revenue', formatNumber(data.shippingRevenue));
        setStatValue('stat-delivering', data.deliveringOrders);
        setStatValue('stat-completed', data.completedOrders);

        // Update logistics cards
        setStatValue('stat-top-province', data.topProvince || '—');
        setStatValue('stat-top-province-name', data.topProvinceName || '—');

        // Update badge count
        var badge = document.querySelector('.sm-tab-btn[data-tab="statistics"] .sm-tab-badge');
        if (badge) badge.textContent = data.totalStatistics || 0;

        // Update top methods table
        updateTopMethodsTable(data.topMethods);

        // Update activity timeline
        updateActivityTimeline(data.recentActivities);
    }

    function setStatValue(id, value) {
        var el = document.getElementById(id);
        if (el) {
            if (typeof value === 'number') {
                el.textContent = value.toLocaleString('vi-VN');
            } else {
                el.textContent = value;
            }
        }
    }

    function formatNumber(n) {
        if (n == null) return '0';
        return parseFloat(n).toLocaleString('vi-VN');
    }

    function updateTopMethodsTable(topMethods) {
        var tbody = document.getElementById('stats-top-methods-body');
        if (!tbody) return;

        if (!topMethods || topMethods.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:32px;color:#9CA3AF;">Chưa có dữ liệu</td></tr>';
            return;
        }

        var html = '';
        var rankClass = ['gold', 'silver', 'bronze', '', ''];
        var rankLabels = ['1', '2', '3', '4', '5'];

        topMethods.forEach(function(tsm, i) {
            var providerClass = (tsm.providerType || 'internal').toLowerCase();
            html += '<tr>';
            html += '<td><span class="sm-stat-rank ' + rankClass[i] + '">' + rankLabels[i] + '</span></td>';
            html += '<td><div class="sm-stat-method-name"><div class="sm-stat-method-icon sm-provider-' + providerClass + '"><i class="fas fa-truck"></i></div>' + escapeHtml(tsm.methodName || '') + '</div></td>';
            html += '<td><span class="sm-badge sm-badge-' + providerClass + '"><i class="fas fa-building"></i> ' + escapeHtml(tsm.providerType || '') + '</span></td>';
            html += '<td><strong>' + (tsm.totalOrders || 0) + '</strong></td>';
            html += '<td><span class="sm-price">' + formatNumber(tsm.totalRevenue) + 'đ</span></td>';
            html += '<td><div class="sm-stat-usage-bar"><div class="sm-stat-usage-bar-track"><div class="sm-stat-usage-bar-fill" style="width:' + (tsm.usagePercent || 0) + '%"></div></div><span class="sm-stat-usage-percent">' + (tsm.usagePercent || 0).toFixed(1) + '%</span></div></td>';
            html += '<td><label class="sm-stat-toggle"><input type="checkbox" ' + (tsm.status == 1 ? 'checked' : '') + '><span class="sm-stat-toggle-slider"></span></label></td>';
            html += '</tr>';
        });

        tbody.innerHTML = html;
    }

    function updateActivityTimeline(activities) {
        var container = document.getElementById('stats-activity-timeline');
        if (!container) return;

        if (!activities || activities.length === 0) {
            container.innerHTML = '<div style="text-align:center;padding:32px 20px;"><div style="width:56px;height:56px;background:#F4F5F7;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:24px;color:#D1D5DB;margin:0 auto 12px;"><i class="fas fa-clock-rotate-left"></i></div><p style="font-size:14px;font-weight:600;color:#374151;margin:0 0 4px;">Chưa có hoạt động gần đây</p></div>';
            return;
        }

        var html = '';
        activities.forEach(function(act) {
            html += '<div class="sm-activity-item">';
            html += '<div class="sm-activity-dot sm-activity-dot-' + (act.dotColor || 'info') + '">';
            html += '<i class="fas ' + (act.icon || 'fa-sync') + '"></i></div>';
            html += '<div class="sm-activity-content">';
            html += '<p class="sm-activity-title">' + escapeHtml(act.title || '') + '</p>';
            html += '<p class="sm-activity-desc">' + escapeHtml(act.description || '') + '</p>';
            html += '<span class="sm-activity-time">' + escapeHtml(act.timestamp || '—') + '</span>';
            html += '</div></div>';
        });

        container.innerHTML = html;
    }

    function escapeHtml(str) {
        if (!str) return '';
        var div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    // === TOGGLE STATUS ===
    window.smToggleStatus = function(checkbox) {
        var id = checkbox.getAttribute('data-id');
        var name = checkbox.getAttribute('data-name') || '';
        var newStatus = checkbox.checked ? 1 : 0;
        var msg = newStatus === 1 ? 'kích hoạt' : 'vô hiệu hóa';

        if (!confirm('Bạn có chắc muốn ' + msg + ' phương thức "' + name + '"?')) {
            checkbox.checked = !checkbox.checked;
            return;
        }

        var form = document.createElement('form');
        form.method = 'POST';
        form.action = getContextPath() + '/admin/shippingMethod';
        form.style.display = 'none';

        addField(form, 'action', 'toggleStatus');
        addField(form, 'id', id);
        addField(form, 'status', newStatus);

        document.body.appendChild(form);
        form.submit();
    };

    function addField(form, name, value) {
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;
        input.value = value;
        form.appendChild(input);
    }

    // === SEARCH FILTERS ===
    window.smFilterTable = function(inputId, tableId) {
        var input = document.getElementById(inputId);
        var table = document.getElementById(tableId);
        if (!input || !table) return;

        var filter = input.value.toLowerCase().trim();
        var rows = table.querySelectorAll('tbody tr');

        rows.forEach(function(row) {
            var text = row.textContent.toLowerCase();
            row.style.display = text.indexOf(filter) > -1 ? '' : 'none';
        });
    };

    // === INIT ===
    function init() {
        initTabs();
        initModals();

        var searchInputs = document.querySelectorAll('[data-search]');
        searchInputs.forEach(function(input) {
            var tableId = input.getAttribute('data-search');
            input.addEventListener('input', function() {
                smFilterTable(input.id, tableId);
            });
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();
