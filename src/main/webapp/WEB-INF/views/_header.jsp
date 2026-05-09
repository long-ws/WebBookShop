<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<style>
.search-autocomplete-wrapper {
    position: relative;
    width: 100%;
}
.search-autocomplete-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border: 1px solid #dee2e6;
    border-top: none;
    border-radius: 0 0 8px 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    z-index: 1000;
    max-height: 420px;
    overflow-y: auto;
    display: none;
}
.search-autocomplete-dropdown.show {
    display: block;
}
.search-autocomplete-item {
    display: flex;
    align-items: center;
    padding: 10px 12px;
    text-decoration: none;
    color: #212529;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background-color 0.15s;
}
.search-autocomplete-item:last-child {
    border-bottom: none;
}
.search-autocomplete-item:hover {
    background-color: #f8f9fa;
}
.search-autocomplete-item img {
    width: 40px;
    height: 40px;
    object-fit: cover;
    border-radius: 4px;
    margin-right: 12px;
    flex-shrink: 0;
}
.search-autocomplete-info {
    flex: 1;
    min-width: 0;
}
.search-autocomplete-name {
    font-weight: 500;
    font-size: 0.9rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-bottom: 2px;
}
.search-autocomplete-author {
    font-size: 0.75rem;
    color: #6c757d;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.search-autocomplete-price {
    font-size: 0.85rem;
    font-weight: 600;
    color: #ee4d2d;
    white-space: nowrap;
    margin-left: 10px;
}
.search-autocomplete-empty {
    padding: 12px;
    text-align: center;
    color: #6c757d;
    font-size: 0.85rem;
}
.search-autocomplete-footer {
    padding: 8px 12px;
    background: #f8f9fa;
    text-align: center;
    border-top: 1px solid #dee2e6;
    font-size: 0.75rem;
    color: #6c757d;
}
mark.search-highlight {
    background-color: #fff3cd;
    color: #856404;
    padding: 0 2px;
    border-radius: 2px;
    font-weight: 600;
}
</style>

<header class="section-header">
	<section class="header-main border-bottom">
		<div class="container">
			<div class="row align-items-center">
				<div class="col-lg-3 py-3">
					<h3 class="m-0">
						<a class="text-body text-decoration-none"
							href="${pageContext.request.contextPath}/"> <i
							class="bi bi-house"></i> Shop Bán Sách
						</a>
					</h3>
				</div>

				<div class="col-lg-4 col-xl-5 ${empty sessionScope.currentUser ? 'mb-3 mb-lg-0' : ''}">
					<div class="search-autocomplete-wrapper">
						<form id="headerSearchForm" action="${pageContext.request.contextPath}/search"
							method="post">
							<div class="input-group w-100">
								<input type="text" id="headerSearchInput" class="form-control"
									placeholder="Nhập từ khóa cần tìm ..." name="q"
									value="<c:out value='${requestScope.query}'/>" autocomplete="off">
								<button class="btn btn-primary" type="submit">
									<i class="bi bi-search"></i>
								</button>
							</div>
						</form>
						<div id="searchDropdown" class="search-autocomplete-dropdown"></div>
					</div>
					<div class="text-end mt-1">
						<a href="${pageContext.request.contextPath}/advancedSearch"
							class="small text-muted text-decoration-none">Tìm kiếm nâng cao</a>
					</div>
				</div>

				<div class="col-lg-5 col-xl-4">
					<ul
						class="nav col-12 col-lg-auto my-2 my-lg-0 justify-content-center justify-content-lg-end text-small">
						<c:choose>
							<c:when test="${not empty sessionScope.currentUser}">
								<li><a href="${pageContext.request.contextPath}/user"
									class="nav-link text-body"><i
										class="bi bi-person d-block text-center fs-3"></i> Tài khoản</a></li>
								<li><a href="${pageContext.request.contextPath}/order"
									class="nav-link text-body"><i
										class="bi bi-list-check d-block text-center fs-3"></i> Đơn hàng</a></li>
							</c:when>
						</c:choose>
						<li><a href="${pageContext.request.contextPath}/cart"
							class="nav-link text-body position-relative"> <span
								id="total-cart-items-quantity"
								class="position-absolute top-0 end-0 mt-2 me-2 badge rounded-pill ${sessionScope.cartCount > 0 ? 'bg-primary' : 'bg-secondary'}"
								style="font-size: 0.65rem; min-width: 1.1rem; height: 1.1rem; display: flex; align-items: center; justify-content: center; padding: 0 0.25rem;">
									${sessionScope.cartCount > 0 ? sessionScope.cartCount : ''}
							</span>
								<i
								class="bi bi-cart d-block text-center fs-3 position-relative"></i>
								Giỏ hàng
						</a></li>
					</ul>
				</div>

			</div>
		</div>
	</section>
</header>

<nav class="navbar navbar-light border-bottom">
	<div class="container d-flex align-items-center">

		<details style="position: relative;">
			<summary
				style="cursor: pointer; padding: 5px 10px; background-color: #f8f9fa; border: 1px solid #ddd; border-radius: 4px;">
				<i class="bi bi-list"></i> Danh mục sản phẩm
			</summary>

			<ul
				style="position: absolute; top: 100%; left: 0; background-color: #e9ecef; border: 1px solid #ccc; list-style: none; padding: 10px; margin: 0; min-width: 200px; z-index: 100;">
				<c:forEach var="cat" items="${requestScope.categories}">
					<li><a class="dropdown-item"
						href="${pageContext.request.contextPath}/category?id=${cat.id}">${cat.name}</a></li>
				</c:forEach>
			</ul>
		</details>

		<div class="ms-auto">
			<c:choose>
				<c:when test="${not empty sessionScope.currentUser}">
					<span>Xin chào <strong>${sessionScope.currentUser.fullname}</strong>!
					</span>
					<a class="btn btn-light ms-2"
						href="${pageContext.request.contextPath}/signout">Đăng xuất</a>
				</c:when>
				<c:otherwise>
					<a class="btn btn-light me-2"
						href="${pageContext.request.contextPath}/signup">Đăng ký</a>
					<a class="btn btn-primary"
						href="${pageContext.request.contextPath}/signin">Đăng nhập</a>
				</c:otherwise>
			</c:choose>
		</div>

	</div>
</nav>

<script>
(function() {
    var searchInput = document.getElementById('headerSearchInput');
    var dropdown = document.getElementById('searchDropdown');
    var searchForm = document.getElementById('headerSearchForm');
    var contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
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
</script>
