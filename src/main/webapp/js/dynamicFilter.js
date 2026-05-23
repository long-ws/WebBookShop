(function () {
    // Constructor thay thế cho Class
    var TableFilter = function (table) {
        this.table = table;
        this.columns = [];
        this.columnMap = {};
        this.data = [];
        this.mode = 'OR';

        this.init();
    };

    TableFilter.prototype.init = function () {
        this.detectColumns();

        if (this.columns.length === 0) {
            return;
        }

        this.injectFilterRow();
        this.cacheData();
        this.bindEvents();
        this.bindFilterControls();
    };

    // ===== Detect columns =====
    TableFilter.prototype.detectColumns = function () {
        var self = this;
        var headers = this.table.querySelectorAll('thead th');

        headers.forEach(function (th, index) {
            if (th.classList.contains('no-filter')) {
                return;
            }

            var field = th.dataset.name || 'col_' + index;

            self.columnMap[field] = self.columns.length;

            self.columns.push({
                index: index,
                field: field
            });
        });
    };

    // ===== Create filter row =====
    TableFilter.prototype.injectFilterRow = function () {
        var thead = this.table.querySelector('thead');

        if (!thead) {
            return;
        }

        var row = this.table.querySelector('.tf-filter-row');

        if (!row) {
            row = document.createElement('tr');
            row.className = 'tf-filter-row';
            thead.appendChild(row);
        }

        row.innerHTML = '';

        var headers = this.table.querySelectorAll('thead th');

        headers.forEach(function (th, index) {
            var td = document.createElement('td');

            if (th.classList.contains('no-filter')) {
                row.appendChild(td);
                return;
            }

            var field = th.dataset.name || 'col_' + index;

            var input = document.createElement('input');
            input.type = 'text';
            input.className = 'form-control form-control-sm tf-input';
            input.placeholder = 'Lọc...';
            input.dataset.field = field;

            td.appendChild(input);
            row.appendChild(td);
        });
    };

    // ===== Cache data =====
    TableFilter.prototype.cacheData = function () {
        var self = this;
        var rows = this.table.querySelectorAll('tbody tr');

        this.data = [];

        rows.forEach(function (row) {
            var cells = row.querySelectorAll('td');

            var values = self.columns.map(function (col) {
                var cell = cells[col.index];
                var text = cell ? cell.textContent : '';

                return text.toLowerCase().trim();
            });

            self.data.push({
                el: row,
                values: values
            });
        });
    };

    // ===== Bind Filter Controls =====
    TableFilter.prototype.bindFilterControls = function () {
        var self = this;

        var toggle = document.getElementById('filterToggle');
        var label = document.querySelector('label[for="filterToggle"]');

        if (toggle) {
            toggle.addEventListener('change', function (e) {
                self.mode = e.target.checked ? 'AND' : 'OR';

                if (label) {
                    label.textContent = self.mode;
                }

                self.apply();
            });
        }
    };

    // ===== Events =====
    TableFilter.prototype.bindEvents = function () {
        var self = this;
        var inputs = this.table.querySelectorAll('.tf-input');
        var debounce;

        inputs.forEach(function (input) {
            input.addEventListener('keyup', function () {
                clearTimeout(debounce);

                debounce = setTimeout(function () {
                    self.apply();
                }, 250);
            });

            input.addEventListener('change', function () {
                self.apply();
            });
        });
    };

    // ===== Apply filter =====
    TableFilter.prototype.apply = function () {
        var self = this;
        var inputs = this.table.querySelectorAll('.tf-input');
        var filters = [];

        inputs.forEach(function (input) {
            var val = input.value.toLowerCase().trim();

            if (val !== '') {
                filters.push({
                    field: input.dataset.field,
                    value: val
                });
            }
        });

        this.data.forEach(function (row) {
            var visible = true;

            if (filters.length > 0) {
                if (self.mode === 'AND') {
                    visible = true;

                    for (var i = 0; i < filters.length; i++) {
                        if (!self.match(row, filters[i])) {
                            visible = false;
                            break;
                        }
                    }
                } else {
                    visible = false;

                    for (var j = 0; j < filters.length; j++) {
                        if (self.match(row, filters[j])) {
                            visible = true;
                            break;
                        }
                    }
                }
            }

            row.el.style.display = visible ? '' : 'none';
        });
    };

    // ===== Match =====
    TableFilter.prototype.match = function (row, filter) {
        var idx = this.columnMap[filter.field];

        if (idx === undefined) {
            return false;
        }

        return row.values[idx].indexOf(filter.value) !== -1;
    };

    // ===== AUTO INIT =====
    window.addEventListener('load', function () {
        var tables = document.querySelectorAll('.filter-table');

        tables.forEach(function (table) {
            new TableFilter(table);
        });
    });
})();