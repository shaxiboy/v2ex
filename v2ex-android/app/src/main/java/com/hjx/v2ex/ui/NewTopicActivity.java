package com.hjx.v2ex.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hjx.v2ex.R;
import com.hjx.v2ex.bean.NewTopicOnce;
import com.hjx.v2ex.bean.NewTopicResult;
import com.hjx.v2ex.bean.Node;
import com.hjx.v2ex.network.RetrofitSingleton;
import com.hjx.v2ex.util.V2EXUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTopicActivity extends AppCompatActivity {

    @BindView(R.id.node)
    TextView nodeTV;
    @BindView(R.id.choose_node)
    Button chooseNodeBtn;
    @BindView(R.id.title)
    EditText titleEdt;
    @BindView(R.id.content)
    EditText contentEdt;
    @BindView(R.id.new_topic)
    Button newTopicBtn;

    private int newTopicOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        ButterKnife.bind(this);
        chooseNodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(NewTopicActivity.this, ChooseNodeActivity.class), 1);
            }
        });
        titleEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().equals("") && !nodeTV.getText().equals("")) newTopicBtn.setEnabled(true);
                else newTopicBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newTopicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewTopic();
            }
        });

        getNewTopicOnce();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Node node = (Node) data.getSerializableExtra("node");
            nodeTV.setText(node.getTitle());
            nodeTV.setTag(node);
        }
    }

    private void getNewTopicOnce() {
        RetrofitSingleton.getInstance(this).getNewTopicOnce().enqueue(new Callback<NewTopicOnce>() {
            @Override
            public void onResponse(Call<NewTopicOnce> call, Response<NewTopicOnce> response) {
                NewTopicOnce data = response.body();
                if(data != null) {
                    newTopicOnce = data.getOnce();
                }
            }

            @Override
            public void onFailure(Call<NewTopicOnce> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void sendNewTopic() {
        final ProgressDialog dialog = V2EXUtil.showProgressDialog(this, "正在发布新主题");
        RetrofitSingleton.getInstance(this).newTopic(titleEdt.getText().toString().trim(), contentEdt.getText().toString().trim(), ((Node) nodeTV.getTag()).getName(), newTopicOnce).enqueue(new Callback<NewTopicResult>() {
            @Override
            public void onResponse(Call<NewTopicResult> call, Response<NewTopicResult> response) {
                dialog.dismiss();
                NewTopicResult data = response.body();
                if(data != null) {
                    if(data.isSuccess()) {
                        Toast.makeText(NewTopicActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        DataLoadingBaseActivity.gotoTopicDetailsActivity(NewTopicActivity.this, data.getNewTopicId());
                    } else {
                        newTopicOnce = data.getNewOnce();
                        if(data.getFailedMsg() != null) {
                            Toast.makeText(NewTopicActivity.this, "发布失败：" + data.getFailedMsg(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(NewTopicActivity.this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(NewTopicActivity.this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewTopicResult> call, Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(NewTopicActivity.this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }
        });
    }
}
