package com.example.ecommerce.Controller;

import com.example.ecommerce.Model.User;
import com.example.ecommerce.Services.AddressService;
import com.example.ecommerce.Util.AuthUtil;
import com.example.ecommerce.config.AppConst;
import com.example.ecommerce.payload.AddressDTO;
import com.example.ecommerce.payload.AddressResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;
    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.addAddress(addressDTO,user);
        return ResponseEntity.ok().body(savedAddressDTO);
    }

    @GetMapping("/addresses")
    public ResponseEntity<AddressResponse> getAllAddresses(@RequestParam(name="pageNumber", defaultValue = AppConst.PAGE_NUMBER, required = false) Integer pageNumber,
                                                            @RequestParam (name="pageSize", defaultValue = AppConst.PAGE_SIZE, required = false) Integer pageSize,
                                                            @RequestParam (name="sortBy", defaultValue = AppConst.SORT_BY, required = false) String sortBy,
                                                            @RequestParam (name="sortOrder", defaultValue = AppConst.SORT_ORDER, required = false) String sortOrder) {
        AddressResponse addressDTOS = addressService.getAllAddress(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok().body(addressDTOS);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("addressId") Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return ResponseEntity.ok().body(addressDTO);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<AddressResponse> getByUser(@RequestParam(name="pageNumber", defaultValue = AppConst.PAGE_NUMBER, required = false) Integer pageNumber,
                                                     @RequestParam (name="pageSize", defaultValue = AppConst.PAGE_SIZE, required = false) Integer pageSize,
                                                     @RequestParam (name="sortBy", defaultValue = AppConst.SORT_BY, required = false) String sortBy,
                                                     @RequestParam (name="sortOrder", defaultValue = "addressId", required = false) String sortOrder){
        AddressResponse addressDTOS = addressService.getAllUserAddress(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok().body(addressDTOS);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable("addressId") Long addressId, @RequestBody AddressDTO addressDTO) {
        AddressDTO UpdatedaddressDTO = addressService.updateAddressById(addressDTO, addressId);
        return ResponseEntity.ok().body(UpdatedaddressDTO);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable("addressId") Long addressId) {
        addressService.deleteAddressById(addressId);
        return ResponseEntity.ok().body("Address with id: " + addressId + " has been deleted");
    }
}
