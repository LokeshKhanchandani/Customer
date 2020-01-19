package com.example.customer;

public class Waste {
    public int newspaper,paper,tins,plastic,cans;
    public String userId,address;
    Waste(){}

    Waste(int n,int p,int t,int pl,int c,String u){
        newspaper=n;
        paper=p;
        tins=t;
        plastic=pl;
        cans=c;
        userId=u;
    }
}
