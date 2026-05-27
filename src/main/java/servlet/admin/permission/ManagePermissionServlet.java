package servlet.admin.permission;

import constants.ViewAttributeConstants;
import context.UserPermissionContext;
import exception.BusinessException;
import helpers.MessageHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import service.PermissionService;
import service.PermissionServiceImpl;
import dto.permission.ManagePermissionResponse;

@WebServlet(name = "ManagePermissionServlet", urlPatterns = "/admin/permission")
public class ManagePermissionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final PermissionService permissionService = new PermissionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ManagePermissionResponse> permissions;
        try {
            permissions = permissionService.getAllPermissions();
        } catch (BusinessException e) {
            permissions = new java.util.ArrayList<>();
            MessageHelper.setErrorMessage(request.getSession(), e.getMessage());
        }
        Map<String, List<ManagePermissionResponse>> permissionsByModule = groupByModule(permissions);
        
        UserPermissionContext securityContext = (UserPermissionContext) request
                .getAttribute(ViewAttributeConstants.Security.SECURITY_CONTEXT);
        if (securityContext != null) {
            request.setAttribute(ViewAttributeConstants.Permission.HAS_CREATE, securityContext.isCanCreatePermission());
            request.setAttribute(ViewAttributeConstants.Permission.HAS_EDIT, securityContext.isCanEditPermission());
            request.setAttribute(ViewAttributeConstants.Permission.HAS_DELETE, securityContext.isCanDeletePermission());
        }

        request.setAttribute(ViewAttributeConstants.Permission.PERMISSIONS_BY_MODULE, permissionsByModule);
        request.getRequestDispatcher("/WEB-INF/views/permissionManagerView.jsp").forward(request, response);
    }

    private Map<String, List<ManagePermissionResponse>> groupByModule(List<ManagePermissionResponse> permissions) {
        Map<String, List<ManagePermissionResponse>> result = new TreeMap<>();
        for (ManagePermissionResponse p : permissions) {
            String module = p.getModule() != null ? p.getModule() : "OTHER";
            result.computeIfAbsent(module, k -> new java.util.ArrayList<>()).add(p);
        }
        return result;
    }

}
