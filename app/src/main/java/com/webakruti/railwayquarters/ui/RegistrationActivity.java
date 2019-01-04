package com.webakruti.railwayquarters.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.webakruti.railwayquarters.R;
import com.webakruti.railwayquarters.model.OTPResponse;
import com.webakruti.railwayquarters.model.RegistrationResponse;
import com.webakruti.railwayquarters.retrofit.ApiConstants;
import com.webakruti.railwayquarters.retrofit.service.RestClient;
import com.webakruti.railwayquarters.utils.NetworkUtil;
import com.webakruti.railwayquarters.utils.SharedPreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextName;
    private EditText editTextMobileNumber;
    private Button buttonRegister;
   // private ImageView imageViewBack;
    private LinearLayout linearLayoutGotoLogin;
    private ProgressDialog progressDialogForAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        SharedPreferenceManager.setApplicationContext(RegistrationActivity.this);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextMobileNumber = (EditText) findViewById(R.id.editTextMobileNumber);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        linearLayoutGotoLogin = (LinearLayout) findViewById(R.id.linearLayoutGotoLogin);
        linearLayoutGotoLogin.setOnClickListener(this);

        /*imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        buttonRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonRegister:
                if (editTextName.getText().toString().length() > 0) {
                    if (editTextMobileNumber.getText().toString().length() > 0) {
                        if (editTextMobileNumber.getText().toString().length() == 10) {
                            /*Intent intent = new Intent(RegistrationActivity.this, OtpActivity.class);
                            startActivity(intent);
                            finish();*/
                            if (NetworkUtil.hasConnectivity(RegistrationActivity.this)) {
                                callRegistartionAPI();
                            } else {
                                Toast.makeText(RegistrationActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Mobile number must be valid", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Mobile number Can't be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Name Can't be empty", Toast.LENGTH_SHORT).show();

                }

                break;

            case R.id.linearLayoutGotoLogin:
                Intent intent2 = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();

                break;


        }
    }


    private void callRegistartionAPI() {

        progressDialogForAPI = new ProgressDialog(this);
        progressDialogForAPI.setCancelable(false);
        progressDialogForAPI.setIndeterminate(true);
        progressDialogForAPI.setMessage("Please wait...");
        progressDialogForAPI.show();


        Call<RegistrationResponse> requestCallback = RestClient.getApiService(ApiConstants.BASE_URL).registration(editTextName.getText().toString(), editTextMobileNumber.getText().toString(), editTextMobileNumber.getText().toString());
        requestCallback.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.code() == 200) {

                    RegistrationResponse result = response.body();
                    if (result.getError() == null && result.getSuccess() != null) {
                        if (result.getSuccess().getStatus()) {

                            if (NetworkUtil.hasConnectivity(RegistrationActivity.this)) {
                                callOTPApi();
                            } else {
                                Toast.makeText(RegistrationActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                            }


                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Response code is 401
                    Toast.makeText(RegistrationActivity.this, "Unauthorized User", Toast.LENGTH_SHORT).show();
                }

                if (progressDialogForAPI != null) {
                    progressDialogForAPI.cancel();
                }
            }


            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {

                if (t != null) {

                    if (progressDialogForAPI != null) {
                        progressDialogForAPI.cancel();
                    }
                    if (t.getMessage() != null)
                        Log.e("error", t.getMessage());
                }

            }
        });


    }

    private void callOTPApi() {

        Call<OTPResponse> requestCallback = RestClient.getApiService(ApiConstants.BASE_URL).otpVerification(editTextMobileNumber.getText().toString());
        requestCallback.enqueue(new Callback<OTPResponse>() {
            @Override
            public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.code() == 200) {

                    OTPResponse result = response.body();
                    if (result.getSuccess().getStatus()) {

                        Intent intent = new Intent(RegistrationActivity.this, OtpActivity.class);
                        intent.putExtra("MOBILE_NO", editTextMobileNumber.getText().toString());
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(RegistrationActivity.this, "OTP Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Response code is 401
                    Toast.makeText(RegistrationActivity.this, "OTP Error", Toast.LENGTH_SHORT).show();
                }

                if (progressDialogForAPI != null) {
                    progressDialogForAPI.cancel();
                }
            }


            @Override
            public void onFailure(Call<OTPResponse> call, Throwable t) {

                if (t != null) {

                    if (progressDialogForAPI != null) {
                        progressDialogForAPI.cancel();
                    }
                    if (t.getMessage() != null)
                        Log.e("error", t.getMessage());
                }

            }
        });


    }


}
