package com.example.hp.bookseller;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewBookDetails extends AppCompatActivity implements View.OnClickListener
{

    /*
    In this after clicking main activity data of books is viewed.

    */
    ImageView imgbook;
    TextView txtviewname,txtviewcondition,txtviewprice,txtviewauthor,txtviewpublisher,txtviewtype;
    Button buttoncall;
    BookBean bookBean;
    Picasso picasso;
    Context context;

    UserBean rs;

    ProgressDialog progressDialog;


    void initviews()
    {
        imgbook=(ImageView)findViewById(R.id.BImage);
        txtviewname=(TextView)findViewById(R.id.BName);
        txtviewauthor=(TextView)findViewById(R.id.BAuthor);
        txtviewpublisher=(TextView)findViewById(R.id.BPublisher);
        txtviewprice=(TextView)findViewById(R.id.BPrice);
        txtviewcondition=(TextView)findViewById(R.id.BCondition);
        txtviewtype=(TextView)findViewById(R.id.BType);
        buttoncall=(Button)findViewById(R.id.buttoncall);


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);

        buttoncall.setOnClickListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        initviews();

        Intent rcv=getIntent();
        bookBean= (BookBean) rcv.getSerializableExtra("Key_bookbean");
        picasso.with(context).load(bookBean.getImage()).into(imgbook);
        txtviewname.setText(bookBean.getName());
        txtviewauthor.setText("By "+bookBean.getAuthor());
        txtviewpublisher.setText(bookBean.getPublisher());
        txtviewprice.setText("\u20B9"+bookBean.getPrice());
        txtviewcondition.setText(bookBean.getCondition());
        txtviewtype.setText(bookBean.getTrait());

        rs = new UserBean();

        retrieveReciverDetails();


        // for action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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


    void retrieveReciverDetails()
    {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST , BookSellerUtil.URL_RETRIEVE_PHONENO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

//                    Toast.makeText(Favourite.this,"JSON DATA  "+response,Toast.LENGTH_LONG).show();
                    if (jsonArray != null)
                    {
                        for (int i = 0; i < jsonArray.length(); i++)
                        {

                            JSONObject jObj = jsonArray.getJSONObject(i);
                            rs.setName(jObj.getString(BookSellerUtil.KEY_NAME));
                            rs.setPhone(jObj.getString(BookSellerUtil.KEY_PHONE));
                            rs.setEmail(jObj.getString(BookSellerUtil.KEY_EMAIL));
                            rs.setToken(jObj.getString(BookSellerUtil.KEY_TOKEN));
                            rs.setUid(jObj.getString(BookSellerUtil.KEY_UID));

//                            Toast.makeText(getApplicationContext(), "Reciver "+rs.toString(), Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();
                        }

                    }

                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), "Data Doesnt Exist" + e, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(), "Error " + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(BookSellerUtil.KEY_USERNAME,bookBean.getUserName());


                return map;

            }

        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.buttoncall:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Posted By \n"+rs.getName());
                builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    String call1 = rs.getPhone();
                    phoneIntent.setData(Uri.parse("tel:" + call1));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(phoneIntent);
                }
            });
                builder.setNegativeButton("Send Message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //                Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ViewBookDetails.this,Chat.class);
                        intent.putExtra("reciver",rs);
                        startActivity(intent);
                        Log.i("Reciver Info ",rs.toString());

                    }
                });
                builder.create().show();


        }


    }
}
