package dao;

import beans.shipping.Address;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setId(rs.getLong("id"));
        address.setUserId(rs.getLong("user_id"));
        address.setFullname(rs.getString("fullname"));
        address.setPhone(rs.getString("phone"));
        address.setProvince(rs.getString("province"));
        address.setDistrict(rs.getString("district"));
        address.setWard(rs.getString("ward"));
        address.setAddressDetail(rs.getString("address_detail"));
        address.setIsDefault(rs.getBoolean("is_default"));
        address.setProvinceId(rs.getInt("province_id"));
        address.setDistrictId(rs.getInt("district_id"));
        address.setWardCode(rs.getString("ward_code"));
        return address;
    }

    public List<Address> getAddressesByUserId(long userId) {
        String sql = "SELECT * FROM user_shipping_addresses WHERE user_id = ? ORDER BY is_default DESC";
        List<Address> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAddress(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createAddress(Address address) {
        String sql = "INSERT INTO user_shipping_addresses " +
                "(user_id, fullname, phone, province, district, ward, address_detail, " +
                "province_id, district_id, ward_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, address.getUserId());
            ps.setString(2, address.getFullname());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getProvince());
            ps.setString(5, address.getDistrict());
            ps.setString(6, address.getWard());
            ps.setString(7, address.getAddressDetail());
            ps.setInt(8, address.getProvinceId());
            ps.setInt(9, address.getDistrictId());
            ps.setString(10, address.getWardCode());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Address getAddressById(long userId, long addressId) {
        String sql = "SELECT * FROM user_shipping_addresses WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, addressId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAddress(long userId, Address address) {
        String sql = "UPDATE user_shipping_addresses SET " +
                "fullname = ?, phone = ?, province = ?, district = ?, ward = ?, address_detail = ?, " +
                "province_id = ?, district_id = ?, ward_code = ? " +
                "WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, address.getFullname());
            ps.setString(2, address.getPhone());
            ps.setString(3, address.getProvince());
            ps.setString(4, address.getDistrict());
            ps.setString(5, address.getWard());
            ps.setString(6, address.getAddressDetail());
            ps.setInt(7, address.getProvinceId());
            ps.setInt(8, address.getDistrictId());
            ps.setString(9, address.getWardCode());
            ps.setLong(10, address.getId());
            ps.setLong(11, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAddress(long userId, long addressId) {
        String sql = "DELETE FROM user_shipping_addresses WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, addressId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setDefaultAddress(long userId, long addressId) {
        String sql = "UPDATE user_shipping_addresses SET is_default = (id = ?) WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, addressId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Address getDefaultAddress(long userId){
        String sql = "SELECT * FROM user_shipping_addresses WHERE user_id = ? AND is_default = 1";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}