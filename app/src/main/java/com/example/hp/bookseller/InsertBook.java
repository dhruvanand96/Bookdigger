package com.example.hp.bookseller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InsertBook extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener,CompoundButton.OnCheckedChangeListener
{
    @InjectView(R.id.BName)
    EditText BName;

    @InjectView(R.id.BAuthor)
    EditText BAuthor;

    @InjectView(R.id.BPublisher)
    EditText BPublisher;

    @InjectView(R.id.BPrice)
    EditText BPrice;

    @InjectView(R.id.ConditionNew)
    RadioButton ConditionNew;

    @InjectView(R.id.ConditionOld)
    RadioButton ConditionOld;

    @InjectView(R.id.BImage)
    ImageView BImage;

    @InjectView(R.id.BInsert)
    Button BInsert;

    @InjectView(R.id.spinner_trait1)
    Spinner trait;



    BookBean bean;
    StringRequest request;
    RequestQueue requestQueue;

    ProgressDialog dialog;
    SharedPreferences sharedPreferences;
    BookSellerUtil util;


    ArrayAdapter<String> traitadapter;

    Bitmap bitmap;
    static final int GALLERY_REQUESTCODE=1;
    static final int CAMERA_REQUESTCODE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_book);
        requestQueue= Volley.newRequestQueue(this);

        ButterKnife.inject(this);

        init();
    }


    void init()
    {
        sharedPreferences = getSharedPreferences(BookSellerUtil.SHAREDPREFS_FILENAME,MODE_PRIVATE);

        BImage.setOnClickListener(this);
        BInsert.setOnClickListener(this);

        bean = new BookBean();

        traitadapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        traitadapter.add("Select Type ");
        traitadapter.add("Mechanical");
        traitadapter.add("Computer Science ");
        traitadapter.add("IT");
        traitadapter.add("Electronics & Communication");
        traitadapter.add("Electrical");
        traitadapter.add("Civil");
        traitadapter.add("Production");
        traitadapter.add("Other");


        trait.setAdapter(traitadapter);
        trait.setOnItemSelectedListener(this);

        dialog=new ProgressDialog(this);
        dialog.setMessage("please wait");
        dialog.setCancelable(false);


        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.book);
        bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

        ConditionNew.setOnCheckedChangeListener(this);
        ConditionOld.setOnCheckedChangeListener(this);

    }

    void insertDataIntoBean()
    {
        bean.setName(BName.getText().toString().trim());
        bean.setAuthor(BAuthor.getText().toString().trim());
        bean.setPublisher(BPublisher.getText().toString().trim());
        bean.setPrice(BPrice.getText().toString().trim());

        bean.setImage(getStringImage(bitmap));
        bean.setUserName(sharedPreferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,""));
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


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
    {
        if(compoundButton.getId()==R.id.ConditionNew)
        {


            boolean check = ConditionNew.isChecked();
            if(check)
            {
                bean.setCondition("new");
//                Toast.makeText(getApplicationContext(),"in compound change "+bean.getCondition(),Toast.LENGTH_LONG).show();
            }

        }
        if(compoundButton.getId()==R.id.ConditionOld)
        {
            boolean check = ConditionOld.isChecked();
            if(check)
            {
                bean.setCondition("old");
//                Toast.makeText(getApplicationContext(),"in compound change "+bean.getCondition(),Toast.LENGTH_LONG).show();
            }

        }
    }

    void insertIntoCloud()
    {
        Log.i("test","insertIntoCloud");
        dialog.show();



        request = new StringRequest(Request.Method.POST, util.URL_ITEMINSERT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject obj= new JSONObject(response);
                            int success=obj.getInt("success");
                            String message=obj.getString("message");

                            if(success==1)
                            {
                                Toast.makeText(getApplicationContext(),success+"-"+message,Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(InsertBook.this,MainActivity.class);
                                MainActivity.getM().finish();
                                dialog.dismiss();
                                startActivity(intent);
                                finish();
                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext()," exception is"+e,Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {   dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"error "+error,Toast.LENGTH_LONG).show();

                    }
                })


        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> map= new HashMap<String,String>();
                map.put(util.KEY_ITEM_IMAGE,bean.getImage());
                map.put(util.KEY_ITEM_NAME,bean.getName());
                map.put(util.KEY_ITEM_AUTHOR,bean.getAuthor());
                map.put(util.KEY_ITEM_PUBLISHER,bean.getPublisher());
                map.put(util.KEY_ITEM_CONDITION,bean.getCondition());
                map.put(util.KEY_ITEM_PRICE,bean.getPrice());
                map.put(util.KEY_USERNAME,bean.getUserName());
                map.put(util.KEY_ITEM_TRAIT,bean.getTrait());
                Log.i("test",map.toString());
                return map;
            }
        };

        requestQueue.add(request);
    }

    public String getStringImage(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.BImage:

                String[] title = {"Gallery","Camera"};
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Upload Image");
                builder.setItems(title, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        Intent intent;
                        switch (which)
                        {

                            case 0:
                                intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUESTCODE);
                                break;
                            case 1:
                                intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (intent.resolveActivity(getPackageManager()) != null)
                                {
                                    startActivityForResult(intent, CAMERA_REQUESTCODE);
                                }
                                break;
                        }
                    }
                });
                builder.show();
                break;

            case R.id.BInsert:
//                Toast.makeText(getApplicationContext(),"Book Insert",Toast.LENGTH_LONG).show();

//                Toast.makeText(getApplicationContext(),bean.toString(),Toast.LENGTH_LONG).show();

                if(validations())
                {
                    insertDataIntoBean();
                    insertIntoCloud();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUESTCODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            Uri filePath = data.getData();
            try
            {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                bitmap=Bitmap.createScaledBitmap(bitmap,500,500,true);

                BImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (requestCode == CAMERA_REQUESTCODE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

            BImage.setImageBitmap(bitmap);
        }
    }

    boolean validations()
    {
        boolean flag = true;

        if(BName.getText().toString().isEmpty())
        {
            BName.setError("Book Name Required");
            flag = false;
        }
        if(BPublisher.getText().toString().isEmpty())
        {
            BPublisher.setError("Publisher Name Required");
            flag = false;
        }
        if(BAuthor.getText().toString().isEmpty())
        {
            BAuthor.setError("Author Required");
            flag = false;
        }
        if(BPrice.getText().toString().isEmpty())
        {
            BPrice.setError(" Price Required");
            flag = false;
        }
        if(bean.getTrait()==traitadapter.getItem(0)){
            Toast.makeText(this,"select a book type",Toast.LENGTH_LONG).show();
            flag = false;
        }
        return flag;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if(parent.getId()==R.id.spinner_trait1)
        {
            bean.setTrait(traitadapter.getItem(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
