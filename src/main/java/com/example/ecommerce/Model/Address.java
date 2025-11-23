package com.example.ecommerce.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Building Name Must be 5 chars!!!")
    @Column(name = "buildingName")
    private String buildingName;
    @NotBlank
    @Size(min = 5, message = "Street Name Must be 5 chars!!!")
    @Column(name = "street")
    private String street;
    @NotBlank
    @Size(min = 4, message = "City name  be 4 chars!!!")
    @Column(name = "city")
    private String city;
    @NotBlank
    @Size(min = 2, message = "State name  be 2 chars!!!")
    @Column(name = "state")
    private String state;
    @Size(min = 6, message = "Pin name  be 6 chars!!!")
    @Column(name = "pinCode")
    private String pincode;
    @Size(min = 2, message = "Country name  be 2 chars!!!")
    @Column(name = "country")
    private String country;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
