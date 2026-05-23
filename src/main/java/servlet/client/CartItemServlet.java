package servlet.client;

import java.io.IOException;
import java.time.LocalDateTime;

import beans.Cart;
import beans.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CartItemService;
import service.CartService;

@WebServlet(name = "CartItemServlet", value = "/cartItem")
public class CartItemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final CartService cartService = new CartService();
    private final CartItemService cartItemService = new CartItemService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "add":
                    handleAdd(request, response);
                    break;
                case "update":
                    handleUpdate(request, response);
                    break;
                case "delete":
                    handleDelete(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/cart");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại!");
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long userId = Long.parseLong(request.getParameter("userId"));
        long productId = Long.parseLong(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        Cart cart = cartService.getByUserId(userId);

        long cartId;
        if (cart == null) {
            cartId = cartService.insert(
                    new Cart(0L, userId, LocalDateTime.now(), null)
            );
        } else {
            cartId = cart.getId();
        }

        CartItem item = cartItemService.getByCartIdAndProductId(cartId, productId);

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            item.setUpdatedAt(LocalDateTime.now());
            cartItemService.update(item);
        } else {
            cartItemService.insert(
                    new CartItem(0L, cartId, productId, quantity, LocalDateTime.now(), null)
            );
        }

        int cartCount = cartService.countCartItemQuantityByUserId(userId);
        request.getSession().setAttribute("cartCount", cartCount);

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long cartItemId = Long.parseLong(request.getParameter("cartItemId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        CartItem item = cartItemService.getById(cartItemId);
        if (item == null) {
            throw new IllegalStateException("Cart item not found");
        }

        Cart cart = cartService.getById(item.getCartId());
        long userId = cart != null ? cart.getUserId() : 0;

        item.setQuantity(quantity);
        item.setUpdatedAt(LocalDateTime.now());
        cartItemService.update(item);

        if (userId > 0) {
            int cartCount = cartService.countCartItemQuantityByUserId(userId);
            request.getSession().setAttribute("cartCount", cartCount);
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {

        long cartItemId = Long.parseLong(request.getParameter("cartItemId"));

        CartItem item = cartItemService.getById(cartItemId);
        long userId = 0;
        if (item != null) {
            Cart cart = cartService.getById(item.getCartId());
            userId = cart != null ? cart.getUserId() : 0;
        }

        cartItemService.delete(cartItemId);
        if (userId > 0) {
            int cartCount = cartService.countCartItemQuantityByUserId(userId);
            request.getSession().setAttribute("cartCount", cartCount);
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
