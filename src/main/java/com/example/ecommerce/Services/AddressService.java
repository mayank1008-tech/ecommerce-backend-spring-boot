package com.example.ecommerce.Services;

import com.example.ecommerce.Model.User;
import com.example.ecommerce.payload.AddressDTO;
import com.example.ecommerce.payload.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressDTO addAddress(AddressDTO addressDTO, User user);

    AddressResponse getAllAddress(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressDTO getAddressById(Long addressId);

    AddressResponse getAllUserAddress(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    AddressDTO updateAddressById(AddressDTO addressDTO, Long addressId);

    void deleteAddressById(Long addressId);

}
