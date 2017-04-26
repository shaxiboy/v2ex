package com.hjx.v2ex.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.SigninParams;
import com.hjx.v2ex.bean.SigninResult;
import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.util.V2EXUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.type;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    Button loginBtn;
    @BindView(R.id.error_msg)
    TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnFocusChange({R.id.name, R.id.password})
    public void handleErrorMsg(View view, boolean b) {
        errorMsg.setText("");
    }

    @OnClick(R.id.login)
    public void login(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if(TextUtils.isEmpty(name.getText())) {
            errorMsg.setText("用户名不能为空");
            return;
        }
        if(TextUtils.isEmpty(password.getText())) {
            errorMsg.setText("密码不能为空");
            return;
        }
        final ProgressDialog progressDialog = V2EXUtil.showProgressDialog(this, "正在登录");
        RetrofitSingleton.getInstance(this).signin().enqueue(new Callback<SigninParams>() {
            @Override
            public void onResponse(Call<SigninParams> call, Response<SigninParams> response) {
                SigninParams signinParams = response.body();
                if(signinParams != null) {
                    Map<String, String> params = new HashMap<>();
                    params.put(signinParams.getName(), name.getText().toString());
                    params.put(signinParams.getPassword(), password.getText().toString());
                    params.put("once", signinParams.getOnce());
                    params.put("next", signinParams.getNext());
                    RetrofitSingleton.getInstance(LoginActivity.this).signin(params).enqueue(new Callback<SigninResult>() {
                        @Override
                        public void onResponse(Call<SigninResult> call, Response<SigninResult> response) {
                            progressDialog.dismiss();
                            SigninResult result = response.body();
                            if(result != null) {
                                if(result.isSigin()) {
                                    V2EXUtil.writeLoginResult(LoginActivity.this, result);
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    loginFailed(result.getErrorMsg());
                                }
                            } else {
                                loginFailed("登录失败，请重试");
                            }
                        }

                        @Override
                        public void onFailure(Call<SigninResult> call, Throwable throwable) {
                            progressDialog.dismiss();
                            loginFailed("登录失败，请重试");
                            throwable.printStackTrace();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    loginFailed("登录失败，请重试");
                }
            }

            @Override
            public void onFailure(Call<SigninParams> call, Throwable throwable) {
                progressDialog.dismiss();
                loginFailed("登录失败，请重试");
                throwable.printStackTrace();
            }
        });
    }

    public void loginFailed(String msg) {
        errorMsg.setText(msg);
        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
    }


}
