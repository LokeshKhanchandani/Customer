package com.example.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.getDefault;

public class Garbage extends AppCompatActivity {
    private String userId;
    private DatabaseReference myRef,myRef1;
    private Button done,logout;
    private Customer customer;
    ValueEventListener eventListener1,eventListener2;

    private TextView news,copies,tins,plastic,cans;
    private Customer intentCustomer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garbage);
        intentCustomer= (Customer) getIntent().getExtras().getSerializable("customer");
        userId=getUserId(intentCustomer.email);
        fillForm();
//        userId=getIntent().getExtras().getString("userId");
//        customer= (Customer) getIntent().getExtras().getSerializable("current");
//        if(customer==null){
//            Toast.makeText(Garbage.this,"Customer data not present!",Toast.LENGTH_LONG).show();
//            finish();
//        }

//        myRef= FirebaseDatabase.getInstance().getReference("customers").child(userId);
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                customer=dataSnapshot.getValue(Customer.class);
//
//                if(customer.picked){
//                    setContentView(R.layout.activity_garbage);
////                    Toast.makeText(Garbage.this,"chala",Toast.LENGTH_LONG).show();
//                    fillForm();
//                }
//
//                else {
//                    setContentView(R.layout.pending);
//                    showRequest();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }
    private String getUserId(String emai){
        String input="";
        for(int i=0;i<emai.length();i++)
        {
            if((emai.charAt(i)>='a' && emai.charAt(i)<='z')|| (emai.charAt(i)>='A' && emai.charAt(i)<='Z') || (emai.charAt(i)>='0' && emai.charAt(i)<='9')) {
                input += emai.charAt(i);
            }
        }
        return input;
    }

//    private void update(final Customer customer){

//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    //send intent to about us activity
////                    finish();
////                    Intent i=new Intent(Garbage.this,About.class);
////                    startActivity(i);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


    public void fillForm(){
        CheckBox news,paper,tins,plastic,cans;
        news=findViewById(R.id.newspaper);
        paper=findViewById(R.id.Paper);
        tins=findViewById(R.id.tins);
        plastic=findViewById(R.id.Plastic);
        cans=findViewById(R.id.Cans);
        final EditText newspaperT,paperT,tinsT,plasticT,cansT;
        newspaperT=findViewById(R.id.editTextNewspaper);
        paperT=findViewById(R.id.editTextPaper);
        tinsT=findViewById(R.id.editTextTins);
        plasticT=findViewById(R.id.editTextPlastic);
        cansT=findViewById(R.id.editTextCans);
//        logout = findViewById(R.id.Logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        news.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    newspaperT.setVisibility(View.VISIBLE);
                }else
                    newspaperT.setVisibility(View.INVISIBLE);
            }
        });
        paper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    paperT.setVisibility(View.VISIBLE);
                }else
                    paperT.setVisibility(View.INVISIBLE);
            }
        });
        tins.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tinsT.setVisibility(View.VISIBLE);
                }else
                    tinsT.setVisibility(View.INVISIBLE);
            }
        });
        cans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    cansT.setVisibility(View.VISIBLE);
                }else
                    cansT.setVisibility(View.INVISIBLE);
            }
        });
        plastic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    plasticT.setVisibility(View.VISIBLE);
                }else
                    plasticT.setVisibility(View.INVISIBLE);
            }
        });
        done=findViewById(R.id.Done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n,p,t,pl,c;
                if(newspaperT.getVisibility()==View.VISIBLE && !newspaperT.getText().toString().equals(""))
                    n=Integer.parseInt(newspaperT.getText().toString().trim());
                else
                    n=0;
                if(paperT.getVisibility()==View.VISIBLE && !paperT.getText().toString().equals(""))
                    p=Integer.parseInt(paperT.getText().toString().trim());
                else
                    p=0;
                if(tinsT.getVisibility()==View.VISIBLE && !tinsT.getText().toString().equals(""))
                    t=Integer.parseInt(tinsT.getText().toString().trim());
                else
                    t=0;
                if(plasticT.getVisibility()==View.VISIBLE && !plasticT.getText().toString().equals(""))
                    pl=Integer.parseInt(plasticT.getText().toString().trim());
                else
                    pl=0;
                if(cansT.getVisibility()==View.VISIBLE && !cansT.getText().toString().equals(""))
                    c=Integer.parseInt(cansT.getText().toString().trim());
                else
                    c=0;
                newspaperT.setText("Newspaper Rs 10/kg- "+n);
                paperT.setText("Used Copies Rs 7/kg- "+p);
                tinsT.setText("Tins Rs 15/kg- "+t);
                plasticT.setText("Plastic Rs 7/kg- "+pl);
                cansT.setText("Metallic Cans Rs 8/kg- "+c);
                newspaperT.setClickable(false);
                paperT.setClickable(false);
                tinsT.setClickable(false);
                plasticT.setClickable(false);
                cansT.setClickable(false);
                Long tslong=System.currentTimeMillis()/1000;
                intentCustomer.lastused=tslong.toString();
                customer=intentCustomer;
                final Waste waste=new Waste(n,p,t,pl,c,customer.username,customer.address,customer.phone,userId);
                final String user=getUserId(customer.email);
//                List<Address> addresses=updateLocation(customer);
                FusedLocationProviderClient fusedLocationClient;
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(Garbage.this);
                final List<Address>[] addresses = new List[]{null};
                fusedLocationClient.getLastLocation().addOnSuccessListener(Garbage.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Geocoder geocoder = new Geocoder(Garbage.this, getDefault());
                                    try {
                                        addresses[0] = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                                        if(addresses.length>0) {
                                            customer.city = addresses[0].get(0).getLocality();
                                            waste.address = addresses[0].get(0).getAddressLine(0);
                                        }else{
                                            customer.city="Agra";
                                            waste.address="Bhagwan Talkies";
                                        }
                                        customer.picked=false;
                                        myRef=FirebaseDatabase.getInstance().getReference("garbage").child(customer.city).child(user+customer.lastused);
//        Customer customer=myRef.
                                        myRef.setValue(waste);
                                        myRef=null;
                                        Toast.makeText(Garbage.this,"Your Request is successfully sent",Toast.LENGTH_SHORT).show();
                                        done.setVisibility(View.INVISIBLE);
//                                        update(customer);
                                        String user=getUserId(customer.email);
                                        myRef1=FirebaseDatabase.getInstance().getReference("customers").child(getUserId(user));

//        Toast.makeText(Garbage.this,"Updating Location",Toast.LENGTH_SHORT).show();

//                                        final Customer newcustomer=customer;
                                        myRef1.setValue(customer);
                                        myRef1=null;
                                        Intent i = new Intent(Garbage.this, Pending.class);
                                        i.putExtra("userId", userId);
                                        i.putExtra("customer",customer);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                                        finish();
//
//                                        myRef.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
//                            String address = addresses.get(0).getAddressLine(0);
//                            String city = addresses.get(0).getLocality();
//                            String state = addresses.get(0).getAdminArea();
//                            String zip = addresses.get(0).getPostalCode();
//                            String country = addresses.get(0).getCountryName();
//                            customer.city=city;
                                }
                            }
                        });
//                if(addresses!=null) {
//                    customer.city = addresses.get(0).getLocality();
//                    waste.address=addresses.get(0).getAddressLine(0);
//                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(FirebaseDatabase.getInstance()!=null)
            myRef=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef=null;
    }
}
