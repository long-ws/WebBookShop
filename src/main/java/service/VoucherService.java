package service;

import beans.Voucher;
import dao.CartItemDAO;
import dao.VoucherDao;
import dto.getUsableVouchers.CartItemDTO;
import dto.getUsableVouchers.VoucherDTO;

import java.sql.SQLException;
import java.util.*;

public class VoucherService {
    private final VoucherDao voucherDao;
    private final CartItemDAO cartItemDao = new CartItemDAO();

    public VoucherService() {
        this.voucherDao = new VoucherDao();
    }

    public boolean createVoucher(Voucher voucher) {
        return voucherDao.createVoucher(voucher);
    }
    public int count() {
        return voucherDao.count();
    }

    public List<Voucher> getOrderedPart(int limit, int offset, String orderBy, String orderDir) {
        return voucherDao.getOrderedPart(limit, offset, orderBy, orderDir);
    }

    public Voucher getVoucherById(long vId) {
        return voucherDao.getVoucherById(vId);
    }

    public boolean deleteVoucher(long vId) {
        return voucherDao.deleteVoucher(vId);
    }

    public boolean updateVoucher(Voucher voucher) {
        return voucherDao.updateVoucher(voucher);
    }
    public Voucher getVoucherWithRelations(long vId){
        return voucherDao.getVoucherWithRelations(vId);
    }

    public List<Voucher> getVouchersForUser(Integer applyTo, int offset, int recordsPerPage) {
        return voucherDao.getVouchersForUser(applyTo, offset, recordsPerPage);
    }

    public int getTotalVouchersCountForUser(Integer applyTo) {
        return voucherDao.getTotalVouchersCountForUser(applyTo);
    }
    public Map<VoucherDTO, Boolean> getUsableVouchers(long userId, long cartId) {
        List<CartItemDTO> cartItems = cartItemDao.getCartItemDTO(cartId);
        List<VoucherDTO> availableVouchers = voucherDao.getUsableVouchers(userId);

        double cartTotal = 0;
        Map<Long, Double> categoryTotal = new HashMap<>();
        Map<Long, Double> productTotal = new HashMap<>();
        for (CartItemDTO item : cartItems) {
            double itemTotal = item.getPrice() * item.getQuantity();
            cartTotal += itemTotal;
            categoryTotal.put(item.getCategoryId(), categoryTotal.getOrDefault(item.getCategoryId(), 0.0) + itemTotal);
            productTotal.put(item.getProductId(), productTotal.getOrDefault(item.getProductId(), 0.0) + itemTotal);
        }
        List<VoucherDTO> usableList = new ArrayList<>();
        List<VoucherDTO> unusableList = new ArrayList<>();

        for (VoucherDTO v : availableVouchers) {
            boolean isUsable = false;
            double minPurchase = v.getMinPurchase();

            switch (v.getApplyTo()) {
                case 0:
                case 3:
                    if (cartTotal >= minPurchase) isUsable = true;
                    break;
                case 1:
                    double totalValidCategoryAmount = 0;
                    for (long categoryId : convertCsvToList(v.getCategoryIdsCsv())) {
                        totalValidCategoryAmount += categoryTotal.getOrDefault(categoryId, 0.0);
                    }
                    if (totalValidCategoryAmount >= minPurchase && totalValidCategoryAmount > 0) isUsable = true;
                    break;
                case 2:
                    double totalValidProductAmount = 0;
                    for (long productId : convertCsvToList(v.getProductIdsCsv())) {
                        totalValidProductAmount += productTotal.getOrDefault(productId, 0.0);
                    }
                    if (totalValidProductAmount >= minPurchase && totalValidProductAmount > 0) isUsable = true;
                    break;
            }

            if (isUsable) {
                usableList.add(v);
            } else {
                unusableList.add(v);
            }
        }
        Map<VoucherDTO, Boolean> voucherMap = new LinkedHashMap<>();
        for (VoucherDTO v : usableList) {
            voucherMap.put(v, true);
        }
        for (VoucherDTO v : unusableList) {
            voucherMap.put(v, false);
        }

        return voucherMap;
    }    private List<Long> convertCsvToList(String csv) {
        List<Long> list = new ArrayList<>();
        if (csv != null && !csv.trim().isEmpty()) {
            String[] tokens = csv.split(",");
            for (String token : tokens) {
                try {
                    list.add(Long.parseLong(token.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return list;
    }
}
