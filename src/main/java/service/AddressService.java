package service;

import beans.shipping.Address;
import dao.AddressDAO;

import java.util.List;

public class AddressService {
    private final AddressDAO dao =  new AddressDAO();
    public List<Address> getAddressesByUserId(long userId){
        return dao.getAddressesByUserId(userId);
    }
    public boolean createAddress(Address address){
        return dao.createAddress(address);
    }
    public Address getAddressById(long userId, long addressId){
        return dao.getAddressById(userId, addressId);
    }
    public boolean updateAddress(long userId, Address address){
        return dao.updateAddress(userId, address);
    }
    public boolean deleteAddress(long userId, long addressId){
        return dao.deleteAddress(userId, addressId);
    }
    public boolean setDefaultAddress(long userId, long addressId){
        return dao.setDefaultAddress(userId, addressId);
    }
}
