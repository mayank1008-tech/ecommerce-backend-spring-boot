package com.example.ecommerce.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @OneToOne(mappedBy = "payment", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Order order;

    @NotBlank
    @Size(min=4, message = "Payment method must be of atleast 4 chars")
    private String paymentMethod;

    private String pgPaymentId;  //payment gateway ki payment id
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;

    public Payment(String paymentMethod, String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName) {

        this.paymentMethod = paymentMethod;
        this.pgPaymentId=pgPaymentId;
        this.pgStatus=pgStatus;
        this.pgResponseMessage=pgResponseMessage;
        this.pgName=pgName;
    }

}
