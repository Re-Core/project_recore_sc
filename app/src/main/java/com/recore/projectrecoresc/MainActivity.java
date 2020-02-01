package com.recore.projectrecoresc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.recore.projectrecoresc.Common.Common;
import com.recore.projectrecoresc.Helper.LocalHelper;
import com.recore.projectrecoresc.Model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
//import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE =1000 ;

    private Button btnContinue;
    private FirebaseDatabase db;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    //added
    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthCredential credential;
    TextInputEditText edtPhoneNumber, edtVerificationCode;
    Button btnSendCode, btnVerify;

    AlertDialog waitingDialog;
    private LinearLayout main_layout;


//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Arkhip_font.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());
        setContentView(R.layout.activity_main);

        printHashKey();

        Paper.init(this);
        String language = Paper.book().read("language");
        if (language == null) {
            Paper.book().write("language", "en");
            changeLanguageDialog();
        }


        mAuth=FirebaseAuth.getInstance();

        main_layout = (LinearLayout)findViewById(R.id.main_layout);
        edtPhoneNumber=(TextInputEditText)findViewById(R.id.phone_edit_text);
        edtVerificationCode=(TextInputEditText)findViewById(R.id.code_edit_text);

        btnSendCode=(Button)findViewById(R.id.btn_send_code);
        btnVerify=(Button)findViewById(R.id.btn_verify);
        waitingDialog =new SpotsDialog(this);
        // waitingDialog.setMessage(getResources().getString(R.string.waiting_string));
        waitingDialog.setCancelable(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                Toast.makeText(MainActivity.this,"Verification Complete",Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                waitingDialog.dismiss();
                // Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(MainActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                Log.d("FirebaseException",e.getMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
//                    Toast.makeText(MainActivity.this,"InValid Phone Number",Toast.LENGTH_SHORT).show();
                    edtPhoneNumber.setError(getResources().getString(R.string.invalid_phone),getDrawable(R.drawable.ic_error_outline_black_24dp));
                    if (main_layout != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(main_layout.getWindowToken(), 0);
                    }
                    Snackbar.make(main_layout,getString(R.string.phone_number_format),Snackbar.LENGTH_LONG).show();

                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Log.d(TAG, "onCodeSent:" + verificationId);
                waitingDialog.dismiss();
                Toast.makeText(MainActivity.this,"Verification code has been send on your number",Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                edtPhoneNumber.setVisibility(View.GONE);
                btnSendCode.setVisibility(View.GONE);

                edtVerificationCode.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.VISIBLE);
            }
        };

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtPhoneNumber.getText().toString())){
                    waitingDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+964"+
                            edtPhoneNumber.getText().toString(),
                            60,
                            java.util.concurrent.TimeUnit.SECONDS,
                            MainActivity.this,
                            mCallbacks);
                }else{
                    waitingDialog.dismiss();
//                    Toast.makeText(MainActivity.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                    edtPhoneNumber.setError(getResources().getString(R.string.no_phone),getDrawable(R.drawable.ic_error_outline_black_24dp));
                    if (main_layout != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    Snackbar.make(main_layout,getString(R.string.phone_number_format),Snackbar.LENGTH_LONG).show();
                }

            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edtVerificationCode.getText().toString())){
                    waitingDialog.show();
                    credential = PhoneAuthProvider.getCredential(mVerificationId, edtVerificationCode.getText().toString());
                }
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential);
            }
        });

    }


    private void changeLanguageDialog() {
        final androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(this);

        dialog.setTitle(getResources().getString(R.string.choose_language_string));

        LayoutInflater inflater = LayoutInflater.from(this);
        View change_language_view = inflater.inflate(R.layout.layout_language, null);

        RadioButton enLang = (RadioButton) change_language_view.findViewById(R.id.radio_english_lng);
        RadioButton kuLang = (RadioButton) change_language_view.findViewById(R.id.radio_kurdish_lng);
        RadioButton arLang = (RadioButton) change_language_view.findViewById(R.id.radio_arabic_lng);

        enLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Paper.book().write("language", "en");
                    updateView((String) Paper.book().read("language"));
                }
            }
        });

        arLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Paper.book().write("language", "ar");
                    updateView((String) Paper.book().read("language"));
                }
            }
        });
        kuLang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Paper.book().write("language", "ku");
                    updateView((String) Paper.book().read("language"));
                }
            }
        });

        dialog.setView(change_language_view);

        dialog.setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        dialog.show();

    }

    private void updateView(String language) {
        Context context = LocalHelper.setLocale(this, language);
        Resources resources = context.getResources();

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {


        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete()) {
                            // Log.d(TAG, "signInWithCredential:success");
                            userRef =FirebaseDatabase.getInstance().getReference(Common.UserInformation);
                            final String userPhone = edtPhoneNumber.getText().toString();
                            //check if number exist in firebase
                            Log.d("TAG","inside of signInWithCredential / onComplete");

                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.child(userPhone).exists()){
                                        //if user does not exist create new user and added to database
                                        User user =new User();
                                        user.setUserPhone(userPhone);
                                        user.setUsername("");
                                        user.setUserAvatar("");
                                        user.setUserId(userPhone);
                                        user.setUserTimeStamp(ServerValue.TIMESTAMP);
                                        user.setUserPlane("FreeTrail");
                                        user.setSubscriptionDate(ServerValue.TIMESTAMP);
                                        user.setUserState("Active");
                                        user.setUserPoint("0");
                                        user.setUserContribution("0");

                                        userRef.child(edtPhoneNumber.getText().toString())
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //login
                                                        userRef.child(edtPhoneNumber.getText().toString())
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        Common.currentUser = dataSnapshot.getValue(User.class);

                                                                        Paper.book().write("currentUser", Common.currentUser);


                                                                        Intent intent =new Intent(MainActivity.this,HomeActivity.class);
                                                                        startActivity(intent);
                                                                        waitingDialog.dismiss();
                                                                        finish();
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                waitingDialog.dismiss();
                                                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }else{
                                        //if user already have an account
                                        userRef.child(userPhone)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Common.currentUser = dataSnapshot.getValue(User.class);

                                                        Paper.book().write("currentUser", Common.currentUser);

                                                        Intent intent =new Intent(MainActivity.this,HomeActivity.class);
                                                        startActivity(intent);
                                                        waitingDialog.dismiss();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(MainActivity.this,"Verification Done",Toast.LENGTH_SHORT).show();
                            // ...
                        } else {
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                waitingDialog.dismiss();
//                                Toast.makeText(MainActivity.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                                edtPhoneNumber.setError(getResources().getString(R.string.invalid_phone),getDrawable(R.drawable.ic_error_outline_black_24dp));
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
       Common.currentUser = Paper.book().read("currentUser");
        if (Common.currentUser!=null){
            userRef=FirebaseDatabase.getInstance().getReference(Common.UserInformation);
            userRef.child(Common.currentUser.getUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Common.currentUser = dataSnapshot.getValue(User.class);
                            Paper.book().write("currentUser",Common.currentUser);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }
    }

    private void printHashKey() {

            try {
                PackageInfo info = getPackageManager().getPackageInfo("com.recore.projectrecoresc"
                ,PackageManager.GET_SIGNATURES);

                for (Signature signature:info.signatures){

                    MessageDigest md =MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


    }
}
