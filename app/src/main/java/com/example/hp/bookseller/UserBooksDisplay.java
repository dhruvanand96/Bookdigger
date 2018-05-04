package com.example.hp.bookseller;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserBooksDisplay extends AppCompatActivity
{

    ProgressDialog progressDialog;

    RecyclerView mRecyclerView;
    ArrayList<BookBean>  bookitemlist;
    GridLayoutManager gridLayoutManager;
    ViewitemsAdapter viewitemsAdapter;
    BookBean bookBean;
    BookBean bookBean1;

    int arraylistPosition;


    SharedPreferences sharedPreferences;


    void init()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);



        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerviewuser);
        mRecyclerView.setHasFixedSize(true);
        gridLayoutManager= new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addOnItemTouchListener( new ViewItemClick(getApplicationContext(), new ViewItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position)
            {

                arraylistPosition=position;
                bookBean1=bookitemlist.get(arraylistPosition);
//                Toast.makeText(getApplicationContext(),"user id is "+bookBean1.toString(),Toast.LENGTH_LONG).show();
                showDialog();


            }
        }));

        bookitemlist= new ArrayList<>();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"Delete"};

        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                switch (which)
                {
                    case 0:
                        showDeleteConfirm();

                        break;
                }
            }
        });
        builder.create().show();
    }

    void showDeleteConfirm()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete: "+bookBean1.getName());
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                   deleteBook();
            }
        }
        );

        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }

    void deleteBook()
    {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BookSellerUtil.URL_DELETE_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    if(success==1)
                    {
                        Toast.makeText(getApplicationContext(), "Book Deleted ", Toast.LENGTH_LONG).show();
                        bookitemlist.remove(arraylistPosition);
                        viewitemsAdapter.notifyDataSetChanged();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Item Not Deleted " + response, Toast.LENGTH_LONG).show();

                    progressDialog.dismiss();
                }
                catch (JSONException e)
                {
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
                        Toast.makeText(getApplicationContext(), "Error  " + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(BookSellerUtil.KEY_ITEM_ID,String.valueOf(bookBean1.getId()));
                return map;

            }


        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    void retrieveAllItems()
    {
//        progressDialog.show();
        initDialog(UserBooksDisplay.this);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BookSellerUtil.URL_RETRIEVE_USERITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");


//                  Toast.makeText(getApplicationContext(),"JSON DATA  "+response,Toast.LENGTH_LONG).show();

                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++)
                        {

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
//                            progressDialog.dismiss();
                            dismissDialog();
                        }
                    }

                    viewitemsAdapter = new ViewitemsAdapter(getApplicationContext(), R.layout.activity_book, bookitemlist);
                    mRecyclerView.setAdapter(viewitemsAdapter);

                } catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), "Exception " + e+" response "+response, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "Error " + error, Toast.LENGTH_LONG).show();
//                        progressDialog.dismiss();
                        dismissDialog();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(BookSellerUtil.KEY_USERNAME,sharedPreferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,""));

                return map;

            }


        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        init();

        sharedPreferences=getSharedPreferences(BookSellerUtil.SHAREDPREFS_FILENAME,MODE_PRIVATE);
        // Toast.makeText(getApplicationContext(),"Share Prefrences Name "+sharedPreferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,""),Toast.LENGTH_LONG).show();

        retrieveAllItems();
    }


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
