<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý vận chuyển - BookShop Admin</title>
    <meta name="sm-context-path" content="${pageContext.request.contextPath}">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/shippingMethodManagerView.css">
</head>
<body>
<jsp:include page="../_headerAdmin.jsp" />

<div class="sm-admin">
    <div class="sm-hero">
        <div class="sm-hero-content">
            <div class="sm-hero-row">
                <div class="sm-hero-text">
                    <div class="sm-hero-badge">    
                    </div>
                    <div class="sm-hero-icon">
                        <i class="fas fa-truck-fast"></i>
                    </div>
                    <h1>Trung tâm quản lý vận chuyển</h1>
                    <p>Quản lý phương thức giao hàng, phí ship, khu vực và điều phối vận đơn</p>
                </div>
                <div class="sm-hero-actions">
                    <button type="button" class="sm-hero-btn sm-hero-btn-primary" data-bs-toggle="modal" data-bs-target="#modal-add-method">
                        <i class="fas fa-plus"></i> Thêm phương thức
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/shipmentManager" class="sm-hero-btn sm-hero-btn-outline">
                        <i class="fas fa-file-invoice"></i> Tạo vận đơn
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="sm-stats-grid">
        <div class="sm-stat-card card-primary">
            <div class="sm-stat-icon-wrap sm-stat-icon-primary">
                <i class="fas fa-boxes-stacked"></i>
            </div>
            <div class="sm-stat-body">
                <h3>${requestScope.totalMethods != null ? requestScope.totalMethods : 0}</h3>
                <p>Phương thức vận chuyển</p>
            </div>
        </div>
        <div class="sm-stat-card card-success">
            <div class="sm-stat-icon-wrap sm-stat-icon-success">
                <i class="fas fa-check-circle"></i>
            </div>
            <div class="sm-stat-body">
                <h3>${requestScope.activeMethods != null ? requestScope.activeMethods : 0}</h3>
                <p>Đang hoạt động</p>
            </div>
        </div>
        <div class="sm-stat-card card-warning">
            <div class="sm-stat-icon-wrap sm-stat-icon-warning">
                <i class="fas fa-bolt"></i>
            </div>
            <div class="sm-stat-body">
                <h3>${requestScope.expressMethods != null ? requestScope.expressMethods : 0}</h3>
                <p>Giao nhanh</p>
            </div>
        </div>
        <div class="sm-stat-card card-info">
            <div class="sm-stat-icon-wrap sm-stat-icon-info">
                <i class="fas fa-truck"></i>
            </div>
            <div class="sm-stat-body">
                <h3>${requestScope.inactiveMethods != null ? requestScope.inactiveMethods : 0}</h3>
                <p>Giao tiêu chuẩn</p>
            </div>
        </div>
    </div>

    <div class="sm-alert-wrap">
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="sm-alert sm-alert-success">
                <i class="fas fa-check-circle"></i>
                <span>${sessionScope.successMessage}</span>
            </div>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <div class="sm-alert sm-alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <span>${sessionScope.errorMessage}</span>
            </div>
        </c:if>
        <c:remove var="successMessage" scope="session" />
        <c:remove var="errorMessage" scope="session" />
    </div>

    <div class="sm-content-area">
        <div class="sm-card">
            <!-- ===== TABS ===== -->
            <div class="sm-tabs-header">
                <button class="sm-tab-btn active" data-tab="methods">
                    <i class="fas fa-truck-fast"></i> Phương thức
                    <span class="sm-tab-badge">${requestScope.totalMethods != null ? requestScope.totalMethods : 0}</span>
                </button>
                <button class="sm-tab-btn" data-tab="zones">
                    <i class="fas fa-map-marked-alt"></i> Khu vực
                    <span class="sm-tab-badge">${requestScope.totalZones != null ? requestScope.totalZones : 0}</span>
                </button>
                <button class="sm-tab-btn" data-tab="weight-fees">
                    <i class="fas fa-scale-balanced"></i> Phí theo trọng lượng
                    <span class="sm-tab-badge">${requestScope.totalWeightFees != null ? requestScope.totalWeightFees : 0}</span>
                </button>
                <button class="sm-tab-btn" data-tab="provinces">
                    <i class="fas fa-building"></i> Tỉnh / Thành
                    <span class="sm-tab-badge">${requestScope.totalProvinces != null ? requestScope.totalProvinces : 0}</span>
                </button>
                <button class="sm-tab-btn" data-tab="statistics">
                    <i class="fas fa-chart-line"></i> Thống kê
                    <span class="sm-tab-badge">${requestScope.totalStatistics != null ? requestScope.totalStatistics : 0}</span>
                </button>
            </div>

            <div class="sm-tab-content active" id="tab-methods">
                <div class="sm-table-toolbar">
                    <h4><i class="fas fa-list"></i> Danh sách phương thức vận chuyển</h4>
                    <div class="sm-toolbar-right">
                        <div class="sm-search-wrap">
                            <i class="fas fa-search"></i>
                            <input type="text" id="search-methods" placeholder="Tìm kiếm phương thức..." data-search="table-methods">
                        </div>
                        <button type="button" class="sm-btn sm-btn-primary" data-bs-toggle="modal" data-bs-target="#modal-add-method">
                            <i class="fas fa-plus"></i> Thêm mới
                        </button>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty requestScope.shippingMethods}">
                        <table class="sm-data-table" id="table-methods">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Phương thức</th>
                                    <th>Nhà cung cấp</th>
                                    <th>Thời gian giao</th>
                                    <th>Phí / kg</th>
                                    <th>Loại</th>
                                    <th>Miễn phí từ</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="m" items="${requestScope.shippingMethods}" varStatus="i">
                                    <tr data-method-id="${m.id}"
                                        data-name="${fn:escapeXml(fn:toLowerCase(m.name))}"
                                        data-days="${m.estimatedDays}"
                                        data-price="${m.pricePerKg}"
                                        data-provider="${m.providerType}"
                                        data-ghn-service-id="${m.ghnServiceId}"
                                        data-express="${m.express}"
                                        data-surcharge="${m.expressSurcharge}"
                                        data-threshold="${m.freeShippingThreshold}">
                                        <td class="text-muted fw-medium">${i.index + 1}</td>
                                        <td>
                                            <div class="d-flex align-items-center gap-2">
                                                <div class="sm-provider-icon sm-provider-${fn:toLowerCase(m.providerType)}">
                                                    <i class="fas fa-truck"></i>
                                                </div>
                                                <div>
                                                    <div class="fw-semibold">${fn:escapeXml(m.name)}</div>
                                                    <small class="text-muted">ID: ${m.id}</small>
                                                </div>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="sm-badge sm-badge-${fn:toLowerCase(m.providerType)}">
                                                <i class="fas fa-building"></i> ${m.providerType}
                                            </span>
                                        </td>
                                        <td><strong>${m.estimatedDays}</strong> ngày</td>
                                        <td>
                                            <span class="sm-price">
                                                <fmt:formatNumber pattern="#,##0" value="${m.pricePerKg}" />đ
                                                <small>/kg</small>
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${m.express}">
                                                    <span class="sm-badge sm-badge-express"><i class="fas fa-bolt"></i> Giao nhanh</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sm-badge sm-badge-standard">Tiêu chuẩn</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${m.freeShippingThreshold > 0}">
                                                    <span class="sm-price"><fmt:formatNumber pattern="#,##0" value="${m.freeShippingThreshold}" />đ</span>
                                                </c:when>
                                                <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <label class="sm-toggle">
                                                <input type="checkbox" ${m.status == 1 ? 'checked' : ''}
                                                    data-id="${m.id}" data-name="${fn:escapeXml(m.name)}"
                                                    onchange="smToggleStatus(this)">
                                                <span class="sm-toggle-slider"></span>
                                            </label>
                                        </td>
                                        <td>
                                            <div class="sm-action-group">
                                                <button type="button" class="sm-action-btn" title="Sửa"
                                                    onclick="smEditMethod(${m.id})">
                                                    <i class="fas fa-pen"></i>
                                                </button>
                                                <button type="button" class="sm-action-btn danger" title="Xóa"
                                                    onclick="smDeleteMethod(${m.id}, '${fn:escapeXml(m.name)}')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="sm-empty">
                            <div class="sm-empty-icon"><i class="fas fa-truck-fast"></i></div>
                            <h5>Chưa có phương thức vận chuyển nào</h5>
                            <p>Thêm phương thức vận chuyển đầu tiên để bắt đầu quản lý giao hàng.</p>
                            <button type="button" class="sm-btn sm-btn-primary" data-bs-toggle="modal" data-bs-target="#modal-add-method">
                                <i class="fas fa-plus"></i> Thêm phương thức
                            </button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
          
            <div class="sm-tab-content" id="tab-zones">
                <div class="sm-table-toolbar">
                    <h4><i class="fas fa-map"></i> Khu vực giao hàng</h4>
                    <div class="sm-toolbar-right">
                        <div class="sm-search-wrap">
                            <i class="fas fa-search"></i>
                            <input type="text" id="search-zones" placeholder="Tìm kiếm khu vực..." data-search="table-zones">
                        </div>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty requestScope.shippingZones}">
                        <table class="sm-data-table" id="table-zones">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Khu vực</th>
                                    <th>Loại vùng</th>
                                    <th>Phí cơ bản</th>
                                    <th>Phí / kg</th>
                                    <th>Thời gian giao</th>
                                    <th>Mô tả</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="z" items="${requestScope.shippingZones}" varStatus="i">
                                    <tr data-zone-id="${z.id}"
                                        data-name="${fn:escapeXml(fn:toLowerCase(z.zoneName))}"
                                        data-zonetype="${z.zoneType}"
                                        data-basefee="${z.baseFee}"
                                        data-pricekg="${z.pricePerKg}"
                                        data-desc="${fn:escapeXml(z.description)}"
                                        data-zonestatus="${z.status}">
                                        <td class="text-muted fw-medium">${i.index + 1}</td>
                                        <td><strong>${fn:escapeXml(z.zoneName)}</strong></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${z.zoneType == 'INNER'}">
                                                    <span class="sm-badge sm-badge-inner"><i class="fas fa-city"></i> Nội thành</span>
                                                </c:when>
                                                <c:when test="${z.zoneType == 'PROVINCIAL'}">
                                                    <span class="sm-badge sm-badge-provincial"><i class="fas fa-map"></i> Tỉnh lẻ</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sm-badge sm-badge-remote"><i class="fas fa-mountain"></i> Vùng xa</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><span class="sm-price"><fmt:formatNumber pattern="#,##0" value="${z.baseFee}" />đ</span></td>
                                        <td><span class="sm-price"><fmt:formatNumber pattern="#,##0" value="${z.pricePerKg}" />đ</span></td>
                                        <td>${z.estimatedDaysMin}–${z.estimatedDaysMax} ngày</td>
                                        <td class="text-muted"><small>${fn:escapeXml(z.description)}</small></td>
                                        <td>
                                            <span class="sm-badge ${z.status == 1 ? 'sm-badge-active' : 'sm-badge-inactive'}">
                                                <span class="sm-badge-dot"></span>
                                                ${z.status == 1 ? 'Hoạt động' : 'Tạm ngừng'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="sm-action-group">
                                                <button type="button" class="sm-action-btn" title="Sửa" onclick="smEditZone(${z.id})">
                                                    <i class="fas fa-pen"></i>
                                                </button>
                                                <button type="button" class="sm-action-btn danger" title="Xóa"
                                                    onclick="smDeleteZone(${z.id}, '${fn:escapeXml(z.zoneName)}')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="sm-empty">
                            <div class="sm-empty-icon"><i class="fas fa-map-marked-alt"></i></div>
                            <h5>Chưa có khu vực giao hàng</h5>
                            <p>Thêm khu vực giao hàng để phân loại phí vận chuyển theo vùng miền.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="sm-tab-content" id="tab-weight-fees">
                <div class="sm-table-toolbar">
                    <h4><i class="fas fa-scale-balanced"></i> Biểu phí theo trọng lượng</h4>
                    <div class="sm-toolbar-right">
                        <div class="sm-search-wrap">
                            <i class="fas fa-search"></i>
                            <input type="text" id="search-weight-fees" placeholder="Tìm kiếm biểu phí..." data-search="table-weight-fees">
                        </div>
                        <button type="button" class="sm-btn sm-btn-primary" onclick="smOpenAddWeightFee()">
                            <i class="fas fa-plus"></i> Thêm biểu phí
                        </button>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty requestScope.weightFees}">
                        <table class="sm-data-table" id="table-weight-fees">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Phương thức</th>
                                    <th>Khu vực</th>
                                    <th>Khoảng cân nặng (kg)</th>
                                    <th>Phí cơ bản</th>
                                    <th>Phí vượt kg</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="f" items="${requestScope.weightFees}" varStatus="i">
                                    <tr data-fee-id="${f.id}"
                                        data-feemethod="${f.shippingMethodId}"
                                        data-method-name="${fn:escapeXml(f.methodName)}"
                                        data-feezone="${f.zoneType}"
                                        data-feeminw="${f.minWeight}"
                                        data-feemaxw="${f.maxWeight}"
                                        data-feebase="${f.baseFee}"
                                        data-feeperkg="${f.feePerKg}">
                                        <td class="text-muted fw-medium">${i.index + 1}</td>
                                        <td><strong>${fn:escapeXml(f.methodName)}</strong></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${f.zoneType == 'INNER'}">
                                                    <span class="sm-badge sm-badge-inner"><i class="fas fa-city"></i> Nội thành</span>
                                                </c:when>
                                                <c:when test="${f.zoneType == 'PROVINCIAL'}">
                                                    <span class="sm-badge sm-badge-provincial"><i class="fas fa-map"></i> Tỉnh lẻ</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sm-badge sm-badge-remote"><i class="fas fa-mountain"></i> Vùng xa</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span class="sm-weight-range">
                                                <i class="fas fa-arrows-alt-v"></i>
                                                ${f.minWeight} – ${f.maxWeight} kg
                                            </span>
                                        </td>
                                        <td>
                                            <div class="sm-price">
                                                <fmt:formatNumber pattern="#,##0" value="${f.baseFee}" />đ
                                            </div>
                                        </td>
                                        <td>
                                            <div class="sm-price">
                                                +<fmt:formatNumber pattern="#,##0" value="${f.feePerKg}" />đ/kg
                                            </div>
                                        </td>
                                        <td>
                                            <div class="sm-action-group">
                                                <button type="button" class="sm-action-btn" title="Sửa" onclick="smEditWeightFee(${f.id})">
                                                    <i class="fas fa-pen"></i>
                                                </button>
                                                <button type="button" class="sm-action-btn danger" title="Xóa" onclick="smDeleteWeightFee(${f.id})">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="sm-empty">
                            <div class="sm-empty-icon"><i class="fas fa-scale-balanced"></i></div>
                            <h5>Chưa có biểu phí theo trọng lượng</h5>
                            <p>Thêm biểu phí để tính phí ship chính xác theo cân nặng đơn hàng.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
                  
            <div class="sm-tab-content" id="tab-provinces">
                <div class="sm-table-toolbar">
                    <h4><i class="fas fa-building"></i> Tỉnh / Thành phố</h4>
                    <div class="sm-toolbar-right">
                        <div class="sm-search-wrap">
                            <i class="fas fa-search"></i>
                            <input type="text" id="search-provinces" placeholder="Tìm kiếm tỉnh/thành..." data-search="table-provinces">
                        </div>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty requestScope.provinces}">
                        <table class="sm-data-table" id="table-provinces">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Mã</th>
                                    <th>Tên tỉnh / thành</th>
                                    <th>Loại</th>
                                    <th>Vùng miền</th>
                                    <th>Thành phố lớn</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${requestScope.provinces}" varStatus="i">
                                    <tr data-province-id="${p.id}"
                                        data-provcode="${fn:escapeXml(p.provinceCode)}"
                                        data-provname="${fn:escapeXml(fn:toLowerCase(p.provinceName))}"
                                        data-provtype="${p.provinceType}"
                                        data-provregion="${p.region}"
                                        data-provmetro="${p.metroCity}">
                                        <td class="text-muted fw-medium">${i.index + 1}</td>
                                        <td><code>${fn:escapeXml(p.provinceCode)}</code></td>
                                        <td><strong>${fn:escapeXml(p.provinceName)}</strong></td>
                                        <td><span class="text-muted">${p.provinceType}</span></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.region == 'MienBac'}">
                                                    <span class="sm-badge sm-badge-info"><i class="fas fa-map-pin"></i> Miền Bắc</span>
                                                </c:when>
                                                <c:when test="${p.region == 'MienTrung'}">
                                                    <span class="sm-badge sm-badge-warning"><i class="fas fa-map-pin"></i> Miền Trung</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sm-badge sm-badge-success"><i class="fas fa-map-pin"></i> Miền Nam</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.metroCity}">
                                                    <span class="sm-badge sm-badge-active"><i class="fas fa-star"></i> Có</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sm-badge sm-badge-inactive">Không</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="sm-action-group">
                                                <button type="button" class="sm-action-btn" title="Sửa" onclick="smEditProvince(${p.id})">
                                                    <i class="fas fa-pen"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="sm-empty">
                            <div class="sm-empty-icon"><i class="fas fa-building"></i></div>
                            <h5>Chưa có dữ liệu tỉnh / thành phố</h5>
                            <p>Danh sách tỉnh thành sẽ được hiển thị tại đây sau khi được thêm vào hệ thống.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="sm-tab-content" id="tab-statistics">
                <!-- Dashboard Header -->
                <div class="sm-stats-header">
                    <div class="sm-stats-header-left">
                        <h2><i class="fas fa-chart-line"></i> Thống kê vận chuyển</h2>
                        <p>Theo dõi hiệu suất giao hàng, doanh thu vận chuyển và hoạt động logistics toàn hệ thống</p>
                    </div>
                    <div class="sm-stats-header-right">
                        <select class="sm-stats-filter" id="stats-filter-period" onchange="smLoadStatsByPeriod(this.value)">
                            <option value="today" ${requestScope.currentStatsPeriod == 'today' ? 'selected' : ''}>Hôm nay</option>
                            <option value="7days" ${empty requestScope.currentStatsPeriod or requestScope.currentStatsPeriod == '7days' ? 'selected' : ''}>7 ngày</option>
                            <option value="30days" ${requestScope.currentStatsPeriod == '30days' ? 'selected' : ''}>30 ngày</option>
                            <option value="90days" ${requestScope.currentStatsPeriod == '90days' ? 'selected' : ''}>90 ngày</option>
                        </select>
                        <button type="button" class="sm-stats-export-btn" onclick="smExportStats(document.getElementById('stats-filter-period').value)">
                            <i class="fas fa-download"></i> Xuất báo cáo
                        </button>
                    </div>
                </div>
       
                <div class="sm-statistics-grid">
                    <div class="sm-statistics-card">
                        <div class="sm-statistics-icon-wrap sm-stat-icon-purple">
                            <i class="fas fa-boxes-stacked"></i>
                        </div>
                        <div>
                            <p class="sm-statistics-value" id="stat-total-orders">${requestScope.totalShippingOrders != null ? requestScope.totalShippingOrders : 0}</p>
                            <p class="sm-statistics-label">Tổng đơn vận chuyển</p>
                        </div>
                    </div>
                    <div class="sm-statistics-card">
                        <div class="sm-statistics-icon-wrap sm-stat-icon-orange">
                            <i class="fas fa-bolt"></i>
                        </div>
                        <div>
                            <p class="sm-statistics-value">${requestScope.expressOrders != null ? requestScope.expressOrders : 0}</p>
                            <p class="sm-statistics-label">Đơn giao nhanh</p>
                        </div>
                    </div>
                    <div class="sm-statistics-card">
                        <div class="sm-statistics-icon-wrap sm-stat-icon-blue">
                            <i class="fas fa-truck"></i>
                        </div>
                        <div>
                            <p class="sm-statistics-value">${requestScope.standardOrders != null ? requestScope.standardOrders : 0}</p>
                            <p class="sm-statistics-label">Đơn giao tiêu chuẩn</p>
                        </div>
                    </div>
                    <div class="sm-statistics-card">
                        <div class="sm-statistics-icon-wrap sm-stat-icon-green">
                            <i class="fas fa-coins"></i>
                        </div>
                        <div>
                            <p class="sm-statistics-value" id="stat-shipping-revenue">
                                <fmt:formatNumber pattern="#,##0" value="${requestScope.shippingRevenue != null ? requestScope.shippingRevenue : 0}" />đ
                            </p>
                            <p class="sm-statistics-label">Doanh thu phí ship</p>
                        </div>
                    </div>
                    <div class="sm-statistics-card">
                        <div class="sm-statistics-icon-wrap sm-stat-icon-teal">
                            <i class="fas fa-truck-fast"></i>
                        </div>
                        <div>
                            <p class="sm-statistics-value" id="stat-delivering">${requestScope.deliveringOrders != null ? requestScope.deliveringOrders : 0}</p>
                            <p class="sm-statistics-label">Đơn đang giao</p>
                        </div>
                    </div>
                    <div class="sm-statistics-card">
                        <div class="sm-statistics-icon-wrap sm-stat-icon-pink">
                            <i class="fas fa-circle-check"></i>
                        </div>
                        <div>
                            <p class="sm-statistics-value" id="stat-completed">${requestScope.completedOrders != null ? requestScope.completedOrders : 0}</p>
                            <p class="sm-statistics-label">Đơn hoàn thành</p>
                        </div>
                    </div>
                </div>

                <!-- Charts Row: Method Stats + Status Stats -->
                <div class="sm-charts-row">
                    <!-- Left: Phuong thuc van chuyen -->
                    <div class="sm-chart-card">
                        <h4 class="sm-chart-card-title"><i class="fas fa-truck-fast"></i> Phân bổ phương thức vận chuyển</h4>
                        <c:choose>
                            <c:when test="${(requestScope.expressOrders != null && requestScope.expressOrders > 0) || (requestScope.standardOrders != null && requestScope.standardOrders > 0)}">
                                <c:set var="totalMethodOrders" value="${(requestScope.expressOrders != null ? requestScope.expressOrders : 0) + (requestScope.standardOrders != null ? requestScope.standardOrders : 0)}" />
                                <c:if test="${totalMethodOrders > 0}">
                                    <div class="sm-chart-item">
                                        <div class="sm-chart-item-header">
                                            <span class="sm-chart-item-label">
                                                <span class="sm-chart-item-label-dot" style="background:#F97316"></span>
                                                Giao nhanh
                                            </span>
                                            <span class="sm-chart-item-value">
                                                ${requestScope.expressOrders != null ? requestScope.expressOrders : 0}
                                                <span>(<fmt:formatNumber pattern="##0.0" value="${(requestScope.expressOrders != null ? requestScope.expressOrders : 0) * 100.0 / totalMethodOrders}" />%)</span>
                                            </span>
                                        </div>
                                        <div class="sm-analytics-progress">
                                            <div class="sm-analytics-progress-bar sm-progress-orange" style="width: ${(requestScope.expressOrders != null ? requestScope.expressOrders : 0) * 100.0 / totalMethodOrders}%"></div>
                                        </div>
                                    </div>
                                    <div class="sm-chart-item">
                                        <div class="sm-chart-item-header">
                                            <span class="sm-chart-item-label">
                                                <span class="sm-chart-item-label-dot" style="background:#3B82F6"></span>
                                                Giao tiêu chuẩn
                                            </span>
                                            <span class="sm-chart-item-value">
                                                ${requestScope.standardOrders != null ? requestScope.standardOrders : 0}
                                                <span>(<fmt:formatNumber pattern="##0.0" value="${(requestScope.standardOrders != null ? requestScope.standardOrders : 0) * 100.0 / totalMethodOrders}" />%)</span>
                                            </span>
                                        </div>
                                        <div class="sm-analytics-progress">
                                            <div class="sm-analytics-progress-bar sm-progress-blue" style="width: ${(requestScope.standardOrders != null ? requestScope.standardOrders : 0) * 100.0 / totalMethodOrders}%"></div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="sm-stat-empty">
                                    <div class="sm-stat-empty-icon"><i class="fas fa-chart-pie"></i></div>
                                    <h5>Chưa có dữ liệu phương thức</h5>
                                    <p>Dữ liệu sẽ hiển thị khi có đơn vận chuyển</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Right: Trang thai van don -->
                    <div class="sm-chart-card">
                        <h4 class="sm-chart-card-title"><i class="fas fa-flag"></i> Phân bổ trạng thái vận đơn</h4>
                        <c:choose>
                            <c:when test="${requestScope.totalShippingOrders != null && requestScope.totalShippingOrders > 0}">
                                <c:set var="totalOrders" value="${requestScope.totalShippingOrders}" />
                                <div class="sm-chart-item">
                                    <div class="sm-chart-item-header">
                                        <span class="sm-chart-item-label">
                                            <span class="sm-chart-item-label-dot" style="background:#F59E0B"></span>
                                            Chờ xác nhận
                                        </span>
                                        <span class="sm-chart-item-value">
                                            ${requestScope.pendingOrders != null ? requestScope.pendingOrders : 0}
                                            <span>(<fmt:formatNumber pattern="##0.0" value="${totalOrders > 0 ? ((requestScope.pendingOrders != null ? requestScope.pendingOrders : 0) * 100.0 / totalOrders) : 0}" />%)</span>
                                        </span>
                                    </div>
                                    <div class="sm-analytics-progress">
                                        <div class="sm-analytics-progress-bar sm-progress-orange" style="width: ${totalOrders > 0 ? ((requestScope.pendingOrders != null ? requestScope.pendingOrders : 0) * 100.0 / totalOrders) : 0}%"></div>
                                    </div>
                                </div>
                                <div class="sm-chart-item">
                                    <div class="sm-chart-item-header">
                                        <span class="sm-chart-item-label">
                                            <span class="sm-chart-item-label-dot" style="background:#8B5CF6"></span>
                                            Đã lấy hàng
                                        </span>
                                        <span class="sm-chart-item-value">
                                            ${requestScope.pickedOrders != null ? requestScope.pickedOrders : 0}
                                            <span>(<fmt:formatNumber pattern="##0.0" value="${totalOrders > 0 ? ((requestScope.pickedOrders != null ? requestScope.pickedOrders : 0) * 100.0 / totalOrders) : 0}" />%)</span>
                                        </span>
                                    </div>
                                    <div class="sm-analytics-progress">
                                        <div class="sm-analytics-progress-bar sm-progress-purple" style="width: ${totalOrders > 0 ? ((requestScope.pickedOrders != null ? requestScope.pickedOrders : 0) * 100.0 / totalOrders) : 0}%"></div>
                                    </div>
                                </div>
                                <div class="sm-chart-item">
                                    <div class="sm-chart-item-header">
                                        <span class="sm-chart-item-label">
                                            <span class="sm-chart-item-label-dot" style="background:#3B82F6"></span>
                                            Đang vận chuyển
                                        </span>
                                        <span class="sm-chart-item-value">
                                            ${requestScope.shippingOrders != null ? requestScope.shippingOrders : 0}
                                            <span>(<fmt:formatNumber pattern="##0.0" value="${totalOrders > 0 ? ((requestScope.shippingOrders != null ? requestScope.shippingOrders : 0) * 100.0 / totalOrders) : 0}" />%)</span>
                                        </span>
                                    </div>
                                    <div class="sm-analytics-progress">
                                        <div class="sm-analytics-progress-bar sm-progress-blue" style="width: ${totalOrders > 0 ? ((requestScope.shippingOrders != null ? requestScope.shippingOrders : 0) * 100.0 / totalOrders) : 0}%"></div>
                                    </div>
                                </div>
                                <div class="sm-chart-item">
                                    <div class="sm-chart-item-header">
                                        <span class="sm-chart-item-label">
                                            <span class="sm-chart-item-label-dot" style="background:#14B8A6"></span>
                                            Đang giao
                                        </span>
                                        <span class="sm-chart-item-value">
                                            ${requestScope.deliveringOrders != null ? requestScope.deliveringOrders : 0}
                                            <span>(<fmt:formatNumber pattern="##0.0" value="${totalOrders > 0 ? ((requestScope.deliveringOrders != null ? requestScope.deliveringOrders : 0) * 100.0 / totalOrders) : 0}" />%)</span>
                                        </span>
                                    </div>
                                    <div class="sm-analytics-progress">
                                        <div class="sm-analytics-progress-bar sm-progress-teal" style="width: ${totalOrders > 0 ? ((requestScope.deliveringOrders != null ? requestScope.deliveringOrders : 0) * 100.0 / totalOrders) : 0}%"></div>
                                    </div>
                                </div>
                                <div class="sm-chart-item">
                                    <div class="sm-chart-item-header">
                                        <span class="sm-chart-item-label">
                                            <span class="sm-chart-item-label-dot" style="background:#10B981"></span>
                                            Đã giao
                                        </span>
                                        <span class="sm-chart-item-value">
                                            ${requestScope.completedOrders != null ? requestScope.completedOrders : 0}
                                            <span>(<fmt:formatNumber pattern="##0.0" value="${totalOrders > 0 ? ((requestScope.completedOrders != null ? requestScope.completedOrders : 0) * 100.0 / totalOrders) : 0}" />%)</span>
                                        </span>
                                    </div>
                                    <div class="sm-analytics-progress">
                                        <div class="sm-analytics-progress-bar sm-progress-green" style="width: ${totalOrders > 0 ? ((requestScope.completedOrders != null ? requestScope.completedOrders : 0) * 100.0 / totalOrders) : 0}%"></div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="sm-stat-empty">
                                    <div class="sm-stat-empty-icon"><i class="fas fa-flag"></i></div>
                                    <h5>Chưa có dữ liệu trạng thái</h5>
                                    <p>Dữ liệu sẽ hiển thị khi có vận đơn</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Top Shipping Methods -->
                <div class="sm-top-methods-section">
                    <h4 class="sm-section-title"><i class="fas fa-trophy"></i> Top phương thức vận chuyển</h4>
                    <c:choose>
                        <c:when test="${not empty requestScope.topShippingMethods}">
                            <table class="sm-statistics-table">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Tên phương thức</th>
                                        <th>Nhà cung cấp</th>
                                        <th>Tổng đơn</th>
                                        <th>Doanh thu</th>
                                        <th>Tỷ lệ sử dụng</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody id="stats-top-methods-body">
                                    <c:forEach var="tsm" items="${requestScope.topShippingMethods}" varStatus="i">
                                        <tr>
                                            <td>
                                                <span class="sm-stat-rank ${i.index == 0 ? 'gold' : (i.index == 1 ? 'silver' : (i.index == 2 ? 'bronze' : ''))}">${i.index + 1}</span>
                                            </td>
                                            <td>
                                                <div class="sm-stat-method-name">
                                                    <div class="sm-stat-method-icon sm-provider-${fn:toLowerCase(tsm.providerType)}">
                                                        <i class="fas fa-truck"></i>
                                                    </div>
                                                    ${fn:escapeXml(tsm.methodName)}
                                                </div>
                                            </td>
                                            <td>
                                                <span class="sm-badge sm-badge-${fn:toLowerCase(tsm.providerType)}">
                                                    <i class="fas fa-building"></i> ${tsm.providerType}
                                                </span>
                                            </td>
                                            <td><strong>${tsm.totalOrders}</strong></td>
                                            <td>
                                                <span class="sm-price">
                                                    <fmt:formatNumber pattern="#,##0" value="${tsm.totalRevenue}" />đ
                                                </span>
                                            </td>
                                            <td>
                                                <div class="sm-stat-usage-bar">
                                                    <div class="sm-stat-usage-bar-track">
                                                        <div class="sm-stat-usage-bar-fill" style="width: ${tsm.usagePercent > 0 ? tsm.usagePercent : 0}%"></div>
                                                    </div>
                                                    <span class="sm-stat-usage-percent">
                                                        <fmt:formatNumber pattern="#0" value="${tsm.usagePercent > 0 ? tsm.usagePercent : 0}" />%
                                                    </span>
                                                </div>
                                            </td>
                                            <td>
                                                <label class="sm-stat-toggle">
                                                    <input type="checkbox" ${tsm.status == 1 ? 'checked' : ''}>
                                                    <span class="sm-stat-toggle-slider"></span>
                                                </label>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="sm-stat-empty">
                                <div class="sm-stat-empty-icon"><i class="fas fa-chart-bar"></i></div>
                                <h5>Chưa có dữ liệu thống kê vận chuyển</h5>
                                <p>Top phương thức sẽ hiển thị khi có dữ liệu đơn hàng vận chuyển</p>
                                <button type="button" class="sm-btn sm-btn-primary" onclick="window.location.reload()">
                                    <i class="fas fa-redo"></i> Tải lại dữ liệu
                                </button>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Logistics Analytics Cards -->
                <div class="sm-logistics-grid">
                    <div class="sm-logistics-card">
                        <div class="sm-logistics-icon-wrap sm-stat-icon-blue">
                            <i class="fas fa-map-marked-alt"></i>
                        </div>
                        <p class="sm-logistics-value" id="stat-top-province">${not empty requestScope.topProvince ? fn:escapeXml(requestScope.topProvince) : '—'}</p>
                        <p class="sm-logistics-label">Khu vực giao nhiều nhất</p>
                    </div>
                    <div class="sm-logistics-card">
                        <div class="sm-logistics-icon-wrap sm-stat-icon-green">
                            <i class="fas fa-city"></i>
                        </div>
                        <p class="sm-logistics-value" id="stat-top-province-name">${not empty requestScope.topProvinceName ? fn:escapeXml(requestScope.topProvinceName) : '—'}</p>
                        <p class="sm-logistics-label">Tỉnh có nhiều đơn nhất</p>
                    </div>
                    <div class="sm-logistics-card">
                        <div class="sm-logistics-icon-wrap sm-stat-icon-orange">
                            <i class="fas fa-clock"></i>
                        </div>
                        <p class="sm-logistics-value">${requestScope.avgDeliveryDays != null ? requestScope.avgDeliveryDays : '—'}</p>
                        <p class="sm-logistics-label">Thời gian giao TB (ngày)</p>
                    </div>
                    <div class="sm-logistics-card">
                        <div class="sm-logistics-icon-wrap sm-stat-icon-pink">
                            <i class="fas fa-triangle-exclamation"></i>
                        </div>
                        <p class="sm-logistics-value">${not empty requestScope.slowestRegion ? fn:escapeXml(requestScope.slowestRegion) : '—'}</p>
                        <p class="sm-logistics-label">Khu vực giao chậm nhất</p>
                    </div>
                    <div class="sm-logistics-card">
                        <div class="sm-logistics-icon-wrap sm-stat-icon-teal">
                            <i class="fas fa-percent"></i>
                        </div>
                        <p class="sm-logistics-value">
                            <fmt:formatNumber pattern="#0.0" value="${requestScope.deliverySuccessRate != null ? requestScope.deliverySuccessRate : 0}" />%
                        </p>
                        <p class="sm-logistics-label">Tỷ lệ giao thành công</p>
                    </div>
                    <div class="sm-logistics-card">
                        <div class="sm-logistics-icon-wrap sm-stat-icon-purple">
                            <i class="fas fa-rotate-left"></i>
                        </div>
                        <p class="sm-logistics-value">
                            <fmt:formatNumber pattern="#0.0" value="${requestScope.returnRate != null ? requestScope.returnRate : 0}" />%
                        </p>
                        <p class="sm-logistics-label">Tỷ lệ hoàn hàng</p>
                    </div>
                </div>

                <!-- Activity Timeline -->
                <div class="sm-activity-section">
                    <h4 class="sm-section-title"><i class="fas fa-clock-rotate-left"></i> Hoạt động vận chuyển gần đây</h4>
                    <div class="sm-activity-timeline" id="stats-activity-timeline">
                        <c:choose>
                            <c:when test="${not empty requestScope.recentActivities}">
                                <c:forEach var="act" items="${requestScope.recentActivities}" varStatus="i">
                                    <div class="sm-activity-item">
                                        <div class="sm-activity-dot sm-activity-dot-${act.dotColor}">
                                            <i class="fas ${act.icon}"></i>
                                        </div>
                                        <div class="sm-activity-content">
                                            <p class="sm-activity-title">${fn:escapeXml(act.title)}</p>
                                            <p class="sm-activity-desc">${fn:escapeXml(act.description)}</p>
                                            <span class="sm-activity-time">${fn:escapeXml(act.timestamp)}</span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div style="text-align:center;padding:32px 20px;">
                                    <div style="width:56px;height:56px;background:#F4F5F7;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:24px;color:#D1D5DB;margin:0 auto 12px;">
                                        <i class="fas fa-clock-rotate-left"></i>
                                    </div>
                                    <p style="font-size:14px;font-weight:600;color:#374151;margin:0 0 4px;">Chưa có hoạt động gần đây</p>
                                    <p style="font-size:13px;color:#6B7280;margin:0;">Các hoạt động vận chuyển sẽ hiển thị tại đây</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../_footerAdmin.jsp" />

<div class="sm-modal-overlay" id="modal-add-method">
    <div class="sm-modal sm-modal-lg">
        <div class="sm-modal-header">
            <h5><i class="fas fa-plus-circle"></i> Thêm phương thức vận chuyển</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-add-method').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingMethod">
            <input type="hidden" name="action" value="create">
            <div class="sm-modal-body">
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Tên phương thức <span>*</span></label>
                        <input type="text" name="name" class="sm-form-control" placeholder="VD: Giao hàng nhanh GHN" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Nhà cung cấp <span>*</span></label>
                        <select name="providerType" class="sm-form-control" required>
                            <option value="GHN">GHN Express</option>
                            <option value="GHTK">Giao hàng tiết kiệm</option>
                            <option value="INTERNAL">Nội bộ</option>
                        </select>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Thời gian giao (ngày) <span>*</span></label>
                        <input type="number" name="estimatedDays" class="sm-form-control" value="3" min="1" max="30" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Phí / kg (VNĐ) <span>*</span></label>
                        <input type="number" name="pricePerKg" class="sm-form-control" value="20000" min="0" step="1000" required>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>GHN Service ID</label>
                        <input type="number" name="ghnServiceId" class="sm-form-control" value="2">
                    </div>
                    <div class="sm-form-group">
                        <label>GHN From District ID</label>
                        <input type="text" name="ghnFromDistrictId" class="sm-form-control" placeholder="VD: 1567">
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Số điện thoại hỗ trợ</label>
                        <input type="text" name="supportPhone" class="sm-form-control" placeholder="VD: 1900 6365">
                    </div>
                    <div class="sm-form-group">
                        <label>Email hỗ trợ</label>
                        <input type="email" name="supportEmail" class="sm-form-control" placeholder="VD: cskh@ghn.vn">
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Giao nhanh (Express)?</label>
                        <select name="isExpress" class="sm-form-control">
                            <option value="false">Không - Tiêu chuẩn</option>
                            <option value="true">Có - Giao nhanh</option>
                        </select>
                    </div>
                    <div class="sm-form-group">
                        <label>Phí surcharged (x1.x)</label>
                        <input type="number" name="expressSurcharge" class="sm-form-control" value="1.5" min="1" step="0.1">
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Miễn phí vận chuyển từ (VNĐ)</label>
                        <input type="number" name="freeShippingThreshold" class="sm-form-control" value="0" min="0" step="10000" placeholder="0 = không miễn phí">
                    </div>
                    <div class="sm-form-group">
                        <label>GHN Token</label>
                        <input type="text" name="ghnToken" class="sm-form-control" placeholder="Token GHN API">
                    </div>
                </div>
            </div>
            <div class="sm-modal-footer">
                <button type="button" class="sm-btn sm-btn-outline sm-modal-close"
                    onclick="document.getElementById('modal-add-method').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-primary"><i class="fas fa-save"></i> Lưu phương thức</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-edit-method">
    <div class="sm-modal sm-modal-lg">
        <div class="sm-modal-header">
            <h5><i class="fas fa-edit"></i> Sửa phương thức vận chuyển</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-edit-method').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingMethod">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" id="sm-edit-id">
            <div class="sm-modal-body">
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Tên phương thức <span>*</span></label>
                        <input type="text" name="name" id="sm-edit-name" class="sm-form-control" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Nhà cung cấp <span>*</span></label>
                        <select name="providerType" id="sm-edit-provider" class="sm-form-control" required>
                            <option value="GHN">GHN Express</option>
                            <option value="GHTK">Giao hàng tiết kiệm</option>
                            <option value="INTERNAL">Nội bộ</option>
                        </select>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Thời gian giao (ngày) <span>*</span></label>
                        <input type="number" name="estimatedDays" id="sm-edit-days" class="sm-form-control" min="1" max="30" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Phí / kg (VNĐ) <span>*</span></label>
                        <input type="number" name="pricePerKg" id="sm-edit-price" class="sm-form-control" min="0" step="1000" required>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>GHN Service ID</label>
                        <input type="number" name="ghnServiceId" id="sm-edit-ghn-id" class="sm-form-control">
                    </div>
                    <div class="sm-form-group">
                        <label>Giao nhanh?</label>
                        <select name="isExpress" id="sm-edit-express" class="sm-form-control">
                            <option value="false">Tiêu chuẩn</option>
                            <option value="true">Giao nhanh</option>
                        </select>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Phí surcharge (x1.x)</label>
                        <input type="number" name="expressSurcharge" id="sm-edit-surcharge" class="sm-form-control" min="1" step="0.1">
                    </div>
                    <div class="sm-form-group">
                        <label>Miễn phí từ (VNĐ)</label>
                        <input type="number" name="freeShippingThreshold" id="sm-edit-threshold" class="sm-form-control" min="0" step="10000">
                    </div>
                </div>
                <div class="sm-form-group">
                    <label>Số điện thoại hỗ trợ</label>
                    <input type="text" name="supportPhone" id="sm-edit-phone" class="sm-form-control">
                </div>
            </div>
            <div class="sm-modal-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="document.getElementById('modal-edit-method').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-primary"><i class="fas fa-save"></i> Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-delete-method">
    <div class="sm-modal">
        <div class="sm-modal-header">
            <h5><i class="fas fa-trash" style="color:#EF4444"></i> Xóa phương thức</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-delete-method').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingMethod">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" id="sm-del-method-id">
            <div class="sm-modal-body">
                <div class="sm-confirm-icon"><i class="fas fa-exclamation-triangle"></i></div>
                <p class="sm-confirm-text">Bạn có chắc muốn xóa phương thức<br><span class="sm-confirm-name" id="sm-del-method-name"></span>?</p>
                <p class="text-center text-muted" style="font-size:13px">Hành động này không thể hoàn tác.</p>
            </div>
            <div class="sm-confirm-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="document.getElementById('modal-delete-method').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-danger"><i class="fas fa-trash"></i> Xóa</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-edit-zone">
    <div class="sm-modal">
        <div class="sm-modal-header">
            <h5><i class="fas fa-edit"></i> Sửa khu vực giao hàng</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-edit-zone').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingZone">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" id="sm-edit-zone-id">
            <div class="sm-modal-body">
                <div class="sm-form-group">
                    <label>Tên khu vực <span>*</span></label>
                    <input type="text" name="zoneName" id="sm-edit-zone-name" class="sm-form-control" required>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Loại vùng <span>*</span></label>
                        <select name="zoneType" id="sm-edit-zone-type" class="sm-form-control" required>
                            <option value="INNER">Nội thành</option>
                            <option value="PROVINCIAL">Tỉnh lẻ</option>
                            <option value="REMOTE">Vùng xa</option>
                        </select>
                    </div>
                    <div class="sm-form-group">
                        <label>Trạng thái</label>
                        <select name="status" id="sm-edit-zone-status" class="sm-form-control">
                            <option value="1">Hoạt động</option>
                            <option value="0">Tạm ngừng</option>
                        </select>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Phí cơ bản (VNĐ) <span>*</span></label>
                        <input type="number" name="baseFee" id="sm-edit-zone-base-fee" class="sm-form-control" min="0" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Phí / kg (VNĐ) <span>*</span></label>
                        <input type="number" name="pricePerKg" id="sm-edit-zone-price-kg" class="sm-form-control" min="0" required>
                    </div>
                </div>
                <div class="sm-form-group">
                    <label>Mô tả</label>
                    <input type="text" name="description" id="sm-edit-zone-desc" class="sm-form-control">
                </div>
            </div>
            <div class="sm-modal-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="document.getElementById('modal-edit-zone').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-primary"><i class="fas fa-save"></i> Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-delete-zone">
    <div class="sm-modal">
        <div class="sm-modal-header">
            <h5><i class="fas fa-trash" style="color:#EF4444"></i> Xóa khu vực</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-delete-zone').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingZone">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" id="sm-del-zone-id">
            <div class="sm-modal-body">
                <div class="sm-confirm-icon"><i class="fas fa-exclamation-triangle"></i></div>
                <p class="sm-confirm-text">Bạn có chắc muốn xóa khu vực<br><span class="sm-confirm-name" id="sm-del-zone-name"></span>?</p>
                <p class="text-center text-muted" style="font-size:13px">Hành động này không thể hoàn tác.</p>
            </div>
            <div class="sm-confirm-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="document.getElementById('modal-delete-zone').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-danger"><i class="fas fa-trash"></i> Xóa</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-add-weight-fee">
    <div class="sm-modal">
        <div class="sm-modal-header">
            <h5><i class="fas fa-plus-circle" style="color:#10B981"></i> Thêm biểu phí trọng lượng</h5>
            <button type="button" class="sm-modal-close" onclick="smCloseAddWeightFee()">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingWeightFee" id="form-add-weight-fee" novalidate>
            <input type="hidden" name="action" value="create">
            <div class="sm-modal-body">
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Phương thức vận chuyển <span>*</span></label>
                        <select name="shippingMethodId" id="add-fee-method" class="sm-form-control" required>
                            <option value="">-- Chọn phương thức --</option>
                            <c:forEach var="m" items="${requestScope.shippingMethods}">
                                <option value="${m.id}">${fn:escapeXml(m.name)}</option>
                            </c:forEach>
                        </select>
                        <div class="sm-field-error" id="add-fee-method-error"></div>
                    </div>
                    <div class="sm-form-group">
                        <label>Khu vực <span>*</span></label>
                        <select name="zoneType" id="add-fee-zone" class="sm-form-control" required>
                            <option value="">-- Chọn khu vực --</option>
                            <option value="INNER">Nội thành</option>
                            <option value="PROVINCIAL">Tỉnh lẻ</option>
                            <option value="REMOTE">Vùng xa</option>
                        </select>
                        <div class="sm-field-error" id="add-fee-zone-error"></div>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Trọng lượng tối thiểu (kg) <span>*</span></label>
                        <input type="number" name="minWeight" id="add-fee-min-weight" class="sm-form-control"
                            min="0" step="0.5" placeholder="VD: 0" required>
                        <div class="sm-field-error" id="add-fee-min-weight-error"></div>
                    </div>
                    <div class="sm-form-group">
                        <label>Trọng lượng tối đa (kg) <span>*</span></label>
                        <input type="number" name="maxWeight" id="add-fee-max-weight" class="sm-form-control"
                            min="0" step="0.5" placeholder="VD: 5" required>
                        <div class="sm-field-error" id="add-fee-max-weight-error"></div>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Phí cơ bản (VNĐ) <span>*</span></label>
                        <input type="number" name="baseFee" id="add-fee-base-fee" class="sm-form-control"
                            min="0" step="1000" placeholder="VD: 15000" required>
                        <div class="sm-field-error" id="add-fee-base-fee-error"></div>
                    </div>
                    <div class="sm-form-group">
                        <label>Phí vượt / kg (VNĐ) <span>*</span></label>
                        <input type="number" name="feePerKg" id="add-fee-per-kg" class="sm-form-control"
                            min="0" step="1000" placeholder="VD: 3000" required>
                        <div class="sm-field-error" id="add-fee-per-kg-error"></div>
                    </div>
                </div>
                <div class="sm-fee-preview" id="add-fee-preview">
                    <i class="fas fa-info-circle"></i>
                    <span>Xem trước: Biểu phí cho khoảng <strong id="preview-range">—</strong> kg</span>
                </div>
            </div>
            <div class="sm-modal-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="smCloseAddWeightFee()">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-primary"><i class="fas fa-save"></i> Lưu biểu phí</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-edit-weight-fee">
    <div class="sm-modal">
        <div class="sm-modal-header">
            <h5><i class="fas fa-edit"></i> Sửa biểu phí trọng lượng</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-edit-weight-fee').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingWeightFee">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" id="sm-edit-fee-id">
            <div class="sm-modal-body">
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Phương thức <span>*</span></label>
                        <select name="shippingMethodId" id="sm-edit-fee-method" class="sm-form-control" required>
                            <c:forEach var="m" items="${requestScope.shippingMethods}">
                                <option value="${m.id}">${fn:escapeXml(m.name)}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="sm-form-group">
                        <label>Khu vực <span>*</span></label>
                        <select name="zoneType" id="sm-edit-fee-zone" class="sm-form-control" required>
                            <option value="INNER">Nội thành</option>
                            <option value="PROVINCIAL">Tỉnh lẻ</option>
                            <option value="REMOTE">Vùng xa</option>
                        </select>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Trọng lượng tối thiểu (kg) <span>*</span></label>
                        <input type="number" name="minWeight" id="sm-edit-fee-min-w" class="sm-form-control" min="0" step="0.5" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Trọng lượng tối đa (kg) <span>*</span></label>
                        <input type="number" name="maxWeight" id="sm-edit-fee-max-w" class="sm-form-control" min="0" step="0.5" required>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Phí cơ bản (VNĐ) <span>*</span></label>
                        <input type="number" name="baseFee" id="sm-edit-fee-base" class="sm-form-control" min="0" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Phí vượt kg (VNĐ) <span>*</span></label>
                        <input type="number" name="feePerKg" id="sm-edit-fee-perkg" class="sm-form-control" min="0" required>
                    </div>
                </div>
            </div>
            <div class="sm-modal-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="document.getElementById('modal-edit-weight-fee').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-primary"><i class="fas fa-save"></i> Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-delete-weight-fee">
    <div class="sm-modal">
        <div class="sm-modal-header">
            <h5><i class="fas fa-trash" style="color:#EF4444"></i> Xóa biểu phí</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-delete-weight-fee').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/shippingWeightFee">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" id="sm-del-fee-id">
            <div class="sm-modal-body">
                <div class="sm-confirm-icon"><i class="fas fa-exclamation-triangle"></i></div>
                <p class="sm-confirm-text">Bạn có chắc muốn xóa biểu phí<br><span class="sm-confirm-name" id="sm-del-fee-name"></span>?</p>
                <p class="text-center text-muted" style="font-size:13px">Hành động này không thể hoàn tác.</p>
            </div>
            <div class="sm-confirm-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="document.getElementById('modal-delete-weight-fee').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-danger"><i class="fas fa-trash"></i> Xóa</button>
            </div>
        </form>
    </div>
</div>

<div class="sm-modal-overlay" id="modal-edit-province">
    <div class="sm-modal">
        <div class="sm-modal-header">
            <h5><i class="fas fa-edit"></i> Sửa tỉnh / thành phố</h5>
            <button type="button" class="sm-modal-close" onclick="document.getElementById('modal-edit-province').classList.remove('show')">
                <i class="fas fa-times"></i>
            </button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/admin/province">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" id="sm-edit-province-id">
            <div class="sm-modal-body">
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Mã tỉnh <span>*</span></label>
                        <input type="text" name="provinceCode" id="sm-edit-province-code" class="sm-form-control" required>
                    </div>
                    <div class="sm-form-group">
                        <label>Tên tỉnh / thành <span>*</span></label>
                        <input type="text" name="provinceName" id="sm-edit-province-name" class="sm-form-control" required>
                    </div>
                </div>
                <div class="sm-form-row">
                    <div class="sm-form-group">
                        <label>Loại</label>
                        <select name="provinceType" id="sm-edit-province-type" class="sm-form-control">
                            <option value="tinh">Tỉnh</option>
                            <option value="thanh-pho">Thành phố</option>
                        </select>
                    </div>
                    <div class="sm-form-group">
                        <label>Vùng miền</label>
                        <select name="region" id="sm-edit-province-region" class="sm-form-control">
                            <option value="MienBac">Miền Bắc</option>
                            <option value="MienTrung">Miền Trung</option>
                            <option value="MienNam">Miền Nam</option>
                        </select>
                    </div>
                </div>
                <div class="sm-form-group">
                    <label>Thành phố lớn?</label>
                    <select name="isMetroCity" id="sm-edit-province-metro" class="sm-form-control">
                        <option value="false">Không</option>
                        <option value="true">Có</option>
                    </select>
                </div>
            </div>
            <div class="sm-modal-footer">
                <button type="button" class="sm-btn sm-btn-outline" onclick="document.getElementById('modal-edit-province').classList.remove('show')">Hủy</button>
                <button type="submit" class="sm-btn sm-btn-primary"><i class="fas fa-save"></i> Cập nhật</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/shippingMethodManagerView.js"></script>

<script>
(function() {
    'use strict';

    // ---- ADD WEIGHT FEE ----
    window.smOpenAddWeightFee = function() {
        var form = document.getElementById('form-add-weight-fee');
        if (form) form.reset();
        clearAddErrors();
        var preview = document.getElementById('add-fee-preview');
        if (preview) preview.style.display = 'none';
        var overlay = document.getElementById('modal-add-weight-fee');
        if (overlay) {
            overlay.classList.add('show');
            document.body.style.overflow = 'hidden';
            var firstInput = document.getElementById('add-fee-method');
            if (firstInput) firstInput.focus();
        }
    };

    window.smCloseAddWeightFee = function() {
        var overlay = document.getElementById('modal-add-weight-fee');
        if (overlay) overlay.classList.remove('show');
        document.body.style.overflow = '';
    };

    var _origSmDeleteWeightFee = window.smDeleteWeightFee;
    window.smDeleteWeightFee = function(id) {
        var row = document.querySelector('tr[data-fee-id="' + id + '"]');
        var nameEl = document.getElementById('sm-del-fee-name');
        if (nameEl) {
            nameEl.textContent = '';
            if (row) {
                var method = row.dataset.methodName || '';
                var zone = row.dataset.feezone || '';
                var minW = row.dataset.feeminw || '';
                var maxW = row.dataset.feemaxw || '';
                nameEl.textContent = method + ' | ' + zone + ' | ' + minW + '–' + maxW + ' kg';
            }
        }
        var modal = document.getElementById('modal-delete-weight-fee');
        if (modal) {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        }
    };
    
    function clearAddErrors() {
        document.querySelectorAll('#form-add-weight-fee .sm-field-error').forEach(function(el) {
            el.textContent = '';
            el.style.display = 'none';
        });
        document.querySelectorAll('#form-add-weight-fee .sm-form-control.is-invalid').forEach(function(el) {
            el.classList.remove('is-invalid');
        });
    }

    function showAddError(fieldId, message) {
        var input = document.getElementById(fieldId);
        var errorEl = document.getElementById(fieldId + '-error');
        if (input) input.classList.add('is-invalid');
        if (errorEl) {
            errorEl.textContent = message;
            errorEl.style.display = 'block';
        }
    }

    function validateAddWeightFeeForm() {
        clearAddErrors();
        var hasError = false;

        var method = document.getElementById('add-fee-method');
        var zone = document.getElementById('add-fee-zone');
        var minW = document.getElementById('add-fee-min-weight');
        var maxW = document.getElementById('add-fee-max-weight');
        var baseFee = document.getElementById('add-fee-base-fee');
        var perKg = document.getElementById('add-fee-per-kg');

        if (!method || !method.value) {
            showAddError('add-fee-method', 'Vui lòng chọn phương thức vận chuyển');
            hasError = true;
        }
        if (!zone || !zone.value) {
            showAddError('add-fee-zone', 'Vui lòng chọn khu vực');
            hasError = true;
        }
        if (!minW || isNaN(minW.value) || parseFloat(minW.value) < 0) {
            showAddError('add-fee-min-weight', 'Trọng lượng tối thiểu phải >= 0');
            hasError = true;
        }
        if (!maxW || isNaN(maxW.value) || parseFloat(maxW.value) <= 0) {
            showAddError('add-fee-max-weight', 'Trọng lượng tối đa phải > 0');
            hasError = true;
        }
        if (minW && maxW && !isNaN(minW.value) && !isNaN(maxW.value)) {
            if (parseFloat(maxW.value) <= parseFloat(minW.value)) {
                showAddError('add-fee-max-weight', 'Tối đa phải lớn hơn tối thiểu');
                hasError = true;
            }
        }
        if (!baseFee || isNaN(baseFee.value) || parseFloat(baseFee.value) < 0) {
            showAddError('add-fee-base-fee', 'Phí cơ bản phải >= 0');
            hasError = true;
        }
        if (!perKg || isNaN(perKg.value) || parseFloat(perKg.value) < 0) {
            showAddError('add-fee-per-kg', 'Phí vượt kg phải >= 0');
            hasError = true;
        }
        return !hasError;
    }

    function updatePreview() {
        var minW = document.getElementById('add-fee-min-weight');
        var maxW = document.getElementById('add-fee-max-weight');
        var preview = document.getElementById('add-fee-preview');
        var range = document.getElementById('preview-range');
        if (!preview || !range || !minW || !maxW) return;

        var min = parseFloat(minW.value);
        var max = parseFloat(maxW.value);
        if (!isNaN(min) && !isNaN(max) && max > 0) {
            range.textContent = min + ' – ' + max;
            preview.style.display = 'flex';
        } else {
            preview.style.display = 'none';
        }
    }
    
    var addForm = document.getElementById('form-add-weight-fee');
    if (addForm) {
        addForm.addEventListener('submit', function(e) {
            if (!validateAddWeightFeeForm()) {
                e.preventDefault();
            }
        });
        var previewInputs = ['add-fee-min-weight', 'add-fee-max-weight'];
        previewInputs.forEach(function(id) {
            var el = document.getElementById(id);
            if (el) {
                el.addEventListener('input', updatePreview);
                el.addEventListener('change', updatePreview);
            }
        });
    }

    var addModal = document.getElementById('modal-add-weight-fee');
    if (addModal) {
        addModal.addEventListener('click', function(e) {
            if (e.target === addModal) smCloseAddWeightFee();
        });
    }

})();
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
