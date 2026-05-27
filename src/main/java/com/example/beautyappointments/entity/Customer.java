package com.example.beautyappointments.entity;
import jakarta.persistence.*;

 @Entity
public class Customer {
     @Id
     @GeneratedValue (strategy = GenerationType.IDENTITY)
     private Long id;
     private String name;
     private String phone;
     private String email;

     public String getPhone() {
         return phone;
     }

     public void setPhone(String phone) {
         this.phone = phone;
     }

     public String getEmail() {
         return email;
     }

     public void setEmail(String email) {
         this.email = email;
     }

     public Long getId() {
         return id;
     }

     public void setId(Long id) {
         this.id = id;
     }



     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }


@Override

public String toString() {
    return "Customer{" +
            "name='" + name + '\'' +
            ", phone='" + phone + '\'' +
            ", email='" + email + '\'' +
            '}';
}

}
