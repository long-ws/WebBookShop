/**
 * Shipping Method Manager - JavaScript
 * Handles all functionality for shipping method management page
 */

(function() {
    'use strict';

    // Tab functionality
    function initTabs() {
        document.querySelectorAll('.tab-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                var tab = this.getAttribute('data-tab');
                if (!tab) return;
                
                document.querySelectorAll('.tab-btn').forEach(function(b) { 
                    b.classList.remove('active'); 
                });
                this.classList.add('active');
                
                document.querySelectorAll('.tab-content').forEach(function(c) { 
                    c.classList.remove('active'); 
                });
                
                var content = document.getElementById('content-' + tab);
                if (content) {
                    content.classList.add('active');
                }
            });
        });
    }

    // Edit Method
    window.editMethod = function(id) {
        var row = document.querySelector('tr[data-id="' + id + '"]');
        if (!row) return;
        
        document.getElementById('editMethodId').value = id;
        document.getElementById('editMethodName').value = row.dataset.name ? row.dataset.name.charAt(0).toUpperCase() + row.dataset.name.slice(1) : '';
        document.getElementById('editEstimatedDays').value = row.dataset.estimatedDays || 3;
        document.getElementById('editPricePerKg').value = row.dataset.pricePerKg || 20000;
        document.getElementById('editProviderType').value = row.dataset.providerType || 'GHN';
        document.getElementById('editGhnServiceId').value = row.dataset.ghnServiceId || 2;
        document.getElementById('editIsExpress').value = row.dataset.express === 'true' ? 'true' : 'false';
        document.getElementById('editExpressSurcharge').value = row.dataset.expressSurcharge || 1.5;
        document.getElementById('editFreeShippingThreshold').value = row.dataset.freeThreshold || 0;
        
        if (typeof bootstrap !== 'undefined') {
            var modal = new bootstrap.Modal(document.getElementById('editMethodModal'));
            modal.show();
        }
    };

    // Delete Method
    window.deleteMethod = function(id, name) {
        document.getElementById('deleteMethodId').value = id;
        document.getElementById('deleteMethodNameDisplay').textContent = name;
        
        if (typeof bootstrap !== 'undefined') {
            var modal = new bootstrap.Modal(document.getElementById('deleteMethodModal'));
            modal.show();
        }
    };

    // Edit Zone
    window.editZone = function(id) {
        var row = document.querySelector('tr[data-id="' + id + '"]');
        if (!row) return;
        
        document.getElementById('editZoneId').value = id;
        document.getElementById('editZoneName').value = row.dataset.name ? row.dataset.name.replace(/\b\w/g, function(l) { return l.toUpperCase(); }) : '';
        document.getElementById('editZoneType').value = row.dataset.zoneType || 'PROVINCIAL';
        document.getElementById('editZoneBaseFee').value = row.dataset.baseFee || 20000;
        document.getElementById('editZonePricePerKg').value = row.dataset.pricePerKg || 5000;
        
        if (typeof bootstrap !== 'undefined') {
            var modal = new bootstrap.Modal(document.getElementById('editZoneModal'));
            modal.show();
        }
    };

    // Delete Zone
    window.deleteZone = function(id, name) {
        document.getElementById('deleteZoneId').value = id;
        document.getElementById('deleteZoneNameDisplay').textContent = name;
        
        if (typeof bootstrap !== 'undefined') {
            var modal = new bootstrap.Modal(document.getElementById('deleteZoneModal'));
            modal.show();
        }
    };

    // Edit Weight Fee
    window.editWeightFee = function(id) {
        var row = document.querySelector('tr[data-id="' + id + '"]');
        if (!row) return;
        
        document.getElementById('editWeightFeeId').value = id;
        document.getElementById('editWeightFeeMinWeight').value = row.dataset.minWeight || 0;
        document.getElementById('editWeightFeeMaxWeight').value = row.dataset.maxWeight || 5;
        document.getElementById('editWeightFeeBaseFee').value = row.dataset.baseFee || 15000;
        document.getElementById('editWeightFeeFeePerKg').value = row.dataset.feePerKg || 3000;
        
        if (typeof bootstrap !== 'undefined') {
            var modal = new bootstrap.Modal(document.getElementById('editWeightFeeModal'));
            modal.show();
        }
    };

    // Delete Weight Fee
    window.deleteWeightFee = function(id) {
        document.getElementById('deleteWeightFeeId').value = id;
        
        if (typeof bootstrap !== 'undefined') {
            var modal = new bootstrap.Modal(document.getElementById('deleteWeightFeeModal'));
            modal.show();
        }
    };

    // Edit Province
    window.editProvince = function(id) {
        var row = document.querySelector('tr[data-id="' + id + '"]');
        if (!row) return;
        
        document.getElementById('editProvinceId').value = id;
        document.getElementById('editProvinceCode').value = row.dataset.code || '';
        document.getElementById('editProvinceName').value = row.dataset.name ? row.dataset.name.replace(/\b\w/g, function(l) { return l.toUpperCase(); }) : '';
        
        if (typeof bootstrap !== 'undefined') {
            var modal = new bootstrap.Modal(document.getElementById('editProvinceModal'));
            modal.show();
        }
    };

    // Toggle Status
    window.handleToggle = function(checkbox) {
        var methodId = checkbox.dataset.methodId;
        var methodName = checkbox.dataset.methodName;
        var newStatus = checkbox.checked ? 1 : 0;
        
        if (confirm('Ban co muon ' + (newStatus ? 'bat' : 'tat') + ' phuong thuc "' + methodName + '"?')) {
            var form = document.createElement('form');
            form.method = 'POST';
            form.action = window.contextPath + '/admin/shippingMethod';
            
            var actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'toggleStatus';
            form.appendChild(actionInput);
            
            var idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = methodId;
            form.appendChild(idInput);
            
            var statusInput = document.createElement('input');
            statusInput.type = 'hidden';
            statusInput.name = 'status';
            statusInput.value = newStatus;
            form.appendChild(statusInput);
            
            document.body.appendChild(form);
            form.submit();
        } else {
            checkbox.checked = !checkbox.checked;
        }
    };

    // Filter Methods
    window.filterMethods = function() {
        var searchInput = document.getElementById('searchMethods');
        if (!searchInput) return;
        
        var search = searchInput.value.toLowerCase();
        var rows = document.querySelectorAll('#methodsTable tbody tr');
        
        rows.forEach(function(row) {
            var text = row.textContent.toLowerCase();
            row.style.display = text.indexOf(search) !== -1 ? '' : 'none';
        });
    };

    // Initialize on DOM ready
    document.addEventListener('DOMContentLoaded', function() {
        initTabs();
    });

})();
