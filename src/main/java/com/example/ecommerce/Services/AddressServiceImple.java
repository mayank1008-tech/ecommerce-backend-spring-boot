package com.example.ecommerce.Services;

import com.example.ecommerce.Exceptions.APIexception;
import com.example.ecommerce.Exceptions.ResourceNotFound;
import com.example.ecommerce.Model.Address;
import com.example.ecommerce.Model.Category;
import com.example.ecommerce.Model.User;
import com.example.ecommerce.Repository.AddressRepository;
import com.example.ecommerce.Repository.UserRepository;
import com.example.ecommerce.Util.AuthUtil;
import com.example.ecommerce.payload.AddressDTO;
import com.example.ecommerce.payload.AddressResponse;
import com.example.ecommerce.payload.CategoryResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImple implements AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, User user) {
        Address  address = modelMapper.map(addressDTO, Address.class);
        List<Address> existingAdd = user.getAddresses();
        existingAdd.add(address);
        user.setAddresses(existingAdd);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressResponse getAllAddress(Integer pageNumber, Integer pageSize,  String sortBy, String sortOrder) {
        Sort sorted = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pagedetails = PageRequest.of(pageNumber,pageSize, sorted);
        Page<Address> addressPage = addressRepository.findAll(pagedetails);
        List<Address> addresses= addressPage.getContent();

        if(addresses.isEmpty()){
            throw new APIexception("No address added till now!!!");
        }
        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        AddressResponse res =  new AddressResponse();
        res.setContent(addressDTOS);

        //To set JSON metdata
        res.setPageNumber(addressPage.getNumber());
        res.setPageSize(addressPage.getSize());
        res.setTotalElements(addressPage.getTotalElements());
        res.setTotalPages(addressPage.getTotalPages());
        res.setLastPage(addressPage.isLast());
        return res;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFound("Address","addressId",addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public AddressResponse getAllUserAddress(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        User user = authUtil.loggedInUser();
        Sort sorted = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pagedetails = PageRequest.of(pageNumber,pageSize, sorted);
        Page<Address> addressPage = addressRepository.findByUser(user,pagedetails);
        List<Address> addresses= addressPage.getContent();

        if(addresses.isEmpty()){
            throw new APIexception("No address added till now!!!");
        }
        List<AddressDTO> addressDTOS = addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        AddressResponse res =  new AddressResponse();
        res.setContent(addressDTOS);

        //To set JSON metdata
        res.setPageNumber(addressPage.getNumber());
        res.setPageSize(addressPage.getSize());
        res.setTotalElements(addressPage.getTotalElements());
        res.setTotalPages(addressPage.getTotalPages());
        res.setLastPage(addressPage.isLast());
        return res;
    }

    @Override
    public AddressDTO updateAddressById(AddressDTO addressDTO, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFound("Address","addressId",addressId));
        address.setPincode(addressDTO.getPincode());
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        address.setStreet(addressDTO.getStreet());
        address.setBuildingName(addressDTO.getBuildingName());
        address.setState(addressDTO.getState());

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        user.getAddresses().add(address);
        userRepository.save(user);
        return modelMapper.map(addressRepository.save(address), AddressDTO.class);
    }

    @Override
    public void deleteAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFound("Address","addressId",addressId));
        addressRepository.delete(address);

        User user = address.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        userRepository.save(user);
    }
}
