package com.example.customer;

import java.io.Serializable;

public class Customer implements Serializable {

    public String username,phone,address,email,city;
    public boolean picked;
    Customer(){}

    Customer(String username,String phone,String address,String email,String city,Boolean picked){
        this.username=username;
        this.phone=phone;
        this.address=address;
        this.email=email;
        this.picked=picked;
        this.city=city;
        if(city==null)
            this.city="Lucknow";
    }
}
