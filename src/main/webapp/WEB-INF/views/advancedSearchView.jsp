<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">

<head>
<jsp:include page="_meta.jsp" />
<title>Tìm kiếm nâng cao</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/advancedSearchView.css">
</head>

<body>
    <jsp:include page="_header.jsp" />

    <section class="section-content mb-5">
        <div class="container advanced-search-container">
            
            <!-- Search Results Header -->
            <div class="search-results-header">
                <h4 class="mb-2">
                    <i class="bi bi-funnel me-2"></i>Tìm kiếm nâng cao
                </h4>
                <p class="results-count mb-0">
                    Tìm thấy <strong>${requestScope.totalProducts}</strong> sản phẩm
                </p>
                
                <c:if test="${not empty requestScope.keyword || not empty requestScope.selectedCategoryId || not empty requestScope.author || not empty requestScope.publisher || not empty requestScope.minPrice || not empty requestScope.maxPrice || not empty requestScope.minYear || not empty requestScope.maxYear}">
                    <div class="active-filters">
                        <c:if test="${not empty requestScope.keyword}">
                            <span class="filter-tag">
                                Từ khóa: "${requestScope.keyword}"
                            </span>
                        </c:if>
                        <c:if test="${not empty requestScope.selectedCategoryId}">
                            <span class="filter-tag">
                                Danh mục: ${requestScope.selectedCategoryName}
                            </span>
                        </c:if>
                        <c:if test="${not empty requestScope.author}">
                            <span class="filter-tag">
                                Tác giả: "${requestScope.author}"
                            </span>
                        </c:if>
                        <c:if test="${not empty requestScope.publisher}">
                            <span class="filter-tag">
                                NXB: "${requestScope.publisher}"
                            </span>
                        </c:if>
                        <c:if test="${not empty requestScope.minPrice || not empty requestScope.maxPrice}">
                            <span class="filter-tag">
                                Giá: ${requestScope.minPrice}₫ - ${not empty requestScope.maxPrice ? requestScope.maxPrice : '∞'}₫
                            </span>
                        </c:if>
                        <c:if test="${not empty requestScope.minYear || not empty requestScope.maxYear}">
                            <span class="filter-tag">
                                Năm: ${requestScope.minYear} - ${not empty requestScope.maxYear ? requestScope.maxYear : '∞'}
                            </span>
                        </c:if>
                    </div>
                </c:if>
            </div>

            <div class="row">
                <!-- Filter Sidebar -->
                <div class="col-lg-3 col-md-4">
                    <div class="search-filter-card">
                        <form id="advancedSearchForm" action="${pageContext.request.contextPath}/advancedSearch" method="get">
                            
                            <div class="filter-section">
                                <label class="filter-label">
                                    <i class="bi bi-search me-1"></i>Từ khóa
                                </label>
                                <input type="text" name="q" class="filter-input" 
                                    placeholder="Tên sách, tác giả..." 
                                    value="${requestScope.keyword}">
                            </div>

                            <div class="filter-section">
                                <label class="filter-label">
                                    <i class="bi bi-grid me-1"></i>Danh mục
                                </label>
                                <select name="categoryId" class="filter-input">
                                    <option value="">-- Tất cả danh mục --</option>
                                    <c:forEach var="cat" items="${requestScope.categories}">
                                        <option value="${cat.id}" ${requestScope.selectedCategoryId == cat.id ? 'selected' : ''}>
                                            ${cat.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="filter-section">
                                <label class="filter-label">
                                    <i class="bi bi-person me-1"></i>Tác giả
                                </label>
                                <input type="text" name="author" class="filter-input" 
                                    placeholder="Nhập tên tác giả" 
                                    value="${requestScope.author}">
                            </div>

                            <div class="filter-section">
                                <label class="filter-label">
                                    <i class="bi bi-building me-1"></i>Nhà xuất bản
                                </label>
                                <input type="text" name="publisher" class="filter-input" 
                                    placeholder="Nhập tên NXB" 
                                    value="${requestScope.publisher}">
                            </div>

                            <div class="filter-section">
                                <label class="filter-label">
                                    <i class="bi bi-currency-dollar me-1"></i>Khoảng giá (VNĐ)
                                </label>
                                <div class="price-range">
                                    <input type="number" name="minPrice" class="filter-input" 
                                        placeholder="Từ" min="0" 
                                        value="${requestScope.minPrice}">
                                    <span>-</span>
                                    <input type="number" name="maxPrice" class="filter-input" 
                                        placeholder="Đến" min="0" 
                                        value="${requestScope.maxPrice}">
                                </div>
                            </div>

                            <div class="filter-section">
                                <label class="filter-label">
                                    <i class="bi bi-calendar me-1"></i>Năm xuất bản
                                </label>
                                <div class="price-range">
                                    <input type="number" name="minYear" class="filter-input" 
                                        placeholder="Từ" min="1900" max="2100" 
                                        value="${requestScope.minYear}">
                                    <span>-</span>
                                    <input type="number" name="maxYear" class="filter-input" 
                                        placeholder="Đến" min="1900" max="2100" 
                                        value="${requestScope.maxYear}">
                                </div>
                            </div>

                            <div class="filter-section">
                                <label class="filter-label">
                                    <i class="bi bi-arrow-up-down me-1"></i>Sắp xếp theo
                                </label>
                                <select name="sort" class="sort-select">
                                    <option value="totalBuy-DESC" ${requestScope.sort == 'totalBuy-DESC' ? 'selected' : ''}>
                                        Bán chạy nhất
                                    </option>
                                    <option value="price-ASC" ${requestScope.sort == 'price-ASC' ? 'selected' : ''}>
                                        Giá: Thấp đến Cao
                                    </option>
                                    <option value="price-DESC" ${requestScope.sort == 'price-DESC' ? 'selected' : ''}>
                                        Giá: Cao đến Thấp
                                    </option>
                                    <option value="name-ASC" ${requestScope.sort == 'name-ASC' ? 'selected' : ''}>
                                        Tên: A đến Z
                                    </option>
                                    <option value="name-DESC" ${requestScope.sort == 'name-DESC' ? 'selected' : ''}>
                                        Tên: Z đến A
                                    </option>
                                    <option value="yearPublishing-DESC" ${requestScope.sort == 'yearPublishing-DESC' ? 'selected' : ''}>
                                        Mới nhất
                                    </option>
                                    <option value="yearPublishing-ASC" ${requestScope.sort == 'yearPublishing-ASC' ? 'selected' : ''}>
                                        Cũ nhất
                                    </option>
                                </select>
                            </div>

                            <button type="submit" class="btn btn-primary w-100">
                                <i class="bi bi-search me-2"></i>Tìm kiếm
                            </button>
                            
                            <a href="${pageContext.request.contextPath}/advancedSearch" 
                               class="btn btn-outline-secondary w-100 mt-2">
                                <i class="bi bi-x-circle me-2"></i>Xóa bộ lọc
                            </a>
                        </form>
                    </div>
                </div>

                <!-- Results -->
                <div class="col-lg-9 col-md-8">
                    <c:choose>
                        <c:when test="${requestScope.totalProducts == 0}">
                            <div class="no-results">
                                <i class="bi bi-search"></i>
                                <h4>Không tìm thấy sản phẩm nào</h4>
                                <p>Không có sản phẩm nào khớp với tiêu chí tìm kiếm của bạn.</p>
                                <a href="${pageContext.request.contextPath}/advancedSearch" 
                                   class="btn btn-primary">
                                    <i class="bi bi-arrow-repeat me-2"></i>Thử lại với bộ lọc khác
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="row item-grid">
                                <c:forEach var="product" items="${requestScope.products}">
                                    <div class="col-xl-4 col-lg-6 col-md-6 mb-4">
                                        <div class="card p-3 h-100">
                                            <figure class="text-center">
                                                <a href="${pageContext.request.contextPath}/product?id=${product.id}">
                                                    <c:choose>
                                                        <c:when test="${empty product.imageName}">
                                                            <img width="180" height="180" class="img-fluid"
                                                                src="${pageContext.request.contextPath}/img/280px.png"
                                                                alt="280px.png">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img width="180" height="180" class="img-fluid"
                                                                src="${pageContext.request.contextPath}/image/${product.imageName}"
                                                                alt="${product.imageName}">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </a>
                                                <figcaption class="info-wrap mt-2">
                                                    <a href="${pageContext.request.contextPath}/product?id=${product.id}" 
                                                       class="title text-decoration-none">${product.name}</a>
                                                    <div class="text-muted small mb-2">
                                                        <i class="bi bi-person me-1"></i>${product.author}
                                                    </div>
                                                    <div class="text-muted small mb-2">
                                                        <i class="bi bi-building me-1"></i>${product.publisher}
                                                    </div>
                                                    <div class="text-muted small mb-3">
                                                        <i class="bi bi-calendar me-1"></i>${product.yearPublishing}
                                                    </div>
                                                    <div>
                                                        <c:choose>
                                                            <c:when test="${product.discount == 0}">
                                                                <span class="price fw-bold text-danger"> 
                                                                    <fmt:formatNumber pattern="#,##0" value="${product.price}" />₫
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="price fw-bold text-danger"> 
                                                                    <fmt:formatNumber pattern="#,##0" value="${product.price * (100 - product.discount) / 100}" />₫
                                                                </span>
                                                                <span class="ms-2 text-muted text-decoration-line-through small">
                                                                    <fmt:formatNumber pattern="#,##0" value="${product.price}" />₫
                                                                </span>
                                                                <span class="ms-2 badge bg-danger"> 
                                                                    -<fmt:formatNumber pattern="#,##0" value="${product.discount}" />%
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </figcaption>
                                            </figure>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <!-- Pagination -->
                            <c:if test="${requestScope.totalPages > 1}">
                                <nav aria-label="Page navigation">
                                    <ul class="pagination justify-content-center">
                                        <c:choose>
                                            <c:when test="${requestScope.page == 1}">
                                                <li class="page-item disabled">
                                                    <a class="page-link" href="#">Trang trước</a>
                                                </li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="page-item">
                                                    <a class="page-link" 
                                                       href="${pageContext.request.contextPath}/advancedSearch?page=${requestScope.page - 1}${requestScope.baseQueryString}">
                                                        Trang trước
                                                    </a>
                                                </li>
                                            </c:otherwise>
                                        </c:choose>

                                        <c:set var="startPage" value="${requestScope.page - 2}" />
                                        <c:if test="${startPage < 1}">
                                            <c:set var="startPage" value="1" />
                                        </c:if>
                                        
                                        <c:set var="endPage" value="${requestScope.page + 2}" />
                                        <c:if test="${endPage > requestScope.totalPages}">
                                            <c:set var="endPage" value="${requestScope.totalPages}" />
                                        </c:if>

                                        <c:if test="${startPage > 1}">
                                            <li class="page-item">
                                                <a class="page-link" 
                                                   href="${pageContext.request.contextPath}/advancedSearch?page=1${requestScope.baseQueryString}">
                                                    1
                                                </a>
                                            </li>
                                            <c:if test="${startPage > 2}">
                                                <li class="page-item disabled"><span class="page-link">...</span></li>
                                            </c:if>
                                        </c:if>

                                        <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                            <c:choose>
                                                <c:when test="${requestScope.page == i}">
                                                    <li class="page-item active">
                                                        <a class="page-link">${i}</a>
                                                    </li>
                                                </c:when>
                                                <c:otherwise>
                                                    <li class="page-item">
                                                        <a class="page-link" 
                                                           href="${pageContext.request.contextPath}/advancedSearch?page=${i}${requestScope.baseQueryString}">
                                                            ${i}
                                                        </a>
                                                    </li>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>

                                        <c:if test="${endPage < requestScope.totalPages}">
                                            <c:if test="${endPage < requestScope.totalPages - 1}">
                                                <li class="page-item disabled"><span class="page-link">...</span></li>
                                            </c:if>
                                            <li class="page-item">
                                                <a class="page-link" 
                                                   href="${pageContext.request.contextPath}/advancedSearch?page=${requestScope.totalPages}${requestScope.baseQueryString}">
                                                    ${requestScope.totalPages}
                                                </a>
                                            </li>
                                        </c:if>

                                        <c:choose>
                                            <c:when test="${requestScope.page == requestScope.totalPages}">
                                                <li class="page-item disabled">
                                                    <a class="page-link" href="#">Trang sau</a>
                                                </li>
                                            </c:when>
                                            <c:otherwise>
                                                <li class="page-item">
                                                    <a class="page-link" 
                                                       href="${pageContext.request.contextPath}/advancedSearch?page=${requestScope.page + 1}${requestScope.baseQueryString}">
                                                        Trang sau
                                                    </a>
                                                </li>
                                            </c:otherwise>
                                        </c:choose>
                                    </ul>
                                </nav>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </section>

    <jsp:include page="_footer.jsp" />
</body>

</html>
