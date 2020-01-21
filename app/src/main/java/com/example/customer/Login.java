package com.example.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;

public class Login extends AppCompatActivity {

    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    private EditText email,password;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        checkLocationPermission();

        signInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    // Login with gmail
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(Login.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(Login.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updategmailUI(user);
                    } else {
                        Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
                        updategmailUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(Login.this, "acc failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updategmailUI(FirebaseUser fuser){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            String userId=getUserId(personEmail);
//            Toast.makeText(Login.this,userId+"123 "+personName + personEmail ,Toast.LENGTH_SHORT).show();
            Customer customer=checkUser(userId);
            Intent intent=new Intent(Login.this,Garbage.class);
            intent.putExtra("userId",userId);
            intent.putExtra("current",customer);
            startActivity(intent);
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

    private Customer checkUser(String userId){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference myRef=database.getReference("customers").child(userId);
//        Customer customer=myRef.
        final Customer[] customer = new Customer[1];
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    customer[0] =new Customer(account.getDisplayName(),"0",null,account.getEmail(),null,true);
                    myRef.setValue(customer[0]);
                }else
                    customer[0]=dataSnapshot.getValue(Customer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return customer[0];
        }
    //Normal Login
    public void LoginAction(View view) {
        if(email.getText().toString().trim().equals("") || password.getText().toString().trim().equals(""))
            Toast.makeText(Login.this,"Please complete details first!",Toast.LENGTH_SHORT).show();
        else {
            mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Toast.makeText(Login.this, "Login Failed ", Toast.LENGTH_LONG).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    private void updateUI(FirebaseUser account){
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            String personName = account.getDisplayName();
//            String personGivenName = account.getGivenName();
//            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
//            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

//            Toast.makeText(Login.this,personName + personEmail ,Toast.LENGTH_SHORT).show();
            String userId=getUserId(personEmail);
            Intent intent=new Intent(Login.this,Garbage.class);
            intent.putExtra("userId",userId);
            intent.putExtra("current",extractCustomer(userId));
            startActivity(intent);
        }

    }


    public void onLoginClick(View View){
        startActivity(new Intent(this,Register.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }

    private Customer customer1;

    public Customer extractCustomer(String userId){
        DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("customers").child(userId);
        customer1=null;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customer1=dataSnapshot.getValue(Customer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseFetchError",databaseError+"");
            }
        });
        return customer1;
    }

    public void ForgotPassword(View view) {
        final String[] m_Text = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password link");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Email");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                m_Text[0] = input.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(m_Text[0])
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "Email sent.");
                                    Toast.makeText(Login.this,"Password reset link sent to mail",Toast.LENGTH_LONG).show();
                                }
                                dialog.cancel();
                            }
                        });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Login.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(Login.this,"Permission Granted",Toast.LENGTH_SHORT).show();
                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, new LocationListener() {
//                        });
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
////            locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
////            locationManager.removeUpdates((LocationListener) this);
//        }
//    }
}
