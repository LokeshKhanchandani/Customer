package com.example.customer;

public class Customer {

    public String username,phone,address,email;
    public boolean picked;
    Customer(){}

    Customer(String username,String phone,String address,String email,Boolean picked){
        this.username=username;
        this.phone=phone;
        this.address=address;
        this.email=email;
        this.picked=picked;
    }
}
