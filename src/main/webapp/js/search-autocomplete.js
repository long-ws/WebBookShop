(function() {
    var searchInput = document.getElementById('headerSearchInput');
    var dropdown = document.getElementById('searchDropdown');
    var searchForm = document.getElementById('headerSearchForm');
    var contextPath = typeof CONTEXT_PATH !== 'undefined' ? CONTEXT_PATH : '';
    var debounceTimer;
    var currentXhr = null;

    function formatPrice(price) {
        return new Intl.NumberFormat('vi-VN').format(Math.round(price)) + 'đ';
    }

    function getImageSrc(product) {
        return product.imageName
            ? contextPath + '/image/' + product.imageName
            : contextPath + '/img/280px.png';
    }

    function showDropdown() {
        dropdown.classList.add('show');
    }

    function hideDropdown() {
        dropdown.classList.remove('show');
    }

    function renderSuggestions(suggestions) {
        if (!suggestions || suggestions.length === 0) {
            dropdown.innerHTML = '<div class="search-autocomplete-empty">Không có gợi ý phù hợp</div>';
            showDropdown();
            return;
        }

        var html = '';
        for (var i = 0; i < suggestions.length; i++) {
            var p = suggestions[i];
            var imgSrc = getImageSrc(p);
            var finalPrice = p.discount > 0
                ? p.price * (100 - p.discount) / 100
                : p.price;
            var priceHtml;
            if (p.discount > 0) {
                priceHtml = formatPrice(finalPrice)
                    + ' <small class="text-muted text-decoration-line-through" style="font-size:0.7em">' + formatPrice(p.price) + '</small>'
                    + ' <span class="badge bg-danger" style="font-size:0.65em">-' + Math.round(p.discount) + '%</span>';
            } else {
                priceHtml = formatPrice(p.price);
            }

            html += '<div class="search-autocomplete-item" data-id="' + p.id + '">'
                + '<img src="' + imgSrc + '" alt="" onerror="this.onerror=null; this.src=\'' + contextPath + '/img/280px.png\';">'
                + '<div class="search-autocomplete-info">'
                + '<div class="search-autocomplete-name">' + (p.highlightedName || p.name) + '</div>'
                + '<div class="search-autocomplete-author"><i class="bi bi-person me-1"></i>' + (p.highlightedAuthor || p.author || '') + '</div>'
                + '</div>'
                + '<div class="search-autocomplete-price">' + priceHtml + '</div>'
                + '</div>';
        }

        html += '<div class="search-autocomplete-footer">'
            + '<i class="bi bi-search me-1"></i>Nhấn Enter để tìm kiếm hoặc nhấn vào gợi ý'
            + '</div>';

        dropdown.innerHTML = html;
        showDropdown();

        var items = dropdown.querySelectorAll('.search-autocomplete-item');
        for (var j = 0; j < items.length; j++) {
            items[j].addEventListener('click', (function(idx) {
                return function() {
                    window.location.href = contextPath + '/product?id=' + suggestions[idx].id;
                };
            })(j));
        }
    }

    function fetchSuggestions(query) {
        if (currentXhr) {
            currentXhr.abort();
            currentXhr = null;
        }

        if (!query || query.trim().length < 1) {
            hideDropdown();
            return;
        }

        try {
            var url = contextPath + '/searchSuggestion?q=' + encodeURIComponent(query.trim()) + '&limit=8';
            currentXhr = new XMLHttpRequest();
            currentXhr.open('GET', url, true);
            currentXhr.onreadystatechange = function() {
                if (currentXhr.readyState === 4) {
                    if (currentXhr.status === 200) {
                        try {
                            var suggestions = JSON.parse(currentXhr.responseText);
                            renderSuggestions(suggestions);
                        } catch (e) {
                            hideDropdown();
                        }
                    } else {
                        hideDropdown();
                    }
                }
            };
            currentXhr.send(null);
        } catch (e) {
            hideDropdown();
        }
    }

    if (searchInput && dropdown && searchForm) {
        searchInput.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            var query = this.value.trim();
            if (query.length === 0) {
                hideDropdown();
                return;
            }
            debounceTimer = setTimeout(function() {
                fetchSuggestions(query);
            }, 200);
        });

        searchInput.addEventListener('focus', function() {
            var query = this.value.trim();
            if (query.length >= 1 && dropdown.children.length > 0) {
                showDropdown();
            }
        });

        searchInput.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                hideDropdown();
            }
        });

        document.addEventListener('click', function(e) {
            if (!e.target.closest('.search-autocomplete-wrapper')) {
                hideDropdown();
            }
        });

        searchForm.addEventListener('submit', function() {
            hideDropdown();
        });
    }
})();
