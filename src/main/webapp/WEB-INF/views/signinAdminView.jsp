<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="_meta.jsp" />
    <title>Đăng nhập Admin</title>
</head>

<body class="bg-light d-flex align-items-center min-vh-100 py-5">
    
    <main class="container">
        <div class="row">
            <div class="col-10 col-sm-8 col-md-6 col-lg-4 mx-auto">
                
                <div class="text-center mb-4">
                    <h3 class="py-3 px-4 bg-primary text-white rounded-3 shadow-sm d-inline-block fw-bold mb-0">
                        <i class="bi bi-book-half me-2"></i>Shop Bán Sách
                    </h3>
                </div>

                <div class="card shadow-sm border-0 rounded-3">
                    <div class="card-body p-4">
                        
                        <c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
                            <div class="alert alert-danger alert-dismissible fade show shadow-sm mb-3 small" role="alert">
                                <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope[ATTR_ERRORS][ERR_GLOBAL]}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>

                        <h4 class="card-title fw-bold text-dark mb-1">
                            <i class="bi bi-shield-lock-fill text-danger me-2"></i>Đăng nhập Admin
                        </h4>
                        <p class="text-muted small mb-4">Chỉ dành cho quản trị viên và nhân viên</p>
                        
                        <form action="${pageContext.request.contextPath}/admin/signin" method="post">
                            
                            <div class="mb-3">
                                <label class="form-label small fw-semibold text-secondary">Tài khoản quản trị</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-white border-end-0"><i class="bi bi-person-badge text-muted"></i></span>
                                    <input name="${P_USERNAME}"
                                           class="form-control border-start-0 ps-0 ${not empty requestScope[ATTR_ERRORS][P_USERNAME] ? 'is-invalid' : (not empty requestScope[ATTR_VALUES][P_USERNAME] ? 'is-valid' : '')}"
                                           placeholder="Nhập tên đăng nhập" type="text" autocomplete="off"
                                           value="${requestScope[ATTR_VALUES][P_USERNAME]}">
                                    
                                    <c:if test="${not empty requestScope[ATTR_ERRORS][P_USERNAME]}">
                                        <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_USERNAME]}</div>
                                    </c:if>
                                </div>
                            </div>
                            
                            <div class="mb-4">
                                <label class="form-label small fw-semibold text-secondary">Mật khẩu</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-white border-end-0"><i class="bi bi-key text-muted"></i></span>
                                    <input name="${P_PASSWORD}"
                                           class="form-control border-start-0 ps-0 ${not empty requestScope[ATTR_ERRORS][P_PASSWORD] ? 'is-invalid' : ''}"
                                           placeholder="Nhập mật khẩu" type="password" autocomplete="off"
                                           value="">
                                    
                                    <c:if test="${not empty requestScope[ATTR_ERRORS][P_PASSWORD]}">
                                        <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_PASSWORD]}</div>
                                    </c:if>
                                </div>
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm">
                                <i class="bi bi-box-arrow-in-right me-1"></i> Vào trang quản trị
                            </button>
                        </form>

                        <div class="text-center mt-4 pt-2 border-top">
                            <a href="${pageContext.request.contextPath}/signin" class="btn btn-link btn-sm text-decoration-none text-secondary">
                                <i class="bi bi-arrow-left-short"></i> Tôi không phải là quản trị viên
                            </a>
                        </div>
                        
                    </div>
                </div>
                
            </div>
        </div>
    </main>

</body>
</html>
