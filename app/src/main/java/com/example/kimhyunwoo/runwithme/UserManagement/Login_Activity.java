package com.example.kimhyunwoo.runwithme.UserManagement;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kimhyunwoo.runwithme.MainActivity.Main_Activity;
import com.example.kimhyunwoo.runwithme.R;
import com.example.kimhyunwoo.runwithme.UserInfo;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class Login_Activity extends AppCompatActivity {

    // 다이얼로그는 비밀번호 틀렸을때 추가 메세지 띄우기 위함.
    private AlertDialog dialog;
    private boolean EmailFlag,PasswordFlag;
    EditText EmailText;
    EditText PasswordText;
    Button LoginButton;
    TextView FindPasswordTextView;
    TextView RegisterTextView;
    TextInputLayout EmailTextLayout,PasswordTextLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EmailFlag = PasswordFlag = true;
        EmailText = (EditText)findViewById(R.id.LoginEmailText);EmailText.setHint(" Email");
        PasswordText = (EditText)findViewById(R.id.LoginPasswordText);PasswordText.setHint(" Password");
        LoginButton = (Button)findViewById(R.id.LoginButton);
        FindPasswordTextView = (TextView)findViewById(R.id.FindPasswordTextView);
        RegisterTextView = (TextView)findViewById(R.id.RegisterTextView);
        EmailTextLayout = (TextInputLayout)findViewById(R.id.LoginEmailTextInputLayout);
        PasswordTextLayout = (TextInputLayout)findViewById(R.id.LoginPasswordTextInputLayout);


        // 이메일 형식 체크
        EmailText.addTextChangedListener(new TextWatcher() {
            String TemporaryString;
            String ErrorMessage = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TemporaryString = EmailText.getText().toString();
                do{
                    if(TemporaryString.equals(""))
                    {
                        EmailText.setHint(" Email");
                        ErrorMessage = "Email is not Empty";
                        EmailFlag = true;
                        break;
                    }
                    else
                    {
                        EmailFlag = false;
                    }
                    if(isEmailValid(TemporaryString)==false)
                    {
                        ErrorMessage = "Email is not valid";
                        EmailFlag = true;
                        break;
                    }
                    else
                    {
                        EmailFlag = false;
                    }
                    if(TemporaryString.length()>50)
                    {
                        ErrorMessage = "Email is less than 50 letters";
                        EmailFlag = true;
                        break;
                    }
                    else
                    {
                        EmailFlag = false;
                    }
                }while(false);

                if(EmailFlag)
                {
                    EmailTextLayout.setErrorEnabled(true);
                    EmailTextLayout.setError(ErrorMessage);
                }
                else
                {
                    EmailTextLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 비밀번호 형식 체크
        PasswordText.addTextChangedListener(new TextWatcher() {
            private String TemporaryString;
            private String ErrorMessage = "";
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
                        ErrorMessage = "Please enter the Password";
                        PasswordFlag = true;
                        break;
                    }
                    else
                    {
                        PasswordFlag = false;
                    }

                    if(TemporaryString.length() > 50)
                    {
                        ErrorMessage = "Password is less than 50 letters";
                        PasswordFlag = true;
                        break;
                    }
                    else
                    {
                        PasswordFlag = false;
                    }
                }while(false);
                if(PasswordFlag)
                {
                    PasswordTextLayout.setErrorEnabled(true);
                    PasswordTextLayout.setError(ErrorMessage);
                }
                else
                {
                    PasswordTextLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 회원가입 버튼
        RegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Login_Activity.this,Register_Activity.class);      // 회원가입 버튼 눌렀을시 회원가입액티비티로 넘어감
                Login_Activity.this.startActivity(registerIntent);
            }
        });

        //로그인 버튼
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userEmail = EmailText.getText().toString();
                String userPassword = PasswordText.getText().toString();

                if(EmailFlag)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);      // 로그인 실패로 알림을 띄움
                    dialog = builder.setMessage("이메일 포맷을 다시 확인해주세요")
                            .setNegativeButton("Try Again",null)
                            .create();
                    dialog.show();
                    return;
                }

                if(PasswordFlag)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);      // 로그인 실패로 알림을 띄움
                    dialog = builder.setMessage("비밀번호를 다시 확인해주세요")
                            .setNegativeButton("Try Again",null)
                            .create();
                    dialog.show();
                    return;
                }

                Toast.makeText(Login_Activity.this, "Wait a second", Toast.LENGTH_SHORT).show();

                // Volley 사용하기 위한 리스너 정의.
                Response.Listener<String> reponseListener = new Response.Listener<String>()
                {
                    // Volley 를 통해서 정상적으로 웹서버와 통신이 되면 실행되는 함수
                    @Override
                    public void onResponse(String response)
                    {
                        Log.v("LoginResponse",response);
                        try
                        {
                            // JSON 형식으로 값을 response 에 받아서 넘어온다.
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject loginmessage = jsonResponse.getJSONObject("json");
                            String message = loginmessage.getString("login");
                            Log.v("message",message);
                            if(message.equals("ok"))
                            {
                                String nickname = loginmessage.getString("username");
                                UserInfo.setUserEmail(EmailText.getText().toString());
                                UserInfo.setUserNickname(nickname);
                                Intent intent = new Intent(Login_Activity.this,Main_Activity.class);      // 로그인 성공으로 메인화면으로 넘어감.
                                Login_Activity.this.startActivity(intent);
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);      // 로그인 실패로 알림을 띄움
                                dialog = builder.setMessage(message)
                                        .setNegativeButton("Try Again",null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userEmail,userPassword,reponseListener,Login_Activity.this);           // 위에서 작성한 리스너를 기반으로 요청하는 클래스를 선언.(LoginRequest참고)
                RequestQueue queue = Volley.newRequestQueue(Login_Activity.this);            // Volley의 사용법으로 request queue로 queue를 하나 선언하고
                queue.add(loginRequest);
            }
        });

        // 패스워드 찾기 버튼
        FindPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    boolean isEmailValid(CharSequence email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
