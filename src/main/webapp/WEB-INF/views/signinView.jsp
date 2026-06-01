<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="_paramKeys.jsp" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <jsp:include page="_meta.jsp" />
    <title>Đăng nhập hệ thống</title>
</head>

<body class="d-flex flex-column min-vh-100 bg-light">
    <jsp:include page="_header.jsp" />
    
    <main class="flex-fill d-flex align-items-center py-5">
        <section class="container">
            <div class="row">
                <div class="col-10 col-sm-8 col-md-6 col-lg-4 mx-auto">
                    
                    <c:if test="${not empty requestScope[ATTR_ERRORS][ERR_GLOBAL]}">
                        <div class="alert alert-danger alert-dismissible fade show shadow-sm mb-3" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>${requestScope[ATTR_ERRORS][ERR_GLOBAL]}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty sessionScope[SSN_OAUTH_ERROR]}">
                        <div class="alert alert-danger alert-dismissible fade show shadow-sm mb-3" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>${sessionScope[SSN_OAUTH_ERROR]}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                        <c:remove var="oauthError" scope="session" />
                    </c:if>

                    <div class="card shadow-sm border-0 rounded-3">
                        <div class="card-body p-4">
                            
                            <h4 class="card-title fw-bold text-center mb-4 text-dark">Đăng nhập</h4>
                            
                            <form action="${pageContext.request.contextPath}/signin" method="post">
                                
                                <div class="mb-3">
                                    <label class="form-label small fw-semibold text-secondary">Tên đăng nhập</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0"><i class="bi bi-person text-muted"></i></span>
                                        <input name="${P_USERNAME}"
                                               class="form-control border-start-0 ps-0 ${not empty requestScope[ATTR_ERRORS][P_USERNAME] ? 'is-invalid' : (not empty requestScope[ATTR_VALUES][P_USERNAME] ? 'is-valid' : '')}"
                                               placeholder="Nhập tài khoản" type="text" autocomplete="off"
                                               value="${requestScope[ATTR_VALUES][P_USERNAME]}">
                                        
                                        <c:if test="${not empty requestScope[ATTR_ERRORS][P_USERNAME]}">
                                            <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_USERNAME]}</div>
                                        </c:if>
                                    </div>
                                </div>
                                
                                <div class="mb-4">
                                    <label class="form-label small fw-semibold text-secondary">Mật khẩu</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-white border-end-0"><i class="bi bi-lock text-muted"></i></span>
                                        <input name="${P_PASSWORD}"
                                               class="form-control border-start-0 ps-0 ${not empty requestScope[ATTR_ERRORS][P_PASSWORD] ? 'is-invalid' : (not empty requestScope[ATTR_VALUES][P_PASSWORD] ? 'is-valid' : '')}"
                                               placeholder="Nhập mật khẩu" type="password" autocomplete="off"
                                               value="${requestScope[ATTR_VALUES][P_PASSWORD]}">
                                        
                                        <c:if test="${not empty requestScope[ATTR_ERRORS][P_PASSWORD]}">
                                            <div class="invalid-feedback">${requestScope[ATTR_ERRORS][P_PASSWORD]}</div>
                                        </c:if>
                                    </div>
                                </div>
                                
                                <button type="submit" class="btn btn-primary w-100 py-2 shadow-sm fw-semibold">
                                    <i class="bi bi-box-arrow-in-right me-1"></i> Đăng nhập
                                </button>
                            </form>

                            <div class="position-relative text-center my-4">
                                <hr class="text-muted">
                                <span class="position-absolute top-50 start-50 translate-middle bg-white px-3 text-muted small">Hoặc</span>
                            </div>

                            <div class="mb-2">
                                <a href="${pageContext.request.contextPath}/oauth-login?provider=google"
                                   class="btn btn-outline-danger w-100 py-2 d-flex align-items-center justify-content-center fw-semibold">
                                    <i class="bi bi-google me-2"></i> Tiếp tục với Google
                                </a>
                            </div>

                            <div class="text-center mt-3 pt-2 border-top">
                                <a href="${pageContext.request.contextPath}/admin/signin" class="btn btn-link btn-sm text-decoration-none text-muted">
                                    <i class="bi bi-code-slash me-1"></i> Chế độ cho nhà phát triển
                                </a>
                            </div>

                        </div>
                    </div>
                    
                    <p class="text-center mt-4 text-secondary">
                        Bạn chưa có tài khoản? 
                        <a href="${pageContext.request.contextPath}/signup" class="text-primary fw-semibold text-decoration-none">Đăng ký ngay</a>
                    </p>
                    
                </div>
            </div>
        </section>
    </main>
    
    <jsp:include page="_footer.jsp" />
</body>

</html>