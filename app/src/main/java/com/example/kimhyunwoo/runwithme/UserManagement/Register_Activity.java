package com.example.kimhyunwoo.runwithme.UserManagement;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kimhyunwoo.runwithme.ManagementUtil;
import com.example.kimhyunwoo.runwithme.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.jar.Attributes;
import java.util.regex.Pattern;

public class Register_Activity extends AppCompatActivity {

    private ManagementUtil Util;
    private AlertDialog dialog;

    private String UserGenderString;
    private int UserGender;
    private String UserEmail;
    private String UserPassword;
    private String UserBirthDay;
    private String UserName;
    private String ConfirmPassword;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListner;
    EditText EmailText,PasswordText,ConfirmPasswordText,NameText;
    TextInputLayout EmailInputLayout,PasswordInputLayout,ConfirmPasswordInputLayout,NameInputLayout;

    private Button RegisterButton;

    private boolean EmailFlag,PasswordFlag,ConfirmPasswordFlag,NameFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDisplayDate = (TextView)findViewById(R.id.SelectBirthday);

        Util = new ManagementUtil();
        EmailFlag = PasswordFlag = ConfirmPasswordFlag = NameFlag = true;

        EmailText = (EditText)findViewById(R.id.RegisterEmailText);EmailText.setHint(" Email");
        EmailInputLayout = (TextInputLayout) findViewById(R.id.RegisterEmailTextLayout);

        PasswordText = (EditText)findViewById(R.id.RegisterPasswordText);PasswordText.setHint(" Password");
        PasswordInputLayout = (TextInputLayout) findViewById(R.id.RegisterPasswordTextLayout);

        ConfirmPasswordText = (EditText)findViewById(R.id.RegisterPasswordConfirmText);ConfirmPasswordText.setHint(" ConfirmPassword");
        ConfirmPasswordInputLayout = (TextInputLayout) findViewById(R.id.RegisterPasswordConfirmTextLayout);

        NameText = (EditText)findViewById(R.id.RegisterNameText);NameText.setHint(" Name / Nickname");
        NameInputLayout = (TextInputLayout) findViewById(R.id.RegisterNameTextInputlayout);

        RegisterButton = (Button)findViewById(R.id.RegisterButton);

        final RadioGroup GenderGroup = (RadioGroup)findViewById(R.id.GenderGroup);
        int GenderID = GenderGroup.getCheckedRadioButtonId();
        UserGenderString = ((RadioButton)findViewById(GenderID)).getText().toString();

        // 성별 체크
        GenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton GenderButton = (RadioButton)findViewById(checkedId);
                UserGender = GenderButton.getText().toString()=="Female"?0:1;
                Log.v("User's Log","GenderCheck = "+UserGender);
            }
        });

        // 이메일 포맷 체크
        EmailText.addTextChangedListener(new TextWatcher() {
            private String TemporaryString;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                do {
                    TemporaryString = EmailText.getText().toString();
                    if(TemporaryString.equals(""))
                    {
                        EmailText.setHint(" Email");
                        EmailInputLayout.setErrorEnabled(true);
                        EmailInputLayout.setError("Please enter the Email");
                        EmailFlag = true;
                        break;
                    }
                    else
                    {
                        EmailFlag = false;
                    }

                    if(TemporaryString.length()>50)
                    {
                        EmailInputLayout.setErrorEnabled(true);
                        EmailInputLayout.setError("Email can't exceed 50 letters");
                        EmailFlag = true;
                        break;
                    }
                    else
                    {
                        EmailFlag = false;
                    }

                    if(isEmailValid(TemporaryString)==false)
                    {
                        EmailInputLayout.setErrorEnabled(true);
                        EmailInputLayout.setError("Email is not valid");
                        EmailFlag = true;
                        break;
                    }
                    else
                    {
                        EmailFlag = false;
                    }
                }while(false);

                if(!EmailFlag)
                {
                    EmailInputLayout.setErrorEnabled(false);
                    Util.setEditColor(EmailText,"#00ff00");
                }
                else
                {
                    Util.setEditColor(EmailText,"#ff0000");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Password입력부분 오류체크
        PasswordText.addTextChangedListener(new TextWatcher() {
            private String TemporaryString;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TemporaryString = PasswordText.getText().toString();
                do {
                    if(TemporaryString.equals(""))
                    {
                        PasswordText.setHint(" Password");
                        PasswordInputLayout.setErrorEnabled(true);
                        PasswordInputLayout.setError("Please enter the Password");
                        PasswordFlag = true;
                        break;
                    }
                    else
                    {
                        PasswordFlag = false;
                    }

                    if(TemporaryString.length() > 50)
                    {
                        PasswordInputLayout.setErrorEnabled(true);
                        PasswordInputLayout.setError("Password is less than 50 letters");
                        PasswordFlag = true;
                        break;
                    }
                    else
                    {
                        PasswordFlag = false;
                    }
                    if(!(ConfirmPasswordText.getText().toString().equals("")) && !TemporaryString.equals(ConfirmPasswordText.getText().toString()))
                    {
                        ConfirmPasswordInputLayout.setErrorEnabled(true);
                        ConfirmPasswordInputLayout.setError("The Passwords is not matched");
                        ConfirmPasswordFlag = true;
                    }
                    else
                    {
                        ConfirmPasswordFlag = false;
                    }
                }while(false);
                if(!PasswordFlag)
                {
                    PasswordInputLayout.setErrorEnabled(false);
                    Util.setEditColor(PasswordText,"#00ff00");
                }
                else
                {
                    Util.setEditColor(PasswordText,"#ff0000");
                }
                if(!ConfirmPasswordFlag)
                {
                    ConfirmPasswordInputLayout.setErrorEnabled(false);
                    Util.setEditColor(ConfirmPasswordText,"#00ff00");
                }
                else
                {
                    Util.setEditColor(ConfirmPasswordText,"#ff0000");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Confirm Password 오류체크부분
        ConfirmPasswordText.addTextChangedListener(new TextWatcher() {
            private String Temporarystring;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Temporarystring = ConfirmPasswordText.getText().toString();
                do {
                    if(Temporarystring.equals(""))
                    {
                        ConfirmPasswordText.setHint(" Confirm Password");
                        ConfirmPasswordInputLayout.setErrorEnabled(true);
                        ConfirmPasswordInputLayout.setError("Please enter the Confirm Password");
                        ConfirmPasswordFlag = true;
                        break;
                    }
                    else
                    {
                        ConfirmPasswordFlag = false;
                    }
                    if(!PasswordText.getText().toString().equals(Temporarystring))
                    {
                        ConfirmPasswordInputLayout.setErrorEnabled(true);
                        ConfirmPasswordInputLayout.setError("The Passwords do not matched");
                        ConfirmPasswordFlag = true;
                        break;
                    }
                    else
                    {
                        ConfirmPasswordFlag = false;
                    }
                }while(false);
                if(!ConfirmPasswordFlag)
                {
                    ConfirmPasswordInputLayout.setErrorEnabled(false);
                    Util.setEditColor(ConfirmPasswordText,"#00ff00");
                }
                else
                {
                    Util.setEditColor(ConfirmPasswordText,"#ff0000");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //닉네임 오류체크 부분
        NameText.addTextChangedListener(new TextWatcher() {
            private String TemporaryString;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TemporaryString = NameText.getText().toString();
                do {
                    if(TemporaryString.equals(""))
                    {
                        NameText.setHint(" Nickname");
                        NameInputLayout.setErrorEnabled(true);
                        NameInputLayout.setError("Please enter the Nickname");
                        NameFlag = true;
                        break;
                    }
                    else
                    {
                        NameFlag = false;
                    }
                    if(TemporaryString.length()>12)
                    {
                        NameInputLayout.setErrorEnabled(true);
                        NameInputLayout.setError("Nickname is less than 12");
                        NameFlag = true;
                        break;
                    }
                    else
                    {
                        NameFlag = false;
                    }
                    if(!Pattern.matches("^[a-z0-9]{1,12}$", TemporaryString)) {
                        NameInputLayout.setErrorEnabled(true);
                        NameInputLayout.setError("password should be mixing with small English letter and number");
                        NameFlag = true;
                        break;
                    }
                    else
                    {
                        NameFlag = false;
                    }
                }while(false);

                if(!NameFlag)
                {
                    NameInputLayout.setErrorEnabled(false);
                    Util.setEditColor(NameText,"#00ff00");
                }
                else
                {
                    Util.setEditColor(NameText,"#ff0000");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Register_Activity.this,android.R.style.Theme_Black,mDateSetListner,year,month,day);
                dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));
                dialog.show();
            }
        });

        mDateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                Log.v("User's Log DatePicker","date:"+year+"-"+month+"-"+dayOfMonth);
                String date = year+"-"+month+"-"+dayOfMonth;
                mDisplayDate.setText(date);
            }
        };

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEmail = EmailText.getText().toString();
                UserPassword = PasswordText.getText().toString();
                UserName = NameText.getText().toString();
                UserBirthDay = mDisplayDate.getText().toString();

                if(EmailFlag)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
                    dialog = builder.setMessage("이메일을 다시 확인해주세요")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }
                if(PasswordFlag)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
                    dialog = builder.setMessage("비밀번호를 다시 확인해주세요")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(ConfirmPasswordFlag)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
                    dialog = builder.setMessage("비밀번호 확인을 다시 체크해주세요")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(NameFlag)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
                    dialog = builder.setMessage("이름을 다시 확인해주세요")
                            .setNegativeButton("OK", null)
                            .create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> RegisterresponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("User'sRegisterLog",response);
                            JSONObject jsonregisterResponse = new JSONObject(response);
                            JSONObject registmessage = jsonregisterResponse.getJSONObject("json");
                            String registmessage2 = registmessage.getString("join");
                            if (registmessage2.equals("ok")) {
                                Toast.makeText(Register_Activity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
                                dialog = builder.setMessage(registmessage2)
                                        .setNegativeButton("OK", null)
                                        .create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(UserEmail, UserPassword, UserGender, UserName, UserBirthDay, RegisterresponseListener,Register_Activity.this);
                RequestQueue Registerqueue = Volley.newRequestQueue(Register_Activity.this);
                Registerqueue.add(registerRequest);
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        Util = null;
    }

    boolean isEmailValid(CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
