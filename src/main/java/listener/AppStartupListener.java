package listener;

import config.PermissionRegistry;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("[System] Khởi tạo danh sách quyền");
		PermissionRegistry.loadPermissions();
		System.out.println("[System] Khởi tạo danh sách quyền thành công.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("[System] Application shutting down.");
	}
}