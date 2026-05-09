<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Kết quả tìm kiếm</title>
<style>
.search-hero {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 2rem 1.5rem;
    border-radius: 12px;
    margin-bottom: 2rem;
}
.search-bar-large {
    border-radius: 50px;
    padding: 0.75rem 1.5rem;
    font-size: 1.1rem;
    border: 2px solid rgba(255,255,255,0.3);
    background: rgba(255,255,255,0.15);
    color: white;
}
.search-bar-large::placeholder {
    color: rgba(255,255,255,0.7);
}
.search-bar-large:focus {
    background: rgba(255,255,255,0.25);
    border-color: rgba(255,255,255,0.5);
    color: white;
    box-shadow: none;
    outline: none;
}
.btn-search-large {
    border-radius: 50px;
    padding: 0.75rem 2rem;
    font-size: 1rem;
}
.search-keywords {
    background-color: rgba(255,255,255,0.2);
    padding: 0.3rem 0.8rem;
    border-radius: 20px;
    display: inline-block;
    margin: 0.2rem;
    font-size: 0.8rem;
}
.loading-spinner {
    display: none;
    text-align: center;
    padding: 3rem;
}
.spinner-border {
    width: 3rem;
    height: 3rem;
}
.no-results {
    text-align: center;
    padding: 4rem 1rem;
}
.no-results i {
    font-size: 5rem;
    color: #dee2e6;
    margin-bottom: 1.5rem;
}
.search-result-card {
    transition: transform 0.2s, box-shadow 0.2s;
    border: none;
    border-radius: 12px;
    overflow: hidden;
    height: 100%;
}
.search-result-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.1);
}
.search-result-card .card-body {
    padding: 1.25rem;
}
.result-price {
    font-size: 1.15rem;
    font-weight: 700;
    color: #ee4d2d;
}
.result-original-price {
    font-size: 0.85rem;
    color: #999;
    text-decoration: line-through;
}
.result-discount {
    font-size: 0.75rem;
    background: #ee4d2d;
    color: white;
    padding: 2px 6px;
    border-radius: 4px;
}
.stats-bar {
    display: flex;
    align-items: center;
    gap: 1rem;
    flex-wrap: wrap;
}
</style>
</head>

<body>
	<jsp:include page="_header.jsp" />

	<section class="section-content">
		<div class="container">

			<div class="search-hero">
				<div class="row justify-content-center">
					<div class="col-lg-10">
						<h4 class="mb-3">
							<i class="bi bi-search me-2"></i>Tìm kiếm sản phẩm
						</h4>
						<form id="searchForm" class="row g-2 align-items-end">
							<div class="col">
								<input type="text" id="searchInput" class="form-control search-bar-large"
									placeholder="Nhập từ khóa tìm kiếm (VD: Harry Potter, Tolkien)..."
									value="<c:out value='${requestScope.query}'/>" autocomplete="off">
							</div>
							<div class="col-auto">
								<button type="submit" class="btn btn-warning btn-search-large">
									<i class="bi bi-search me-1"></i>Tìm kiếm
								</button>
							</div>
						</form>
						<div class="mt-3">
							<small class="opacity-75 d-block mb-2">Tìm kiếm trong các trường:</small>
							<span class="search-keywords">Tên sản phẩm</span>
							<span class="search-keywords">Tác giả</span>
							<span class="search-keywords">Nhà xuất bản</span>
							<span class="search-keywords">Mô tả</span>
						</div>
						<div class="mt-2">
							<small class="opacity-75">Hỗ trợ tìm kiếm nhiều từ khóa cùng lúc (VD: "Harry Potter Rowling")</small>
						</div>
					</div>
				</div>
			</div>

			<div id="statsBar" class="stats-bar mb-3" style="display: none;">
				<div>
					<span class="fw-bold" id="resultCount">0</span> sản phẩm
				</div>
				<div class="vr"></div>
				<div>
					Từ khóa: <strong id="displayQuery"></strong>
				</div>
				<div class="vr"></div>
				<div class="ms-auto">
					<a href="${pageContext.request.contextPath}/advancedSearch" class="btn btn-outline-primary btn-sm">
						<i class="bi bi-sliders me-1"></i>Tìm kiếm nâng cao
					</a>
				</div>
			</div>

			<div id="loadingSpinner" class="loading-spinner">
				<div class="spinner-border text-primary" role="status">
					<span class="visually-hidden">Loading...</span>
				</div>
				<p class="mt-3 text-muted">Đang tìm kiếm...</p>
			</div>

			<div id="resultsContainer"></div>

			<div id="paginationContainer" class="mt-4"></div>

		</div>
	</section>

	<jsp:include page="_footer.jsp" />

	<script>
	const PRODUCTS_PER_PAGE = 12;
	const contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
	let currentPage = 1;
	let currentQuery = '';
	let totalPages = 1;

	function formatPrice(price) {
		return new Intl.NumberFormat('vi-VN').format(Math.round(price)) + '₫';
	}

	function getImageSrc(product) {
		return product.imageName
			? contextPath + '/image/' + product.imageName
			: contextPath + '/img/280px.png';
	}

	function getProductHtml(product) {
		const finalPrice = product.discount > 0
			? product.price * (100 - product.discount) / 100
			: product.price;
		const priceHtml = product.discount > 0
			? '<span class="result-price">' + formatPrice(finalPrice) + '</span>'
				+ ' <span class="result-original-price">' + formatPrice(product.price) + '</span>'
				+ ' <span class="result-discount">-' + Math.round(product.discount) + '%</span>'
			: '<span class="result-price">' + formatPrice(product.price) + '</span>';
		const author = product.author || 'Không rõ';
		const highlightedName = product.highlightedName || product.name;
		const highlightedAuthor = product.highlightedAuthor || author;
		const imgSrc = getImageSrc(product);

		return '<div class="col-xl-3 col-lg-4 col-md-6">'
			+ '<div class="card search-result-card">'
			+ '<div class="card-body">'
			+ '<div class="text-center mb-3">'
			+ '<a href="' + contextPath + '/product?id=' + product.id + '">'
			+ '<img src="' + imgSrc + '" alt="' + product.name + '" class="img-fluid"'
			+ ' style="width: 140px; height: 140px; object-fit: cover; border-radius: 8px;"'
			+ ' onerror="this.onerror=null; this.src=\'' + contextPath + '/img/280px.png\';">'
			+ '</a>'
			+ '</div>'
			+ '<h6 class="mb-2" style="min-height: 2.8rem; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;">'
			+ '<a href="' + contextPath + '/product?id=' + product.id + '" class="text-decoration-none text-dark">' + highlightedName + '</a>'
			+ '</h6>'
			+ '<p class="text-muted small mb-2"><i class="bi bi-person me-1"></i>' + highlightedAuthor + '</p>'
			+ '<div class="mb-2">' + priceHtml + '</div>'
			+ '<a href="' + contextPath + '/product?id=' + product.id + '" class="btn btn-outline-primary btn-sm w-100">'
			+ '<i class="bi bi-eye me-1"></i>Chi tiết'
			+ '</a>'
			+ '</div>'
			+ '</div>'
			+ '</div>';
	}

	function renderResults(data) {
		const container = document.getElementById('resultsContainer');
		const statsBar = document.getElementById('statsBar');

		if (data.totalProducts === 0) {
			statsBar.style.display = 'flex';
			document.getElementById('resultCount').textContent = '0';
			document.getElementById('displayQuery').textContent = '"' + data.query + '"';

			container.innerHTML = '<div class="no-results">'
				+ '<i class="bi bi-search"></i>'
				+ '<h4>Không tìm thấy sản phẩm nào</h4>'
				+ '<p class="text-muted">Không có sản phẩm nào khớp với từ khóa tìm kiếm của bạn.</p>'
				+ '<div class="alert alert-light text-start d-inline-block" style="max-width: 500px;">'
				+ '<h6><i class="bi bi-lightbulb me-2"></i>Gợi ý tìm kiếm:</h6>'
				+ '<ul class="mb-0 text-start">'
				+ '<li>Thử tìm kiếm với từ khóa khác</li>'
				+ '<li>Sử dụng từ khóa ngắn gọn hơn</li>'
				+ '<li>Tìm theo tên tác giả hoặc nhà xuất bản</li>'
				+ '<li>Tách các từ khóa bằng dấu cách để tìm đồng thời nhiều điều kiện</li>'
				+ '</ul>'
				+ '</div>'
				+ '<div class="mt-4">'
				+ '<a href="${pageContext.request.contextPath}/" class="btn btn-primary">'
				+ '<i class="bi bi-house me-2"></i>Về trang chủ'
				+ '</a>'
				+ '</div>'
				+ '</div>';
			document.getElementById('paginationContainer').innerHTML = '';
			return;
		}

		statsBar.style.display = 'flex';
		document.getElementById('resultCount').textContent = data.totalProducts;
		document.getElementById('displayQuery').textContent = '"' + data.query + '"';

		totalPages = data.totalPages;
		currentPage = data.currentPage;

		let html = '<div class="row">';
		for (let i = 0; i < data.products.length; i++) {
			html += getProductHtml(data.products[i]);
		}
		html += '</div>';
		container.innerHTML = html;

		renderPagination();
	}

	function renderPagination() {
		const container = document.getElementById('paginationContainer');
		if (totalPages <= 1) {
			container.innerHTML = '';
			return;
		}

		let html = '<nav><ul class="pagination justify-content-center">';

		html += '<li class="page-item ' + (currentPage === 1 ? 'disabled' : '') + '">'
			+ '<a class="page-link" href="#" data-page="' + (currentPage - 1) + '">Trang trước</a></li>';

		const maxPages = 5;
		let startPage = Math.max(1, currentPage - Math.floor(maxPages / 2));
		let endPage = Math.min(totalPages, startPage + maxPages - 1);
		if (endPage - startPage < maxPages - 1) {
			startPage = Math.max(1, endPage - maxPages + 1);
		}

		if (startPage > 1) {
			html += '<li class="page-item"><a class="page-link" href="#" data-page="1">1</a></li>';
			if (startPage > 2) html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
		}

		for (let i = startPage; i <= endPage; i++) {
			html += '<li class="page-item ' + (i === currentPage ? 'active' : '') + '">'
				+ '<a class="page-link" href="#" data-page="' + i + '">' + i + '</a></li>';
		}

		if (endPage < totalPages) {
			if (endPage < totalPages - 1) html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
			html += '<li class="page-item"><a class="page-link" href="#" data-page="' + totalPages + '">' + totalPages + '</a></li>';
		}

		html += '<li class="page-item ' + (currentPage === totalPages ? 'disabled' : '') + '">'
			+ '<a class="page-link" href="#" data-page="' + (currentPage + 1) + '">Trang sau</a></li>';

		html += '</ul></nav>';
		container.innerHTML = html;

		container.querySelectorAll('.page-link[data-page]').forEach(function(link) {
			link.addEventListener('click', function(e) {
				e.preventDefault();
				const page = parseInt(this.dataset.page);
				if (page >= 1 && page <= totalPages) {
					performSearch(currentQuery, page);
				}
			});
		});
	}

	async function performSearch(query, page) {
		if (!query || query.trim() === '') {
			document.getElementById('statsBar').style.display = 'none';
			document.getElementById('resultsContainer').innerHTML = '';
			document.getElementById('paginationContainer').innerHTML = '';
			return;
		}

		currentQuery = query;
		currentPage = page || 1;

		document.getElementById('loadingSpinner').style.display = 'block';
		document.getElementById('resultsContainer').innerHTML = '';
		document.getElementById('paginationContainer').innerHTML = '';

		try {
			const url = contextPath + '/search?q=' + encodeURIComponent(query)
				+ '&page=' + currentPage + '&ajax=true';
			const response = await fetch(url);
			const data = await response.json();

			document.getElementById('loadingSpinner').style.display = 'none';
			renderResults(data);

			if (currentPage === 1 && data.totalProducts > 0) {
				const newUrl = contextPath + '/search?q=' + encodeURIComponent(query);
				window.history.replaceState({query: query}, '', newUrl);
			}
		} catch (error) {
			console.error('Search error:', error);
			document.getElementById('loadingSpinner').style.display = 'none';
			document.getElementById('resultsContainer').innerHTML = ''
				+ '<div class="alert alert-danger text-center">'
				+ '<i class="bi bi-exclamation-triangle me-2"></i>'
				+ 'Đã xảy ra lỗi khi tìm kiếm. Vui lòng thử lại.'
				+ '</div>';
		}
	}

	document.addEventListener('DOMContentLoaded', function() {
		const searchForm = document.getElementById('searchForm');
		const searchInput = document.getElementById('searchInput');

		if (searchForm) {
			searchForm.addEventListener('submit', function(e) {
				e.preventDefault();
				const query = searchInput.value.trim();
				if (query) {
					performSearch(query, 1);
				}
			});
		}

		if (searchInput) {
			let debounceTimer;
			searchInput.addEventListener('input', function() {
				clearTimeout(debounceTimer);
				debounceTimer = setTimeout(function() {
					const q = searchInput.value.trim();
					if (q.length >= 2) {
						performSearch(q, 1);
					}
				}, 400);
			});
		}

		const initialQuery = searchInput ? searchInput.value.trim() : '';
		if (initialQuery) {
			performSearch(initialQuery, 1);
		}
	});
	</script>
</body>

</html>
