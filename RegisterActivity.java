package com.freshfarmkart.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.freshfarmkart.R;
import com.freshfarmkart.adapter.ViewPagerAdapter;
import com.freshfarmkart.commonutility.AllUrl;
import com.freshfarmkart.commonutility.CommonUtility;
import com.freshfarmkart.model.CityModel;
import com.freshfarmkart.model.GetUserTypeModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button btn_signup;
    //String[] usertype = {"SELECT USER TYPE", "HOME", "CORPORATE", "TRADE"};
    Spinner spiner, spiner_city;
    private AlertDialog progressDialog;
    EditText et_fname, et_email, et_phno, et_address, et_city, et_pin, et_password, et_cpassword;
    CheckBox checkBox;
    String device_id;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String email, fName, phone, address, city, pin, password, cpassword;
    public GetUserTypeModel getUserTypeModel;
    ArrayList<GetUserTypeModel> userTypeList;
    ArrayList<String> getUserName;
    ArrayList<String> getUserTypeId;
    ArrayList<String> cityname;
    ArrayList<String> cityId;
    String usertype_id, cityid;
    ArrayList<CityModel> citylist;
    public CityModel cityModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        cityname = new ArrayList<>();
        getUserName = new ArrayList<>();

        if (CommonUtility.isNetworkAvailable(this)) {
            getUserType();
            getCity();

        } else {
            Toast.makeText(this, "Please chaeck your internet", Toast.LENGTH_LONG).show();
        }
        init();
        btn_signup.setOnClickListener(this);
    }

    public void init() {
        btn_signup = findViewById(R.id.btn_signup);
        et_fname = findViewById(R.id.et_fname);
        et_email = findViewById(R.id.et_email);
        et_phno = findViewById(R.id.et_phno);
        et_address = findViewById(R.id.et_address);
        et_pin = findViewById(R.id.et_pin);
        et_password = findViewById(R.id.et_password);
        et_cpassword = findViewById(R.id.et_cpassword);
        spiner = (Spinner) findViewById(R.id.spiner);
        spiner_city = (Spinner) findViewById(R.id.spiner_city);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:
                // progressDialog = new SpotsDialog(this, R.style.Custom);
                // progressDialog.show();
                email = et_email.getText().toString();
                fName = et_fname.getText().toString();
                phone = et_phno.getText().toString();
                address = et_address.getText().toString();
                pin = et_pin.getText().toString();
                password = et_password.getText().toString();
                cpassword = et_cpassword.getText().toString();


              if (validate()) {
                    if (!password.equals(cpassword)) {
                        Toast.makeText(this, "Password and Confirm Password must be same", Toast.LENGTH_LONG).show();
                    } else if (CommonUtility.isNetworkAvailable(this)) {
                        UserRegistration(usertype_id, fName, email, phone, address, cityid, pin, password, device_id);
                    } else {
                        Toast.makeText(this, "Please check your internet", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(this, "Please fill correct information", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private boolean validate() {

        Matcher matcherObj = Pattern.compile(emailPattern).matcher(email);

        boolean result = true;
        if (!(et_fname.getText().length() > 0)) {
            et_fname.setError("Name can't be empty");
            result = false;
        }

        if (!matcherObj.matches()) {
            et_email.setError("E-mail is not valid");
            result = false;
        }

        /*if (!(et_city.getText().length() > 0)) {
            et_city.setError("City can't be empty");
            result = false;
        }*/
        if (!(et_address.getText().length() > 0)) {
            et_address.setError("Address can't be empty");
            result = false;
        }

        if (!(et_phno.getText().length() == 10)) {
            et_phno.setError("please enter correct phone no");
            result = false;
        }
        if (!(et_pin.getText().length() == 3)) {
            et_pin.setError("please enter correct pincode");
            result = false;
        }

        if (!(et_password.getText().length() > 5)) {
            et_password.setError("Password length greater then  5 digit");
            result = false;
        }
        if (!(et_cpassword.getText().length() > 5)) {
            et_cpassword.setError("Password length greater then  5 digit");
            result = false;
        }


        return result;
    }

    public void UserRegistration(String usertype_id, String fName, String email, String phone, String address, String city, String pin, String password
            , String device_id) {
        Map<String, String> params = new HashMap<>();
        params.put("UserTypeId", usertype_id);
        params.put("FullName", fName);
        params.put("EmailId", email);
        params.put("MobileNumer", phone);
        params.put("Address", address);
        params.put("CityId", city);
        params.put("PinCode", pin);
        params.put("Password", password);
        params.put("DeviceId", device_id);

        //Utility.showProgressDialog(this);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userRegistration, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("registration", jsonObject.toString());
                // Utility.dismissProgressDialog();
                progressDialog.dismiss();
                try {
                    //JSONObject jsonObject1 = jsonObject.getString();
                    Log.d("jiii", jsonObject.toString());
                    Boolean pass = jsonObject.getBoolean("IsSuccess");
                    //Log.d("pass",pass);
                    String msg = jsonObject.getString("ErrorMessage");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        //intent.putExtra("mobile",mobile);
                        startActivity(intent);
                        RegisterActivity.this.finish();
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please enter right credential", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d("error ocurred", "TimeoutError");

                } else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred", "AuthFailureError");

                } else if (error instanceof ServerError) {
                    Log.d("error ocurred", "ServerError");

                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred", "NetworkError");

                } else if (error instanceof ParseError) {
                    Log.d("error ocurred", "ParseError");

                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("API_Key", "FarmHoues_Key");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    public void getUserType() {
        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AllUrl.getuserType,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("usertype", response);
                        //hiding the progressbar after completion
                        progressDialog.dismiss();
                        try {
                            //so here we are getting that json array
                            userTypeList = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                getUserTypeModel = new GetUserTypeModel();
                                //getting the json object of the particular index inside the array
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String userTypeId = Integer.toString(jsonObject.getInt("UserTypeId"));
                                String userTypeName = jsonObject.getString("UserTypeName");
                                String userTypedesc = jsonObject.getString("UserTypeDesc");
                                // Boolean active = jsonObject.getBoolean("IsActive");
                                getUserTypeModel.setUserTypeId(userTypeId);
                                getUserTypeModel.setUserTypeName(userTypeName);
                                getUserTypeModel.setUserTypeDesc(userTypedesc);
                                //  getUserTypeModel.setUserIsActive(active);
                                userTypeList.add(getUserTypeModel);
                                // userTypeList.add(userTypeId);

                            }
                            for (int i = 0; i < userTypeList.size(); i++) {
                                getUserName.add(userTypeList.get(i).getUserTypeName().toString());
                            }
                            //Creating the ArrayAdapter instance having the country list
                            ArrayAdapter aa = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_layout, getUserName);
                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spiner.setAdapter(aa);

                            spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    usertype_id = userTypeList.get(i).getUserTypeId();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Log.d("error ocurred", "TimeoutError");

                        } else if (error instanceof AuthFailureError) {
                            Log.d("error ocurred", "AuthFailureError");

                        } else if (error instanceof ServerError) {
                            Log.d("error ocurred", "ServerError");

                        } else if (error instanceof NetworkError) {
                            Log.d("error ocurred", "NetworkError");

                        } else if (error instanceof ParseError) {
                            Log.d("error ocurred", "ParseError");

                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("API_Key", "FarmHoues_Key");
                return headers;
            }
        };
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public void getCity() {
        // progressDialog = new SpotsDialog(this, R.style.Custom);
        // progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AllUrl.getCity,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("usercity", response);
                        //hiding the progressbar after completion
                        //     progressDialog.dismiss();
                        citylist = new ArrayList<>();
                        try {
                            //so here we are getting that json array
                            JSONArray jsonArray = new JSONArray(response);
                            //now looping through all the elements of the json array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                cityModel = new CityModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String cityId = Integer.toString(jsonObject.getInt("CityId"));
                                String cityName = jsonObject.getString("CityName");
                                String citydesc = jsonObject.getString("CityDescription");
                                // Boolean active = jsonObject.getBoolean("IsActive");
                                cityModel.setCityName(cityName);
                                cityModel.setCityId(cityId);
                                cityModel.setCityDesc(citydesc);
                                citylist.add(cityModel);
                            }
                            for (int i = 0; i < citylist.size(); i++) {
                                cityname.add(citylist.get(i).getCityName().toString());
                            }
                            //Creating the ArrayAdapter instance having the country list
                            ArrayAdapter aa = new ArrayAdapter(RegisterActivity.this, R.layout.spinner_layout, cityname);
                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            spiner_city.setAdapter(aa);
                            spiner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    cityid = citylist.get(i).getCityId();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Log.d("error ocurred", "TimeoutError");

                        } else if (error instanceof AuthFailureError) {
                            Log.d("error ocurred", "AuthFailureError");

                        } else if (error instanceof ServerError) {
                            Log.d("error ocurred", "ServerError");

                        } else if (error instanceof NetworkError) {
                            Log.d("error ocurred", "NetworkError");

                        } else if (error instanceof ParseError) {
                            Log.d("error ocurred", "ParseError");

                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("API_Key", "FarmHoues_Key");
                return headers;
            }
        };
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}
