package com.hjx.v2ex.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.SigninParams;
import com.hjx.v2ex.bean.SigninResult;
import com.hjx.v2ex.network.RetrofitServiceSingleton;
import com.hjx.v2ex.util.V2EXUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.codeImg)
    ImageView codeImg;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.login)
    Button loginBtn;
    @BindView(R.id.error_msg)
    TextView errorMsg;
    private SigninParams signinParams;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog = V2EXUtil.showProgressDialog(this, "正在获取登录信息");
        getSigninParams();
    }

    @OnFocusChange({R.id.name, R.id.password, R.id.code})
    public void handleErrorMsg(View view, boolean b) {
        errorMsg.setText("");
    }

    @OnClick(R.id.login)
    public void login(View view) {
        if(signinParams == null) return;
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
        if(TextUtils.isEmpty(code.getText())) {
            errorMsg.setText("验证码不能为空");
            return;
        }
        progressDialog = V2EXUtil.showProgressDialog(this, "正在登录");
        Map<String, String> params = new HashMap<>();
        params.put(signinParams.getName(), name.getText().toString());
        params.put(signinParams.getPassword(), password.getText().toString());
        params.put(signinParams.getCode(), code.getText().toString());
        params.put("once", signinParams.getOnce());
        params.put("next", signinParams.getNext());
        RetrofitServiceSingleton.getInstance(LoginActivity.this.getApplication()).signin(params).enqueue(new Callback<SigninResult>() {
            @Override
            public void onResponse(Call<SigninResult> call, Response<SigninResult> response) {
                SigninResult result = response.body();
                if(result != null) {
                    if(result.isSigin()) {
                        progressDialog.dismiss();
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
                loginFailed("登录失败，请重试");
                throwable.printStackTrace();
            }
        });
    }

    private void getSigninParams() {
        signinParams = null;
        RetrofitServiceSingleton.getInstance(getApplication()).getSigninParams().enqueue(new Callback<SigninParams>() {
            @Override
            public void onResponse(Call<SigninParams> call, Response<SigninParams> response) {
                signinParams = response.body();
                if(signinParams != null) {
                    RetrofitServiceSingleton.getInstance(getApplication()).getBitmap(signinParams.getCodeImg()).enqueue(new Callback<Bitmap>() {
                        @Override
                        public void onResponse(Call<Bitmap> call, Response<Bitmap> response) {
                            Bitmap bitmap = response.body();
                            if(bitmap != null) {
                                progressDialog.dismiss();
                                codeImg.setImageBitmap(bitmap);
                                codeImg.setVisibility(View.VISIBLE);
                                code.setVisibility(View.VISIBLE);
                                loginBtn.setEnabled(true);
                            } else {
                                progressDialog.dismiss();
                                errorMsg.setText("获取登录信息失败");
                            }
                        }

                        @Override
                        public void onFailure(Call<Bitmap> call, Throwable t) {
                            progressDialog.dismiss();
                            errorMsg.setText("获取登录信息失败");
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    errorMsg.setText("获取登录信息失败");
                }
            }

            @Override
            public void onFailure(Call<SigninParams> call, Throwable throwable) {
                progressDialog.dismiss();
                errorMsg.setText("获取登录信息失败");
                throwable.printStackTrace();
            }
        });
    }

    public void loginFailed(String msg) {
        errorMsg.setText(msg);
        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
        getSigninParams();
    }


}
