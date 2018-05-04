package com.example.hp.bookseller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    StringRequest Stringrequest;
    RequestQueue requestQueue;
    BookSellerUtil util;

    EditText edittxtusername,edittxtpassword;
    Button btnlogin;
    TextView txtregisteractivity;
    String username,password;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog dialog;


    void initviews()
    {
        edittxtusername=(EditText)findViewById(R.id.edittext_username);
        edittxtpassword=(EditText)findViewById(R.id.edittext_password);
        btnlogin=(Button)findViewById(R.id.buttonlogin);
        txtregisteractivity=(TextView) findViewById(R.id.textview_registeractivity);

        btnlogin.setOnClickListener(this);
    }


    boolean validateFields()
    {

        boolean flag = true;


        if(username.equals(""))
        {
            edittxtusername.setError("Enter Email Id");
            flag = false;
        }
        else
        {
            if(!username.contains("@") && !username.contains("."))
            {
                edittxtusername.setError("Enter Valid Email");
                flag = false;
            }
        }


        if(password.equals(""))
        {
            edittxtpassword.setError("Enter Password");
            flag = false;
        }

        return flag;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initviews();
        btnlogin.setOnClickListener(this);

        requestQueue= Volley.newRequestQueue(this);

        // firebase variable Intializing
        mAuth = FirebaseAuth.getInstance();


        dialog=new ProgressDialog(this);
        dialog.setMessage("please wait");
        dialog.setCancelable(false);

        sharedPreferences=getSharedPreferences(BookSellerUtil.SHAREDPREFS_FILENAME,MODE_PRIVATE);
        editor=sharedPreferences.edit();

    }

    void logincheck()
    {



        Stringrequest= new StringRequest(Request.Method.POST, util.URL_LOGINCHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        try
                        {
                            JSONObject jsonobject=new JSONObject(response);
                            int success= jsonobject.getInt("success");
                            String message=jsonobject.getString("message");
                            signInFireBase();


                            if(success==1)
                            {

                                Intent intentmain= new Intent(LoginActivity.this,MainActivity.class);

                                editor.putBoolean(util.SHAREDPREFS_LOGINFLAG,true);
                                editor.putString(util.SHAREDPREFS_KEYEMAIL,username);
                                editor.putString(util.SHAREDPREFS_SENDERPASSWORD,password);
                                editor.putString(util.SHAREDPREFS_SENDERTOKEN, FirebaseInstanceId.getInstance().getToken());
                                editor.commit();

                                dialog.dismiss();

                                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();


                                handler.sendEmptyMessageDelayed(101,6000);

                                startActivity(intentmain);

                            }
                            else
                            {
                                clearfields();
                                dialog.dismiss();

                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(LoginActivity.this,"exception "+e,Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this,"error is" +error,Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                HashMap<String,String> map= new HashMap<String, String>();
                map.put("USERNAME",username);
                map.put("PASSWORD",password);
                map.put(BookSellerUtil.KEY_TOKEN, FirebaseInstanceId.getInstance().getToken());
                return map;
            }
        };

        requestQueue.add(Stringrequest);
    }


    void signInFireBase()
    {
        dialog.show();

        mAuth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            handler.sendEmptyMessage(100);

                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    @Override
    public void onClick(View v)
    {

        int id=v.getId();

        if(id==R.id.buttonlogin)
        {

            username=edittxtusername.getText().toString().trim();
            password=edittxtpassword.getText().toString().trim();

            if(validateFields())
            {
                signInFireBase();
                logincheck();
            }




        }

    }

    public void registerClickHandler(View view)
    {
        Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    void clearfields()
    {
        edittxtusername.setText("");
        edittxtpassword.setText("");
    }

     @SuppressLint("HandlerLeak")
     Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            if(msg.what==101)
            {
                finish();
            }
            if(msg.what==100)
            {
                updateFirebaseToken();
            }
        }
    };

    DatabaseReference mFirebaseSetting;

    void updateFirebaseToken()
    {
        mFirebaseSetting = FirebaseDatabase.getInstance().getReference();

        mFirebaseSetting.child(BookSellerUtil.JSON_USER).child(mAuth.getCurrentUser().getUid()).child("token").setValue(FirebaseInstanceId.getInstance().getToken());

//        Toast.makeText(getApplicationContext(),"hello "+mAuth.getCurrentUser().getUid(),Toast.LENGTH_LONG).show();


    }
}
