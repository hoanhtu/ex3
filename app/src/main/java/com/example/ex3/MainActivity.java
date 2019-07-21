package com.example.ex3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ex3.Common.Common;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    private final static int REQUEST_CODE=2810;
    //Button phoneLogin, emailLogin;
    //TextView txtSkip;

    @BindView(R.id.phonelogin)
    Button btn_phoneLogin;
    @BindView(R.id.emaillogin)
    Button btn_emailLogin;
    @OnClick(R.id.phonelogin)
    void LoginWithPhone()
    {
        startLoginPage(LoginType.PHONE);
    }
    @OnClick(R.id.emaillogin)
    void LoginWithEmail()
    {
        startLoginPage(LoginType.EMAIL);
    }

    @BindView(R.id.skip)
    TextView txtSkip;
    @OnClick(R.id.skip)
    void skipLoginJustGoHome()
    {
        Intent intent=new Intent(this, Success.class);
        intent.putExtra(Common.IS_LOGIN,false);
        startActivity(intent);
    }


    //Button login =findViewById(R.id.login);



   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //phoneLogin=(Button)findViewById(R.id.phonelogin);
        //emailLogin=(Button)findViewById(R.id.emaillogin);

       AccessToken accessToken= AccountKit.getCurrentAccessToken();
       if(accessToken!=null)
       {
           Intent intent=new Intent(this, Success.class);
           intent.putExtra(Common.IS_LOGIN,true);
           startActivity(intent);

           finish();
       }

       else
       {
           setContentView(R.layout.activity_main);
           ButterKnife.bind(MainActivity.this);
       }


//        emailLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startLoginPage(LoginType.EMAIL);
//
//            }
//        });
//       phoneLogin.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               startLoginPage(LoginType.PHONE);
//
//           }
//       });


        //prinKeyHash();

    }

    private void startLoginPage(LoginType loginType) {

        if(loginType==LoginType.EMAIL) {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.EMAIL,
                            AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
            // ... perform additional configuration ...
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        }
        else if(loginType==LoginType.PHONE)
        {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.PHONE,
                            AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
            // ... perform additional configuration ...
            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE)
        {
            AccountKitLoginResult result=data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(result.getError()!=null)
            {
                Toast.makeText(this,""+result.getError().getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                return;

            }
            else if (result.wasCancelled())
            {
                Toast.makeText(this,"cancel",Toast.LENGTH_SHORT).show();
            }

            else
            {
                Toast.makeText(this,"success ! %s"+result.getAuthorizationCode().substring(0,10),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this, Success.class);
                intent.putExtra(Common.IS_LOGIN,true);
                startActivity(intent);

                finish();
            }
        }
    }

    private void prinKeyHash() {

        try{
            PackageInfo packageInfo=getPackageManager().getPackageInfo(
                    "com.example.ex3",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature: packageInfo.signatures)
            {
                MessageDigest md=MessageDigest.getInstance("SHA");
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
