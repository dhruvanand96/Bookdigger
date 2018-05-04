package com.example.hp.bookseller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{
    EditText edittxtname,edittxtphone,edittxtemail,edittxtusername,edittxtpassword;
    Spinner college,trait;
    Button btnsubmit;

    ArrayAdapter<String> collegeadapter,traitadapter;
    UserBean user,updateuser;
    BookSellerUtil util;

    StringRequest request;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    Boolean updatemode=false;


    // for sign up activity create shared instance of FirebaseAuth
    private FirebaseAuth mAuth;

    // firebase Database
    private DatabaseReference mUsersDatabase;

    private static final String TAG = "EmailPassword";


    public void  initviews()
    {

        edittxtname=(EditText)findViewById(R.id.edittextname);
        edittxtphone=(EditText)findViewById(R.id.edittextphone);
        edittxtemail=(EditText)findViewById(R.id.edittextemail);
        edittxtusername=(EditText)findViewById(R.id.edittextusername);
        edittxtpassword=(EditText)findViewById(R.id.edittextpassword);
        btnsubmit=(Button)findViewById(R.id.submitbutton);
    }

    public void initspinners()
    {
        college=(Spinner)findViewById(R.id.spinner_college);
        trait=(Spinner)findViewById(R.id.spinner_trait);

        collegeadapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        collegeadapter.add("Select your college");
        collegeadapter.add("Guru Nanak Dev Engineering College");
        collegeadapter.add("Gulzar Group of Institutes");
        collegeadapter.add("Bhutta College Of Engineering&Technology");
        collegeadapter.add("RIMT-School of engineering");

        college.setAdapter(collegeadapter);
        college.setOnItemSelectedListener(this);

        traitadapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        traitadapter.add("Select your Trait");
        traitadapter.add("Mechanical");
        traitadapter.add("Computer Science ");
        traitadapter.add("IT");
        traitadapter.add("Electronics & Communication");
        traitadapter.add("Electrical");
        traitadapter.add("Civil");
        traitadapter.add("Production");


        trait.setAdapter(traitadapter);
        trait.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initviews();
        initspinners();

        btnsubmit.setOnClickListener(this);



        requestQueue = Volley.newRequestQueue(this);

        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);

        Intent rcv=getIntent();
        updatemode=rcv.hasExtra("keyuser");

        if(updatemode)
        {

            updateuser=(UserBean)rcv.getSerializableExtra("keyuser");
            user=updateuser;


            edittxtname.setText(updateuser.getName());
            edittxtphone.setText(updateuser.getPhone());
            edittxtemail.setText(updateuser.getEmail());
            edittxtpassword.setText(updateuser.getPassword());
            edittxtusername.setText(updateuser.getUsername());


            for(int i=0;i<collegeadapter.getCount();i++)
            {
              if(collegeadapter.getItem(i).equals(updateuser.getCollegename())){
                  college.setSelection(i);
              }
            }

            for(int x=0;x<traitadapter.getCount();x++){
                if(traitadapter.getItem(x).equals(updateuser.getTraitname())){
                    trait.setSelection(x);
                }}

            btnsubmit.setText("Done");

            // for action bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            edittxtemail.setEnabled(false);
        }

        else
            user= new UserBean();
     }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        if(parent.getId()==R.id.spinner_college)
        {

            user.setCollegename(collegeadapter.getItem(position));

        }
        if(parent.getId()==R.id.spinner_trait)
        {
            user.setTraitname(traitadapter.getItem(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }


    @Override
    public void onClick(View v)
    {
        int id=v.getId();

        switch(id)
        {

            case R.id.submitbutton:


                user.setName(edittxtname.getText().toString().trim());
                user.setPhone(edittxtphone.getText().toString().trim());
                user.setEmail(edittxtemail.getText().toString().trim());
                user.setUsername(edittxtusername.getText().toString().trim());
                user.setPassword(edittxtpassword.getText().toString().trim());
                user.setToken(FirebaseInstanceId.getInstance().getToken());

                if(validateFields())
                {
                    if(updatemode)
                        handler.sendEmptyMessage(101);

                    else
                    {
                        createAccount();

                    }
                }
                break;
        }
    }

    void registeruser()
    {

        String url="";
        if(updatemode)
        {
            url=util.URL_UPDATE;
            progressDialog.show();
//            initDialog(RegistrationActivity.this);
        }
        else
        {
            url=util.URL_INSERT;
        }


        request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject=new JSONObject(response);
                            int success=jsonObject.getInt("success");
                            String message=jsonObject.getString("message");

                            Toast.makeText(getApplicationContext(),success+"-"+message,Toast.LENGTH_LONG).show();

                            if (updatemode)
                            {
                                Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
                                progressDialog.dismiss();
//                                dismissDialog();
                                startActivity(intent);
                                finish();
                            }
                            else
                            {

                                progressDialog.dismiss();
//                                dismissDialog();
                                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }


//                            Toast.makeText(getApplicationContext(),"USER :"+user.toString(),Toast.LENGTH_LONG).show();

                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"No Data You Provided Exists ",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
//                            dismissDialog();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(),"error "+error,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
//                        dismissDialog();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                HashMap<String,String> map= new HashMap<String,String>();
                map.put(util.KEY_NAME,user.getName());
                map.put(util.KEY_PHONE,user.getPhone());
                map.put(util.KEY_EMAIL,user.getEmail());
                map.put(util.KEY_USERNAME,user.getUsername());
                map.put(util.KEY_PASSWORD,user.getPassword());
                map.put(util.KEY_COLLEGE,user.getCollegename());
                map.put(util.KEY_TRAIT,user.getTraitname());


                if(!updatemode)
                // need to add the below element in database table modify scripts according to it
                {
                    map.put(util.KEY_UID,user.getUid());
                    map.put(util.KEY_TOKEN,user.getToken());
                }



                return map;
            }
        };

        requestQueue.add(request);

    }
    void createAccount()
    {

        progressDialog.show();
//        initDialog(RegistrationActivity.this);
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser current_user = mAuth.getCurrentUser();

                            if(current_user!=null)
                            {
                                user.setChats( new ArrayList<String>());
                                user.setUid(current_user.getUid());
                                Log.d("error", "createUserWithEmail:success"+user.toString());
                                mUsersDatabase.child(util.JSON_USER).child(user.getUid()).setValue(user);

                                handler.sendEmptyMessageDelayed(101,4000);

                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
//                            dismissDialog();

                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
//                            dismissDialog();

                        }

                    }
                });
        // [END create_user_with_email]
    }

    boolean validateFields()
    {

        boolean flag = true;

        if(user.getName().isEmpty())
        {
            edittxtname.setError("Please Enter Name");
            flag = false;
        }
        if(user.getPhone().isEmpty())
        {
            edittxtphone.setError("please enter phone");
            flag = false;
        }
        else
        {
            if(user.getPhone().length()!=10)
            {
                edittxtphone.setError("please enter a valid phone");
            }
        }

        if(user.getEmail().isEmpty())
        {
            edittxtemail.setError(" Enter Email");
            flag = false;
        }
        else
        {
            if(!user.getEmail().contains("@") && !user.getEmail().contains("."))
            {
                edittxtemail.setError(" Enter Valid Email");
                flag = false;
            }
        }

        if(user.getUsername().isEmpty())
        {
            edittxtusername.setError("Enter a username");
            flag =false;
        }

        if(user.getPassword().isEmpty())
        {
            edittxtpassword.setError("Enter Password");
            flag = false;
        }
        else
        {
            if((user.getPassword().length()<6))
            {
                edittxtpassword.setError("Password must be 6 in length" + "");
                flag = false;
            }
        }

        if(user.getCollegename()==collegeadapter.getItem(0))
        {
            Toast.makeText(this," select a college",Toast.LENGTH_LONG).show();
            flag = false;
        }

        if(user.getTraitname()==traitadapter.getItem(0))
        {
            Toast.makeText(this," select a branch",Toast.LENGTH_LONG).show();
            flag = false;
        }

        return flag;
    }

    void clearfields()
    {

        edittxtname.setText("");
        edittxtemail.setText("");
        edittxtphone.setText("");
        edittxtusername.setText("");
        edittxtpassword.setText("");
        college.setSelection(0);
        trait.setSelection(0);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            if(msg.what==101)
            {
               registeruser();
            }
        }
    };

    static Dialog dialog;

    public static Dialog initDialog(Context context)
    {
        dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setCancelable(false);
        dialog.show();
        LottieAnimationView animationView = (LottieAnimationView) dialog.findViewById(R.id.animation_view);
        animationView.setAnimation("pencil_write.json");
        animationView.loop(true);
        animationView.setProgress(0.5f);
        animationView.playAnimation();
        return dialog;
    }

    public static void dismissDialog(){
        dialog.dismiss();
    }
}
