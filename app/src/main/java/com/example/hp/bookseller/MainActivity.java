package com.example.hp.bookseller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    BookSellerUtil util;
    static MainActivity m;
    UserBean user1;
    BookBean bookBean;
    StringRequest stringRequest,stringRequest1;
    RequestQueue requestQueue;
    public static MainActivity getM(){
        return m;
    }

    String TAG="HELLO";

    ProgressDialog progressDialog;

    RecyclerView mRecyclerView;
    ArrayList<BookBean>  bookitemlist;
    GridLayoutManager gridLayoutManager;
    ViewitemsAdapter viewitemsAdapter;

    SharedPreferences sharedPreferences;
    TextView navnameTextView,navemailTextView;

    void init()
    {
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerviewall);
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager= new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(viewitemsAdapter);
        mRecyclerView.addOnItemTouchListener( new ViewItemClick(MainActivity.this, new ViewItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position)
            {
                BookBean book=bookitemlist.get(position);
                Intent intent= new Intent(MainActivity.this,ViewBookDetails.class);
                intent.putExtra("Key_bookbean",book);
                //       Toast.makeText(getApplicationContext(),book.toString(),Toast.LENGTH_LONG).show();
                startActivity(intent);

            }
        }));

        requestQueue=Volley.newRequestQueue(this);
        bookitemlist= new ArrayList<>();
        sharedPreferences = getSharedPreferences(BookSellerUtil.SHAREDPREFS_FILENAME,MODE_PRIVATE);

    }

    void retrieveAllItems()
    {
//        progressDialog.show();

        stringRequest = new StringRequest(Request.Method.POST, BookSellerUtil.URL_RETRIEVE_ALLITEMS,new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    String message = jsonObject.getString("message");
//                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    if (jsonArray != null)
                    {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jObj = jsonArray.getJSONObject(i);
                            bookBean = new BookBean();
                            bookBean.setId(jObj.getInt(BookSellerUtil.KEY_ITEM_ID));
                            bookBean.setName(jObj.getString(BookSellerUtil.KEY_ITEM_NAME));
                            bookBean.setImage(jObj.getString(BookSellerUtil.KEY_ITEM_IMAGE));
                            bookBean.setPublisher(jObj.getString(BookSellerUtil.KEY_ITEM_PUBLISHER));
                            bookBean.setAuthor(jObj.getString(BookSellerUtil.KEY_ITEM_AUTHOR));
                            bookBean.setPrice(jObj.getString(BookSellerUtil.KEY_ITEM_PRICE));
                            bookBean.setCondition(jObj.getString(BookSellerUtil.KEY_ITEM_CONDITION));
                            bookBean.setUserName(jObj.getString(BookSellerUtil.KEY_USERNAME));
                            bookBean.setTrait(jObj.getString(BookSellerUtil.KEY_ITEM_TRAIT));

                            bookitemlist.add(bookBean);

                        }

                    }

                    viewitemsAdapter = new ViewitemsAdapter(getApplicationContext(), R.layout.content_main, bookitemlist);
                    mRecyclerView.setAdapter(viewitemsAdapter);
//                    progressDialog.dismiss();
                    dismissDialog();

                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(),"Exception "+e ,Toast.LENGTH_LONG).show();
                    e.printStackTrace();
//                    progressDialog.dismiss();
                    dismissDialog();
                }

            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getApplicationContext(),"Error "+error,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
//                        dismissDialog();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                HashMap<String,String> map= new HashMap<String,String>();
                String email=sharedPreferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,"");
                map.put(BookSellerUtil.KEY_EMAIL,email);

                return map;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the RequestQueue.
        //   requestQueue.add(stringRequest);
    }

    void retrieveuser()
    {

//        progressDialog.show();
        initDialog(MainActivity.this);


        stringRequest1=new StringRequest(Request.Method.POST, util.URL_RETRIEVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");

                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            int id=0;
                            String N="",E="",P="",U="",PW="",C="",T="";

                            for(int i=0;i<jsonArray.length();i++){

                                JSONObject jObj = jsonArray.getJSONObject(i);

                                id = jObj.getInt(util.KEY_ID);
                                N = jObj.getString(util.KEY_NAME);
                                E = sharedPreferences.getString(util.SHAREDPREFS_KEYEMAIL,"");
                                P = jObj.getString(util.KEY_PHONE);
                                U = jObj.getString(util.KEY_USERNAME);
                                PW= jObj.getString(util.KEY_PASSWORD);
                                C=jObj.getString(util.KEY_COLLEGE);
                                T=jObj.getString(util.KEY_TRAIT);

                                user1 = new UserBean(id,N,P,E,U,PW,C,T,"","");
//                                Toast.makeText(MainActivity.this,user1.toString(),Toast.LENGTH_LONG).show();
                                String name= user1.getName();
                                navnameTextView.setText(""+name);

//                                progressDialog.dismiss();
//                                dismissDialog();

                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"No Data In The Table",Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
                            dismissDialog();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(),"error is"+error,Toast.LENGTH_LONG).show();
//                progressDialog.dismiss();
                dismissDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> map= new HashMap<String,String>();
                map.put(util.KEY_USERNAME,sharedPreferences.getString(util.SHAREDPREFS_KEYEMAIL,""));

                return map;
            }
        };

        //  requestQueue.add(stringRequest);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(),InsertBook.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        retrieveuser();
        retrieveAllItems();

        addToRequestQueue(stringRequest1,"h");
        addToRequestQueue(stringRequest,"h");



        m=this;

//        Toast.makeText(getApplicationContext(),sharedPreferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,""),Toast.LENGTH_LONG).show();

        View header =navigationView.getHeaderView(0);

        navnameTextView=(TextView)header.findViewById(R.id.nav_name);
        navemailTextView=(TextView)header.findViewById(R.id.nav_email);


        navemailTextView.setText(sharedPreferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,""));
//        String name= user1.getName();
//      navnameTextView.setText(""+name);

    }

    public <T> void addToRequestQueue(com.android.volley.Request<T> req, String tag)
    {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        requestQueue.add(req);
    }


    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch(id)
        {
            case R.id.nav_userprofile:
                intent=new Intent(MainActivity.this,ViewUserDetails.class);
                UserBean temp = user1;
                intent.putExtra("userdetails",temp);
                startActivity(intent);
                break;
            case R.id.nav_bookslist:
                intent= new Intent(MainActivity.this,UserBooksDisplay.class);
                startActivity(intent);
                break;
            case R.id.nav_chat:
                intent= new Intent(MainActivity.this,ChatRoom.class);
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                String [] to ={"tanyaanand.anand@gmail.com","dhruvanand.anand@gmail.com"};
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setData(Uri.parse("mailto:"));
                email.setType("text/plain");
                email.putExtra(Intent.EXTRA_CC,to);
                email.putExtra(Intent.EXTRA_SUBJECT,"Feedback/Query From BOOK DIG user");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                break;

            case R.id.menu_logout:
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("logout");
                dialog.setMessage("Are you Sure Want to Logout?");
                dialog.setPositiveButton("yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences preferences=getSharedPreferences(util.SHAREDPREFS_FILENAME,MODE_PRIVATE);
                                SharedPreferences.Editor editor= preferences.edit();
                                editor.putBoolean(util.SHAREDPREFS_LOGINFLAG,false);
                                editor.remove(util.SHAREDPREFS_KEYEMAIL);
                                editor.remove(util.SHAREDPREFS_SENDERPASSWORD);
                                editor.remove(util.SHAREDPREFS_SENDERTOKEN);
                                editor.apply();
                                editor.commit();

                                FirebaseAuth.getInstance().signOut();


                                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                );
                dialog.setNegativeButton("No",null);
                dialog.create().show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem search= menu.findItem( R.id.search);
        SearchView searchView=(SearchView)MenuItemCompat.getActionView(search);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener(){
                    @Override
                    public boolean onQueryTextSubmit(String query) {
//                        viewitemsAdapter.filter(query);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewitemsAdapter.filter(newText);
                        return false;
                    }
                }
        );
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//
//        switch (item.getItemId())
//        {
//            case R.id.search:
//
//
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

static Dialog dialog;

    public static Dialog initDialog(Context context)
    {
        dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setCancelable(false);
        dialog.show();
        LottieAnimationView animationView = (LottieAnimationView)dialog.findViewById(R.id.animation_view);
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