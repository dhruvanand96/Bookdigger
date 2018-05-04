package com.example.hp.bookseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewUserDetails extends AppCompatActivity
{

    TextView textViewemail,textViewphone,textViewusername,textViewcollege,textViewtrait;
    UserBean user;
    BookSellerUtil util;
    SharedPreferences sharedPreferences;

   // StringRequest stringRequest;
   //    RequestQueue requestQueue;

    ProgressDialog dialog;


    void initviews()
    {

        textViewemail=(TextView)findViewById(R.id.textview_email);
        textViewphone=(TextView)findViewById(R.id.textview_phone);
        textViewusername=(TextView)findViewById(R.id.textview_username);
        textViewcollege=(TextView)findViewById(R.id.textview_college);
        textViewtrait=(TextView)findViewById(R.id.textview_trait);

        sharedPreferences=getSharedPreferences(BookSellerUtil.SHAREDPREFS_FILENAME,MODE_PRIVATE);

        dialog=new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        //requestQueue= Volley.newRequestQueue(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.userdetails_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.userdetails_update:
                Intent intent=new Intent(ViewUserDetails.this,RegistrationActivity.class);
                intent.putExtra("keyuser",user);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetails);
        initviews();

        Intent rcv=getIntent();
        user=(UserBean) rcv.getSerializableExtra("userdetails");
        Log.d("USER DETAiLS",user.toString());

        textViewemail.setText(user.getEmail());
        textViewphone.setText(user.getPhone());
        textViewusername.setText(user.getName());
        textViewcollege.setText(user.getCollegename());
        textViewtrait.setText(user.getTraitname());


    }

//    void retrieveuser()
//    {
//
//        progressDialog.show();
//
//
//        stringRequest=new StringRequest(Request.Method.POST, util.URL_RETRIEVE,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            int success = jsonObject.getInt("success");
//                            String message = jsonObject.getString("message");
//
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");
//
//                            int id=0;
//                            String N="",E="",P="",U="",PW="",C="",T="";
//
//                            for(int i=0;i<jsonArray.length();i++){
//
//                                JSONObject jObj = jsonArray.getJSONObject(i);
//
//                                id = jObj.getInt(util.KEY_ID);
//                                N = jObj.getString(util.KEY_NAME);
//                                E = jObj.getString(util.KEY_EMAIL);
//                                P = jObj.getString(util.KEY_PHONE);
//                                U = jObj.getString(util.KEY_USERNAME);
//                                PW= jObj.getString(util.KEY_PASSWORD);
//                                C=jObj.getString(util.KEY_COLLEGE);
//                                T=jObj.getString(util.KEY_TRAIT);
//
//                                user = new UserBean(id,N,P,E,U,PW,C,T);
//
//
//                                textViewemail.setText(user.getEmail());
//                                textViewphone.setText(user.getPhone());
//                                textViewusername.setText(user.getName());
//                                textViewcollege.setText(user.getCollegename());
//                                textViewtrait.setText(user.getTraitname());
//
//                                progressDialog.dismiss();
//
//                            }
//                        }
//                        catch (Exception e)
//                        {
//                            Toast.makeText(getApplicationContext(),"No Data In The Table",Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//                Toast.makeText(getApplicationContext(),"error is"+error,Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
//            }
//        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                HashMap<String,String> map= new HashMap<String,String>();
//                map.put(util.KEY_USERNAME,sharedPreferences.getString(util.SHAREDPREFS_KEYEMAIL,"NA"));
//
//                return map;
//            }
//        };
//
//        requestQueue.add(stringRequest);
//    }
}
