package com.gzeinnumer.otpview.otpV2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.gzeinnumer.otpview.databinding.ActivityOTPV2Binding;
import com.gzeinnumer.otpview.service.TimerService;
import com.gzeinnumer.stw.SimpleTextWatcher;

public class OTPV2Activity extends AppCompatActivity {

    public static final String RESULT = "result";
    public static final String TITLE = "title";
    public static final String TYPE = "Type";
    public static final String EMAIL = "email";

    private ActivityOTPV2Binding binding;
    private OTPV2VM vm;
    private Intent intent;

    private String title;
    private String type;
    private String email;

    private final BroadcastReceiver timeTikBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(TimerService.BROADCAST_TIME_TIK_ACTION)) {
                long time = intent.getLongExtra(TimerService.BROADCAST_TIME_TIK_DATA, 0);
                if (time>0){
                    binding.tvCounter.setText("Kirim Ulang (" + time + ")");
                } else {
                    binding.tvCounter.setText("Kirim Ulang");
                }
                binding.tvCounter.setEnabled(time <= 0);
            }
            else {
                binding.tvCounter.setEnabled(true);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(timeTikBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_TIME_TIK_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(timeTikBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOTPV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(OTPV2VM.class);

        startService(new Intent(this, TimerService.class));

        initView();
        initTextWatcher();
        initObserver();
        initOnClick();
    }

    private void initView() {
        intent = getIntent();

        title = intent.getStringExtra(TITLE);
        type = intent.getStringExtra(TYPE);
        email = intent.getStringExtra(EMAIL);

        binding.tvToolbar.setText(title);
    }

    boolean isFill = false;

    private void initTextWatcher() {
        binding.ed1.addTextChangedListener(new SimpleTextWatcher(s -> {
            onHideView(binding.tvError);
            if (s.toString().length()>0) binding.ed2.requestFocus();
        }));
        binding.ed2.addTextChangedListener(new SimpleTextWatcher(s -> {
            onHideView(binding.tvError);
            if (s.toString().length()>0) binding.ed3.requestFocus();
            else binding.ed1.requestFocus();
        }));
        binding.ed3.addTextChangedListener(new SimpleTextWatcher(s -> {
            onHideView(binding.tvError);
            if (s.toString().length()>0) binding.ed4.requestFocus();
            else binding.ed2.requestFocus();
        }));
        binding.ed4.addTextChangedListener(new SimpleTextWatcher(s -> {
            onHideView(binding.tvError);
            if (s.toString().length()>0){
                isFill = true;
            } else {
                isFill =  false;
                binding.ed3.requestFocus();
            }
        }));
    }

    private void onHideView(View view) {
        view.setVisibility(View.GONE);
    }

    private void initObserver() {

    }

    private void actionAfterOTPSuccess() {
        Intent intent = new Intent();
        intent.putExtra(RESULT, "1");
        intent.putExtra(TITLE, title);
        intent.putExtra(TYPE, type);
        intent.putExtra(EMAIL, email);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void initOnClick() {
        binding.btnOk.setOnClickListener(view -> {
            if (isFill){
                binding.tvError.setVisibility(View.GONE);
                validate();
            } else {
                binding.tvError.setVisibility(View.VISIBLE);
            }
        });
        binding.tvCounter.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "kirim OTP Lagi", Toast.LENGTH_SHORT).show();
        });
    }

    private void validate() {
        String otp;
        otp = binding.ed1.getText().toString();
        otp += binding.ed2.getText().toString();
        otp += binding.ed3.getText().toString();
        otp += binding.ed4.getText().toString();
        if (otp.length() == 4) {
            Toast.makeText(getApplicationContext(), "OTP Divalidasi", Toast.LENGTH_SHORT).show();

            actionAfterOTPSuccess();
        } else {
            binding.tvError.setVisibility(View.VISIBLE);
        }
    }
}