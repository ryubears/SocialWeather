package com.example.android.socialweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SocialActivity extends AppCompatActivity {
    @BindView(R.id.social_id_text_view) TextView mIdTextView;
    @BindView(R.id.social_info_text_view) TextView mInfoTextView;
    @BindView(R.id.social_logout_button) Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        ButterKnife.bind(this);

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                //get account kit id
                String accountKitId = account.getId();
                mIdTextView.setText(accountKitId);

                PhoneNumber phoneNumber = account.getPhoneNumber();
                if(phoneNumber != null) {
                    //if phone number is available, display it
                    String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
                    mInfoTextView.setText(formattedPhoneNumber);
                } else {
                    //if email address is available, display it
                    String emailString = account.getEmail();
                    mInfoTextView.setText(emailString);
                }
            }

            @Override
            public void onError(AccountKitError accountKitError) {
                //display error message
                String toastMessage = accountKitError.getErrorType().getMessage();
                Toast.makeText(SocialActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.social_logout_button)
    public void onLogout() {
        //logout from current account
        AccountKit.logOut();
        launchLoginActivity();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //method for formatting phone numbers
    private String formatPhoneNumber(String phoneNumber) {
        try {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
            phoneNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }
}
