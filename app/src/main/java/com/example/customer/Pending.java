package com.example.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Pending extends AppCompatActivity {
    private String userId;
    private DatabaseReference myRef;
    private TextView news,copies,tins,plastic,cans;
    private Customer intentCustomer;
    private Button logout;
    private Waste waste1;
    private ValueEventListener eventListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending);
        intentCustomer= (Customer) getIntent().getExtras().getSerializable("customer");
//        Toast.makeText(Pending.this,intentCustomer.lastused,Toast.LENGTH_LONG).show();
        userId=getUserId(intentCustomer.email);
        showRequest();
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


    public void showRequest(){
        news=findViewById(R.id.newspaper);
        copies=findViewById(R.id.copies);
        tins=findViewById(R.id.tins);
        plastic=findViewById(R.id.plastic);
        cans=findViewById(R.id.cans);
        logout = (Button)findViewById(R.id.Logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Pending.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        final String userid=getUserId(intentCustomer.email);
        myRef= FirebaseDatabase.getInstance().getReference("garbage").child(intentCustomer.city).child(getUserId(intentCustomer.email)+intentCustomer.lastused);
        waste1=null;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        waste1 = dataSnapshot.getValue(Waste.class);
                        news.setText("Newspaper Rs 10/kg-\t " + waste1.newspaper);
                        copies.setText("Used Copies Rs 7/kg-\t " + waste1.paper);
                        tins.setText("Tins Rs 15/kg-\t\t " + waste1.tins);
                        plastic.setText("Plastic Rs 7/kg-\t " + waste1.plastic);
                        cans.setText("Metallic Cans Rs 8/kg-\t " + waste1.cans);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
//        myRef.addValueEventListener(eventListener);
//        myRef=null;

        Button contact,about;
        contact=findViewById(R.id.ContactUs);
        about=findViewById(R.id.About);
        //need to change after merging git pr
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:junkoscrap@gmail.com")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, "Query from "+intentCustomer.username);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Pending.this,About.class);
                i.putExtra("userId",userid);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(eventListener != null) {
            myRef.removeEventListener(eventListener);
        }
        myRef=null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(eventListener != null) {
            myRef.removeEventListener(eventListener);
        }
        myRef=null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(Pending.this,"Thank you!",Toast.LENGTH_SHORT).show();
        this.finishAffinity();
    }
}
