package servlet.client;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Cart;
import beans.CartItem;
import beans.Product;
import beans.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.CartItemService;
import service.CartService;
import service.ProductService;
import utils.CartUtils;

@WebServlet(name = "CartItemServlet", value = "/cartItem")
public class CartItemServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private final CartService cartService = new CartService();
    private final CartItemService cartItemService = new CartItemService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Thieu tham so action\"}");
            return;
        }

        action = action.trim();

        try {
            switch (action) {
                case "add" -> handleAdd(request, response);
                case "update" -> handleUpdate(request, response);
                case "bulkUpdate" -> handleBulkUpdate(request, response);
                case "delete" -> handleDelete(request, response);
                case "clear" -> handleClear(request, response);
                case "select" -> handleSelect(request, response);
                case "deselect" -> handleDeselect(request, response);
                case "selectAll" -> handleSelectAll(request, response);
                case "deselectAll" -> handleDeselectAll(request, response);
                default -> sendErrorResponse(response, "Invalid action");
            }
        } catch (Exception e) {
            System.err.println("CartItemServlet: Exception - " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Co loi xay ra, vui long thu lai!");
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("calculatePrice".equals(action)) {
            handleCalculatePrice(request, response);
            return;
        }

        if ("getSelectedCount".equals(action)) {
            handleGetSelectedCount(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        try {
            String productIdParam = request.getParameter("productId");
            String quantityParam = request.getParameter("quantity");

            if (productIdParam == null || quantityParam == null ||
                productIdParam.trim().isEmpty() || quantityParam.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Thieu thong tin san pham");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            long productId = Long.parseLong(productIdParam.trim());
            int quantity = Integer.parseInt(quantityParam.trim());

            if (quantity < 1) {
                session.setAttribute("errorMessage", "So luong phai lon hon 0");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            Product product = productService.getById(productId);
            if (product == null) {
                session.setAttribute("errorMessage", "San pham khong ton tai");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            if (product.getQuantity() < quantity) {
                session.setAttribute("errorMessage", "San pham khong du hang trong kho");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            if (CartUtils.isGuestUser(session)) {
                CartItem item = new CartItem(0L, 0L, productId, quantity, true, LocalDateTime.now(), null);
                item.setProduct(product);
                CartUtils.addToGuestCart(session, item);
            } else {
                User user = (User) session.getAttribute("currentUser");
                if (user == null) {
                    session.setAttribute("errorMessage", "Ban chua dang nhap");
                    response.sendRedirect(request.getContextPath() + "/signin");
                    return;
                }

                Cart cart = cartService.getByUserId(user.getId());
                long cartId;

                if (cart == null) {
                    cartId = cartService.insert(new Cart(0L, user.getId(), LocalDateTime.now(), null));
                } else {
                    cartId = cart.getId();
                }

                CartItem existingItem = cartItemService.getByCartIdAndProductId(cartId, productId);

                if (existingItem != null) {
                    int newQuantity = existingItem.getQuantity() + quantity;
                    if (product.getQuantity() < newQuantity) {
                        session.setAttribute("errorMessage", "San pham khong du hang trong kho");
                        response.sendRedirect(request.getContextPath() + "/cart");
                        return;
                    }
                    existingItem.setQuantity(newQuantity);
                    existingItem.setUpdatedAt(LocalDateTime.now());
                    cartItemService.update(existingItem);
                } else {
                    cartItemService.insert(new CartItem(0L, cartId, productId, quantity, true, LocalDateTime.now(), null));
                }

                CartUtils.updateCartBadgeFromDb(user.getId(), session);
            }

            session.setAttribute("successMessage", "Da them san pham vao gio hang");
            String redirectUrl = request.getParameter("redirect");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                response.sendRedirect(request.getContextPath() + redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/cart");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Dinh dang so lieu khong hop le");
            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (Exception e) {
            System.err.println("Error in handleAdd: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Loi he thong, vui long thu lai");
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        try {
            String cartItemIdParam = request.getParameter("cartItemId");
            String quantityParam = request.getParameter("quantity");

            if (cartItemIdParam == null || quantityParam == null ||
                cartItemIdParam.trim().isEmpty() || quantityParam.trim().isEmpty()) {
                sendErrorResponse(response, "Thieu tham so cartItemId hoac quantity");
                return;
            }

            long cartItemId = Long.parseLong(cartItemIdParam.trim());
            int quantity = Integer.parseInt(quantityParam.trim());

            if (quantity < 1) {
                session.setAttribute("errorMessage", "So luong phai lon hon 0");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            if (CartUtils.isGuestUser(session)) {
                List<CartItem> guestCart = CartUtils.getGuestCart(session);
                CartItem targetItem = null;
                for (CartItem item : guestCart) {
                    if (item.getId() == cartItemId || item.getProductId() == cartItemId) {
                        targetItem = item;
                        break;
                    }
                }
                if (targetItem != null) {
                    Product product = productService.getById(targetItem.getProductId());
                    if (product != null && product.getQuantity() < quantity) {
                        session.setAttribute("errorMessage", "San pham khong du hang trong kho");
                        response.sendRedirect(request.getContextPath() + "/cart");
                        return;
                    }
                    targetItem.setQuantity(quantity);
                    targetItem.setUpdatedAt(LocalDateTime.now());
                    session.setAttribute("guestCart", guestCart);
                    CartUtils.updateCartBadge(session);
                }
            } else {
                User user = (User) session.getAttribute("currentUser");
                if (user == null) {
                    sendErrorResponse(response, "Nguoi dung chua dang nhap");
                    return;
                }

                CartItem item = cartItemService.getById(cartItemId);
                if (item == null) {
                    sendErrorResponse(response, "Cart item khong ton tai");
                    return;
                }

                Product product = productService.getById(item.getProductId());
                if (product != null && product.getQuantity() < quantity) {
                    session.setAttribute("errorMessage", "San pham khong du hang trong kho");
                    response.sendRedirect(request.getContextPath() + "/cart");
                    return;
                }

                item.setQuantity(quantity);
                item.setUpdatedAt(LocalDateTime.now());
                cartItemService.update(item);

                CartUtils.updateCartBadgeFromDb(user.getId(), session);
            }

            session.setAttribute("successMessage", "Da cap nhat gio hang");
            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Sai dinh dang so lieu");
        } catch (Exception e) {
            System.err.println("Error in handleUpdate: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Loi he thong, vui long thu lai");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        try {
            String cartItemIdParam = request.getParameter("cartItemId");
            if (cartItemIdParam == null || cartItemIdParam.trim().isEmpty()) {
                sendErrorResponse(response, "Thieu tham so cartItemId");
                return;
            }

            long cartItemId = Long.parseLong(cartItemIdParam.trim());

            if (CartUtils.isGuestUser(session)) {
                CartItem item = cartItemService.getById(cartItemId);
                if (item != null) {
                    CartUtils.removeFromGuestCart(session, item.getProductId());
                } else {
                    long productId = Long.parseLong(cartItemIdParam.trim());
                    CartUtils.removeFromGuestCart(session, productId);
                }
            } else {
                cartItemService.delete(cartItemId);
                User user = (User) session.getAttribute("currentUser");
                if (user != null) {
                    CartUtils.updateCartBadgeFromDb(user.getId(), session);
                }
            }

            session.setAttribute("successMessage", "Da xoa san pham khoi gio hang");
            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Sai dinh dang cartItemId");
        } catch (Exception e) {
            System.err.println("Error in handleDelete: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Loi he thong, vui long thu lai");
        }
    }

    private void handleBulkUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        Map<String, String[]> parameters = request.getParameterMap();
        boolean hasError = false;

        if (CartUtils.isGuestUser(session)) {
            List<CartItem> guestCart = CartUtils.getGuestCart(session);
            List<CartItem> toRemove = new ArrayList<>();
            for (CartItem item : guestCart) {
                String quantityParamName = "quantity_" + item.getProductId();
                if (parameters.containsKey(quantityParamName)) {
                    try {
                        int quantity = Integer.parseInt(parameters.get(quantityParamName)[0]);
                        if (quantity < 1) {
                            toRemove.add(item);
                            continue;
                        }

                        Product product = productService.getById(item.getProductId());
                        if (product != null && product.getQuantity() >= quantity) {
                            item.setQuantity(quantity);
                            item.setUpdatedAt(LocalDateTime.now());
                        } else {
                            hasError = true;
                        }
                    } catch (NumberFormatException e) {
                        hasError = true;
                    }
                }

                String selectParamName = "selected_" + item.getProductId();
                if (parameters.containsKey(selectParamName)) {
                    boolean selected = "true".equals(parameters.get(selectParamName)[0]);
                    item.setSelected(selected);
                } else {
                    item.setSelected(false);
                }
            }
            for (CartItem item : toRemove) {
                guestCart.remove(item);
            }
            session.setAttribute("guestCart", guestCart);
            CartUtils.updateCartBadge(session);
        } else {
            User user = (User) session.getAttribute("currentUser");
            Cart cart = cartService.getByUserId(user.getId());
            if (cart != null) {
                List<CartItem> items = cartItemService.getByCartId(cart.getId());
                for (CartItem item : items) {
                    String quantityParamName = "quantity_" + item.getId();
                    if (parameters.containsKey(quantityParamName)) {
                        try {
                            int quantity = Integer.parseInt(parameters.get(quantityParamName)[0]);
                            if (quantity < 1) {
                                hasError = true;
                                continue;
                            }

                            Product product = productService.getById(item.getProductId());
                            if (product != null && product.getQuantity() >= quantity) {
                                item.setQuantity(quantity);
                                item.setUpdatedAt(LocalDateTime.now());
                                cartItemService.update(item);
                            } else {
                                hasError = true;
                            }
                        } catch (NumberFormatException e) {
                            hasError = true;
                        }
                    }

                    String selectParamName = "selected_" + item.getId();
                    if (parameters.containsKey(selectParamName)) {
                        boolean selected = "true".equals(parameters.get(selectParamName)[0]);
                        item.setSelected(selected);
                        cartItemService.updateSelected(item.getId(), selected);
                    } else {
                        item.setSelected(false);
                        cartItemService.updateSelected(item.getId(), false);
                    }
                }
                CartUtils.updateCartBadgeFromDb(user.getId(), session);
            }
        }

        if (hasError) {
            session.setAttribute("errorMessage", "Mot so san pham khong the cap nhat do khong du hang hoac so luong khong hop le");
        } else {
            session.setAttribute("successMessage", "Da cap nhat gio hang");
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleClear(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();

        if (CartUtils.isGuestUser(session)) {
            CartUtils.clearGuestCart(session);
        } else {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                Cart cart = cartService.getByUserId(user.getId());
                if (cart != null) {
                    List<CartItem> items = cartItemService.getByCartId(cart.getId());
                    for (CartItem item : items) {
                        cartItemService.delete(item.getId());
                    }
                    CartUtils.updateCartBadgeFromDb(user.getId(), session);
                }
            }
        }

        session.setAttribute("successMessage", "Da xoa toan bo gio hang");
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleSelect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String cartItemIdParam = request.getParameter("cartItemId");
            if (cartItemIdParam == null || cartItemIdParam.trim().isEmpty()) {
                response.getWriter().write("{\"message\":\"Thieu tham so cartItemId\"}");
                return;
            }

            long cartItemId = Long.parseLong(cartItemIdParam.trim());

            if (!CartUtils.isGuestUser(session)) {
                cartItemService.updateSelected(cartItemId, true);
                User user = (User) session.getAttribute("currentUser");
                if (user != null) {
                    Cart cart = cartService.getByUserId(user.getId());
                    if (cart != null) {
                        int count = cartItemService.getSelectedCountByCartId(cart.getId());
                        response.getWriter().write("{\"message\":\"Da chon\",\"selectedCount\":" + count + "}");
                        CartUtils.updateCartBadgeFromDb(user.getId(), session);
                    } else {
                        response.getWriter().write("{\"message\":\"Da chon\"}");
                    }
                } else {
                    response.getWriter().write("{\"message\":\"Da chon\"}");
                }
            } else {
                List<CartItem> guestCart = CartUtils.getGuestCart(session);
                for (CartItem item : guestCart) {
                    if (item.getId() == cartItemId || item.getProductId() == cartItemId) {
                        item.setSelected(true);
                        break;
                    }
                }
                session.setAttribute("guestCart", guestCart);
                CartUtils.updateCartBadge(session);
                int count = 0;
                for (CartItem item : guestCart) {
                    if (item.isSelected()) count++;
                }
                response.getWriter().write("{\"message\":\"Da chon\",\"selectedCount\":" + count + "}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"message\":\"Loi he thong\"}");
        }
    }

    private void handleDeselect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String cartItemIdParam = request.getParameter("cartItemId");
            if (cartItemIdParam == null || cartItemIdParam.trim().isEmpty()) {
                response.getWriter().write("{\"message\":\"Thieu tham so cartItemId\"}");
                return;
            }

            long cartItemId = Long.parseLong(cartItemIdParam.trim());

            if (!CartUtils.isGuestUser(session)) {
                cartItemService.updateSelected(cartItemId, false);
                User user = (User) session.getAttribute("currentUser");
                if (user != null) {
                    Cart cart = cartService.getByUserId(user.getId());
                    if (cart != null) {
                        int count = cartItemService.getSelectedCountByCartId(cart.getId());
                        response.getWriter().write("{\"message\":\"Da bo chon\",\"selectedCount\":" + count + "}");
                        CartUtils.updateCartBadgeFromDb(user.getId(), session);
                    } else {
                        response.getWriter().write("{\"message\":\"Da bo chon\"}");
                    }
                } else {
                    response.getWriter().write("{\"message\":\"Da bo chon\"}");
                }
            } else {
                List<CartItem> guestCart = CartUtils.getGuestCart(session);
                for (CartItem item : guestCart) {
                    if (item.getId() == cartItemId || item.getProductId() == cartItemId) {
                        item.setSelected(false);
                        break;
                    }
                }
                session.setAttribute("guestCart", guestCart);
                CartUtils.updateCartBadge(session);
                int count = 0;
                for (CartItem item : guestCart) {
                    if (item.isSelected()) count++;
                }
                response.getWriter().write("{\"message\":\"Da bo chon\",\"selectedCount\":" + count + "}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"message\":\"Loi he thong\"}");
        }
    }

    private void handleSelectAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String cartIdParam = request.getParameter("cartId");
            if (cartIdParam != null && !cartIdParam.trim().isEmpty()) {
                long cartId = Long.parseLong(cartIdParam.trim());
                if (!CartUtils.isGuestUser(session)) {
                    cartItemService.updateSelectedByCartId(cartId, true);
                    int count = cartItemService.getSelectedCountByCartId(cartId);
                    response.getWriter().write("{\"message\":\"Da chon tat ca\",\"selectedCount\":" + count + "}");
                } else {
                    List<CartItem> guestCart = CartUtils.getGuestCart(session);
                    for (CartItem item : guestCart) {
                        item.setSelected(true);
                    }
                    session.setAttribute("guestCart", guestCart);
                    response.getWriter().write("{\"message\":\"Da chon tat ca\",\"selectedCount\":" + guestCart.size() + "}");
                }
            } else {
                response.getWriter().write("{\"message\":\"Thieu tham so cartId\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"message\":\"Loi he thong\"}");
        }
    }

    private void handleDeselectAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String cartIdParam = request.getParameter("cartId");
            if (cartIdParam != null && !cartIdParam.trim().isEmpty()) {
                long cartId = Long.parseLong(cartIdParam.trim());
                if (!CartUtils.isGuestUser(session)) {
                    cartItemService.updateSelectedByCartId(cartId, false);
                    response.getWriter().write("{\"message\":\"Da bo chon tat ca\",\"selectedCount\":0}");
                } else {
                    List<CartItem> guestCart = CartUtils.getGuestCart(session);
                    for (CartItem item : guestCart) {
                        item.setSelected(false);
                    }
                    session.setAttribute("guestCart", guestCart);
                    response.getWriter().write("{\"message\":\"Da bo chon tat ca\",\"selectedCount\":0}");
                }
            } else {
                response.getWriter().write("{\"message\":\"Thieu tham so cartId\"}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"message\":\"Loi he thong\"}");
        }
    }

    private void handleGetSelectedCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String cartIdParam = request.getParameter("cartId");
            if (cartIdParam != null && !cartIdParam.trim().isEmpty()) {
                long cartId = Long.parseLong(cartIdParam.trim());
                if (!CartUtils.isGuestUser(session)) {
                    int count = cartItemService.getSelectedCountByCartId(cartId);
                    int quantity = cartItemService.getSelectedQuantityByCartId(cartId);
                    response.getWriter().write("{\"selectedCount\":" + count + ",\"selectedQuantity\":" + quantity + "}");
                } else {
                    List<CartItem> guestCart = CartUtils.getGuestCart(session);
                    int count = 0;
                    int quantity = 0;
                    for (CartItem item : guestCart) {
                        if (item.isSelected()) {
                            count++;
                            quantity += item.getQuantity();
                        }
                    }
                    response.getWriter().write("{\"selectedCount\":" + count + ",\"selectedQuantity\":" + quantity + "}");
                }
            } else {
                response.getWriter().write("{\"selectedCount\":0,\"selectedQuantity\":0}");
            }
        } catch (Exception e) {
            response.getWriter().write("{\"message\":\"Loi he thong\"}");
        }
    }

    private void handleCalculatePrice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String productIdParam = request.getParameter("productId");
        String quantityParam = request.getParameter("quantity");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (productIdParam == null || quantityParam == null) {
                response.getWriter().write("{\"error\":\"Thieu tham so\"}");
                return;
            }

            long productId = Long.parseLong(productIdParam.trim());
            int quantity = Integer.parseInt(quantityParam.trim());

            if (quantity < 1) {
                response.getWriter().write("{\"error\":\"So luong khong hop le\"}");
                return;
            }

            Product product = productService.getById(productId);
            if (product == null) {
                response.getWriter().write("{\"error\":\"San pham khong ton tai\"}");
                return;
            }

            double unitPrice = product.getDiscount() > 0
                    ? product.getPrice() * (100 - product.getDiscount()) / 100
                    : product.getPrice();
            double totalPrice = unitPrice * quantity;

            String json = String.format(
                    "{\"unitPrice\":%.0f,\"totalPrice\":%.0f,\"discount\":%.0f}",
                    unitPrice, totalPrice, product.getDiscount()
            );
            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            response.getWriter().write("{\"error\":\"Sai dinh dang so lieu\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Loi he thong\"}");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"" + message.replace("\"", "\\\"") + "\"}");
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
        }
    }
}
