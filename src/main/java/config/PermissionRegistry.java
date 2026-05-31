package config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constants.PermissionConstants;

public class PermissionRegistry {

	private static volatile Map<String, Set<String>> permissionMapView = Collections.emptyMap();
	private static volatile List<String> sortedPathsDescLength = Collections.emptyList();

	static {
		loadPermissions();
	}

	public static void loadPermissions() {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();

		// User Management
		addMapping(map, "/admin/user", PermissionConstants.USER_VIEW);
		addMapping(map, "/admin/user/detail", PermissionConstants.USER_DETAIL);
		addMapping(map, "/admin/user/create", PermissionConstants.USER_CREATE);
		addMapping(map, "/admin/user/update", PermissionConstants.USER_UPDATE);
		addMapping(map, "/admin/user/delete", PermissionConstants.USER_DELETE);

		// Role Management
		addMapping(map, "/admin/role", PermissionConstants.ROLE_VIEW);
		addMapping(map, "/admin/role/create", PermissionConstants.ROLE_CREATE);
		addMapping(map, "/admin/role/update", PermissionConstants.ROLE_UPDATE);
		addMapping(map, "/admin/role/delete", PermissionConstants.ROLE_DELETE);
		addMapping(map, "/admin/role/batchAssignPermission", PermissionConstants.ROLE_ASSIGN_PERMISSION);
		addMapping(map, "/admin/role/batchRemovePermission", PermissionConstants.ROLE_ASSIGN_PERMISSION);

		// Permission Management
		addMapping(map, "/admin/permission", PermissionConstants.PERMISSION_VIEW);
		addMapping(map, "/admin/permission/create", PermissionConstants.PERMISSION_CREATE);
		addMapping(map, "/admin/permission/update", PermissionConstants.PERMISSION_UPDATE);
		addMapping(map, "/admin/permission/delete", PermissionConstants.PERMISSION_DELETE);

		// Category Management
		addMapping(map, "/admin/categoryManager/view", PermissionConstants.CATEGORY_VIEW);
		addMapping(map, "/admin/categoryManager/detail", PermissionConstants.CATEGORY_VIEW);
		addMapping(map, "/admin/categoryManager/create", PermissionConstants.CATEGORY_CREATE);
		addMapping(map, "/admin/categoryManager/update", PermissionConstants.CATEGORY_UPDATE);
		addMapping(map, "/admin/categoryManager/delete", PermissionConstants.CATEGORY_DELETE);

		// Product Management
		addMapping(map, "/admin/productManager/view", PermissionConstants.PRODUCT_VIEW);
		addMapping(map, "/admin/productManager/detail", PermissionConstants.PRODUCT_VIEW);
		addMapping(map, "/admin/productManager/create", PermissionConstants.PRODUCT_CREATE);
		addMapping(map, "/admin/productManager/update", PermissionConstants.PRODUCT_UPDATE);
		addMapping(map, "/admin/productManager/delete", PermissionConstants.PRODUCT_DELETE);

		// Review Management
		addMapping(map, "/admin/reviewManager/view", PermissionConstants.REVIEW_VIEW);
		addMapping(map, "/admin/reviewManager/detail", PermissionConstants.REVIEW_VIEW);
		addMapping(map, "/admin/reviewManager/update", PermissionConstants.REVIEW_MODERATE);

		// Order Management
		addMapping(map, "/admin/orderManager/view", PermissionConstants.ORDER_VIEW);
		addMapping(map, "/admin/orderManager/detail", PermissionConstants.ORDER_VIEW);
		addMapping(map, "/admin/orderManager/update", PermissionConstants.ORDER_UPDATE);
		addMapping(map, "/admin/orderManager/delete", PermissionConstants.ORDER_DELETE);

		// Voucher Management
		addMapping(map, "/admin/voucherManager/view", PermissionConstants.VOUCHER_VIEW);
		addMapping(map, "/admin/voucherManager/create", PermissionConstants.VOUCHER_CREATE);
		addMapping(map, "/admin/voucherManager/update", PermissionConstants.VOUCHER_UPDATE);
		addMapping(map, "/admin/voucherManager/delete", PermissionConstants.VOUCHER_DELETE);

		// Shipment & Config
		addMapping(map, "/admin/shipmentManager", PermissionConstants.SHIPMENT_VIEW);
		addMapping(map, "/admin/shipmentManager/detail", PermissionConstants.SHIPMENT_VIEW);
		addMapping(map, "/admin/shippingMethod", PermissionConstants.SHIPPING_CONFIG_VIEW);
		addMapping(map, "/admin/shippingZone", PermissionConstants.SHIPPING_CONFIG_UPDATE);
		addMapping(map, "/admin/shippingWeightFee", PermissionConstants.SHIPPING_CONFIG_UPDATE);

		permissionMapView = prepareReadOnlyMap(map);
		rebuildSortedPaths(permissionMapView);
	}

	private static void addMapping(final Map<String, Set<String>> map, final String path, final String permission) {
		Set<String> permissions = map.get(path);
		
		if (permissions == null) {
			permissions = new HashSet<String>();
			map.put(path, permissions);
		}
		
		permissions.add(permission);
	}

	private static void rebuildSortedPaths(final Map<String, Set<String>> map) {
		List<String> keys = new ArrayList<String>(map.keySet());
		
		Collections.sort(keys, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return s2.length() - s1.length();
			}
		});
		
		sortedPathsDescLength = Collections.unmodifiableList(keys);
	}

	public static Map<String, Set<String>> getPermissionMap() {
		return permissionMapView;
	}

	public static List<String> getSortedPathsDescLength() {
		return sortedPathsDescLength;
	}

	private static Map<String, Set<String>> prepareReadOnlyMap(final Map<String, Set<String>> map) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();

		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			String key = entry.getKey();
			Set<String> value = entry.getValue();

			if (key != null && value != null) {
				result.put(key, Collections.unmodifiableSet(new HashSet<String>(value)));
			}
		}
		
		return Collections.unmodifiableMap(result);
	}
}
