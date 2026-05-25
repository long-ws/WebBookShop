package servlet.general;

import java.time.LocalDateTime;
import java.util.List;

import beans.ShippingMethod;
import dao.ShippingMethodDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();

		if (context.getInitParameter("ghnApiUrl") == null) {
			context.setInitParameter("ghnApiUrl", "https://online-gateway.ghn.vn/shiip/public-api");
		}
		if (context.getInitParameter("ghnToken") == null) {
			context.setInitParameter("ghnToken", "2b83af86-1f41-11f0-bc69-eaki11d5a87e");
		}
		if (context.getInitParameter("ghnShopId") == null) {
			context.setInitParameter("ghnShopId", "191493");
		}
		if (context.getInitParameter("ghnFromDistrictId") == null) {
			context.setInitParameter("ghnFromDistrictId", "1542");
		}
		if (context.getInitParameter("ghnFromWardCode") == null) {
			context.setInitParameter("ghnFromWardCode", "20916");
		}

		try {
			ShippingMethodDAO methodDAO = new ShippingMethodDAO();
			List<ShippingMethod> methods = methodDAO.getAllActive();

			if (methods == null || methods.isEmpty()) {
				insertDefaultShippingMethods(methodDAO);
				System.out.println("[AppStartup] Da chen cac phuong thuc van chuyen mac dinh.");
			} else {
				System.out.println("[AppStartup] Tim thay " + methods.size() + " phuong thuc van chuyen.");
			}
		} catch (Exception e) {
			System.err.println("[AppStartup] Loi khi kiem tra phuong thuc van chuyen: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void insertDefaultShippingMethods(ShippingMethodDAO methodDAO) {
		try {
			ShippingMethod standard = new ShippingMethod();
			standard.setName("Giao hang chuan");
			standard.setProviderType("STANDARD");
			standard.setEstimatedDays(5);
			standard.setPricePerKg(15000.0);
			standard.setStatus(1);
			standard.setExpress(false);
			standard.setExpressSurcharge(1.0);
			standard.setMinWeightKg(0.5);
			standard.setMaxWeightKg(50.0);
			standard.setFreeShippingThreshold(500000.0);
			standard.setGhnServiceId(2);
			standard.setGhnFromDistrictId("1542");
			standard.setGhnFromWardCode("20916");
			standard.setSupportPhone("0901234567");
			standard.setSupportEmail("support@webbookshop.com");
			standard.setCreatedAt(LocalDateTime.now());
			standard.setUpdatedAt(LocalDateTime.now());
			methodDAO.insert(standard);

			ShippingMethod express = new ShippingMethod();
			express.setName("Giao hang nhanh");
			express.setProviderType("EXPRESS");
			express.setEstimatedDays(2);
			express.setPricePerKg(25000.0);
			express.setStatus(1);
			express.setExpress(true);
			express.setExpressSurcharge(1.5);
			express.setMinWeightKg(0.5);
			express.setMaxWeightKg(30.0);
			express.setFreeShippingThreshold(0);
			express.setGhnServiceId(1);
			express.setGhnFromDistrictId("1542");
			express.setGhnFromWardCode("20916");
			express.setSupportPhone("0901234567");
			express.setSupportEmail("support@webbookshop.com");
			express.setCreatedAt(LocalDateTime.now());
			express.setUpdatedAt(LocalDateTime.now());
			methodDAO.insert(express);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("[AppShutdown] Ung dung da dong.");
	}
}
