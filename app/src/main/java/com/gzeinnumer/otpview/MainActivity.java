package com.gzeinnumer.otpview;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.gzeinnumer.otpview.databinding.ActivityMainBinding;
import com.gzeinnumer.otpview.ui.otpV2.OTPV2Activity;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findViewById(R.id.btn_otp).setOnClickListener(v -> {
            intentToOTP();
        });
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    setStatus(data);
                }
            });

    private void setStatus(Intent data) {
        String message = data.getStringExtra(OTPV2Activity.RESULT);
        if (message.equals("1")){
            Toast.makeText(getApplicationContext(), "Pindah ke new activity", Toast.LENGTH_SHORT).show();
        }
    }

    private void intentToOTP() {
        Intent i = new Intent(getApplicationContext(), OTPV2Activity.class);
        i.putExtra(OTPV2Activity.TITLE, "OTP GANTI");
        i.putExtra(OTPV2Activity.EMAIL, "Email");
        i.putExtra(OTPV2Activity.TYPE, "1"); //from email

        someActivityResultLauncher.launch(i);
    }
}