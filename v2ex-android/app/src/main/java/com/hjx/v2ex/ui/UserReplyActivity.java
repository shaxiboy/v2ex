package com.hjx.v2ex.ui;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hjx.v2ex.R;
import com.hjx.v2ex.network.RetrofitServiceSingleton;
import com.hjx.v2ex.util.V2EXUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReplyActivity extends AppCompatActivity {

    @BindView(R.id.reply)
    EditText replyEdt;
    @BindView(R.id.contact_type)
    Spinner contactTypeSpinner;
    @BindView(R.id.contact)
    EditText contactEdt;
    @BindView(R.id.submit)
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reply);
        ButterKnife.bind(this);
        replyEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().equals("")) submitBtn.setEnabled(true);
                else submitBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReply();
            }
        });
    }

    private void submitReply() {
        final ProgressDialog dialog = V2EXUtil.showProgressDialog(this, "正在提交反馈");
        String reply = replyEdt.getText().toString().trim();
        String[] contactTypes = {"email", "qq", "wechat"};
        String contact = contactEdt.getText().toString().trim();
        if(contact != null && !contact.equals("")) contact = contactTypes[contactTypeSpinner.getSelectedItemPosition()] + ":" + contact;
        String appVersion = null;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        RetrofitServiceSingleton.getInstance(this).submitUserReply(contact, reply, appVersion).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                String result = null;
                try {
                    result = response.body().string();
                    if(result != null && result.equals("success")) {
                        Toast.makeText(UserReplyActivity.this, "提交成功，非常感谢您的反馈！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(UserReplyActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(UserReplyActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        });
    }
}
