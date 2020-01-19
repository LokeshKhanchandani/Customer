package com.example.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    private EditText name,email,mobile,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();
        name=findViewById(R.id.editTextName);
        email=findViewById(R.id.editTextEmail);
        mobile=findViewById(R.id.editTextMobile);
        pass=findViewById(R.id.editTextPassword);

    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,Login.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }
    public Boolean isSpecialCharAvailable(String s) {
        //int counter =0;
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("[^A-Za-z0-9]");//replace this with your needs
        Matcher m = p.matcher(s);
        // boolean b = m.matches();

        boolean b = m.find();
        if (b == true)
            return true;
        else
            return false;
    }

    public void Register(View view) {
        if(name.getText().toString().trim().equals("") || email.getText().toString().trim().equals("")
                || mobile.getText().toString().trim().equals("") || pass.getText().toString().trim().equals("")) {
            Toast.makeText(Register.this, "Complete and Validate Details", Toast.LENGTH_LONG).show();
        }
        else if(!(!TextUtils.isEmpty(email.getText().toString().trim()) && Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()))
            email.setError("Enter a valid email");
        else if (pass.length()<6||(!isSpecialCharAvailable(pass.getText().toString())))
            pass.setError("Minimum password of length 6 with atleast one special character");
        else if (mobile.getText().toString().trim().length()!=10)
            mobile.setError("Enter valid mobile number");
        else{
            final FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("random", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseDatabase database=FirebaseDatabase.getInstance();
                                final DatabaseReference myRef=database.getReference("customers").child(getUserId(email.getText().toString().trim()));
//        Customer customer=myRef.
                                final Customer[] customer = new Customer[1];
                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            customer[0] =new Customer(name.getText().toString().trim()
                                                    ,mobile.getText().toString().trim(),
                                                    null,email.getText().toString().trim(),
                                                    null,true);
                                            myRef.setValue(customer[0]);
                                        }else
                                            customer[0]=dataSnapshot.getValue(Customer.class);
                                            Toast.makeText(Register.this,"Registration successful",Toast.LENGTH_LONG).show();
                                            Intent i=new Intent(Register.this,Login.class);
                                            startActivity(i);
                                            finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("random", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
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
}
