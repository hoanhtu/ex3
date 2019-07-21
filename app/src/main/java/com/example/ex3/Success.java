package com.example.ex3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ex3.Common.Common;
import com.example.ex3.Fragments.HomeFragment;
import com.example.ex3.Fragments.ShoppingFragment;
import com.example.ex3.Model.User;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

import is.arontibo.library.ElasticDownloadView;

public class Success extends AppCompatActivity {


    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    BottomSheetDialog bottomSheetDialog;
    CollectionReference userRef;


    //@BindView(R.id.elastic_download_view) ElasticDownloadView mElasticDownloadView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        System.out.println("1");


        ButterKnife.bind(this);

        userRef= FirebaseFirestore.getInstance().collection("User");




        if(getIntent()!=null)
        {
            boolean isLogin=getIntent().getBooleanExtra(Common.IS_LOGIN,false);
            if(isLogin)
            {

                //mElasticDownloadView.startIntro();
                //mElasticDownloadView.setProgress(100);
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {

                    @Override
                    public void onSuccess(Account account) {
                        System.out.println("2");

                        if(account!=null)
                        {


                            //mElasticDownloadView.success();


                            System.out.println("3");
                            DocumentReference currenUser=userRef.document(account.getPhoneNumber().toString());

                            currenUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot userSnapShot=task.getResult();
                                        if(!userSnapShot.exists())
                                        {
                                            System.out.println("4");


                                            showUpdateDialog(account.getPhoneNumber().toString());
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        //mElasticDownloadView.fail();
                        Toast.makeText(Success.this, ""+accountKitError.getErrorType().getMessage(),Toast.LENGTH_SHORT).show();

                        System.out.println(accountKitError.getErrorType().getMessage());
                    }
                });
            }

        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId()==R.id.action_home)
                    fragment=new HomeFragment();
                else if(menuItem.getItemId()==R.id.action_shopping)
                    fragment=new ShoppingFragment();
                return loadFragment(fragment);
            }
        });




    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment!=null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            return true;
        }
        return false;
    }

    private void showUpdateDialog(String phoneNumber) {
        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        System.out.println("3");
        View sheetView=getLayoutInflater().inflate(R.layout.layout_update_information,null);

        System.out.println("4");
        Button btn_update=(Button)sheetView.findViewById(R.id.btn_update);
        TextInputEditText edt_name=sheetView.findViewById(R.id.edt_name);
        TextInputEditText edt_address=sheetView.findViewById(R.id.edt_address);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user =new User(edt_name.getText().toString(),edt_address.getText().toString(),phoneNumber);
                userRef.document(phoneNumber)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Success.this, "thank you ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        bottomSheetDialog.dismiss();
                        Toast.makeText(Success.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        //System.out.println(e.getMessage());
                    }
                });

            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();



    }


}
