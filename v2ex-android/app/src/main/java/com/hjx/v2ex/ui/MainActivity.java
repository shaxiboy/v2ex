package com.hjx.v2ex.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hjx.v2ex.R;
import com.hjx.v2ex.adapter.NodesPagerAdapter;
import com.hjx.v2ex.adapter.TopicsPagerAdapter;
import com.hjx.v2ex.bean.SigninResult;
import com.hjx.v2ex.bean.SignoutOnce;
import com.hjx.v2ex.bean.SignoutResult;
import com.hjx.v2ex.bean.UnReadNotificationNum;
import com.hjx.v2ex.event.SignoutEvent;
import com.hjx.v2ex.network.RetrofitServiceSingleton;
import com.hjx.v2ex.util.V2EXUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;
    @BindView(R.id.notification_container)
    FrameLayout notificationContainer;
    @BindView(R.id.notification_image)
    ImageView notificationImage;
    @BindView(R.id.notification_text)
    TextView notificationText;

    private CircleImageView photo;
    private TextView name;

    private TopicsPagerAdapter topicsPagerAdapter;
    private NodesPagerAdapter nodesPagerAdapter;
    private Timer notificationTimer;
    private Handler notificationHandler;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initDrawer();

        tabLayout.setupWithViewPager(viewPager, false);
        topicsPagerAdapter = new TopicsPagerAdapter(getSupportFragmentManager(), this);
        nodesPagerAdapter = new NodesPagerAdapter(getSupportFragmentManager(), NodeListFragment.NODEACTIONTYPE_VIEW);
        if (savedInstanceState != null && savedInstanceState.getString("title").equals("节点")) {
            setTitle("节点");
            viewPager.setAdapter(nodesPagerAdapter);
        } else viewPager.setAdapter(topicsPagerAdapter);

        notificationContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawableCompat.setTint(notificationImage.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                notificationText.setVisibility(View.GONE);
                DataLoadingBaseActivity.gotoNotificationsActivity(MainActivity.this);
            }
        });

        notificationHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                getUnReadNotifications();
            }
        };
        if (V2EXUtil.isLogin(this)) {
            onSignin();
            getNewSignoutOnce();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationTimer != null) notificationTimer.cancel();
    }

    @Subscribe
    public void onSignoutEvent(SignoutEvent event) {
        signout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            onSignin();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出V2EX", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topic:
                viewPager.setAdapter(topicsPagerAdapter);
                if (V2EXUtil.isLogin(this)) notificationContainer.setVisibility(View.VISIBLE);
                setTitle("V2EX");
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.node:
                viewPager.setAdapter(nodesPagerAdapter);
                notificationContainer.setVisibility(View.GONE);
                setTitle("节点");
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.favorite:
                if (V2EXUtil.isLogin(this))
                    startActivity(new Intent(MainActivity.this, MyFavoritesActivity.class));
                else startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            case R.id.new_topic:
                if (V2EXUtil.isLogin(this))
                    startActivity(new Intent(MainActivity.this, NewTopicActivity.class));
                else startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                drawer.closeDrawer(GravityCompat.START);
                return false;
            case R.id.signout:
                new LoginoutDialogFragment().show(getSupportFragmentManager(), "loginout");
                drawer.closeDrawer(GravityCompat.START);
                return false;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                drawer.closeDrawer(GravityCompat.START);
                return false;
            case R.id.feedback:
                startActivity(new Intent(this, UserReplyActivity.class));
                drawer.closeDrawer(GravityCompat.START);
                return false;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("title", getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        photo = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.photo);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (V2EXUtil.isLogin(MainActivity.this))
                    DataLoadingBaseActivity.gotoMemberDetailsActivity(MainActivity.this, name.getText().toString());
                else startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
            }
        });
        name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
    }

    private void onSignin() {
        setViewOnSignin();
        notificationTimer = new Timer();
        notificationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                notificationHandler.sendMessage(Message.obtain());
            }
        }, 0, 30 * 1000);
    }

    private void setViewOnSignin() {
        SigninResult signinResult = V2EXUtil.readLoginResult(this);
        if (signinResult != null) {
            Glide.with(this).load(signinResult.getPhoto()).into(photo);
            name.setText(signinResult.getName());
            navigationView.getMenu().findItem(R.id.signout).setVisible(true);
            if (viewPager.getAdapter() instanceof TopicsPagerAdapter) {
                notificationContainer.setVisibility(View.VISIBLE);
                viewPager.setAdapter(topicsPagerAdapter);
            }
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void onSignout() {
        setViewOnSignout();
        notificationTimer.cancel();
    }

    private void setViewOnSignout() {
        V2EXUtil.clearLoginResult(MainActivity.this);
        Glide.with(this).load(R.drawable.member).into(photo);
        name.setText("点击头像登录");
        navigationView.getMenu().findItem(R.id.signout).setVisible(false);
        if (viewPager.getAdapter() instanceof TopicsPagerAdapter)
            viewPager.setAdapter(topicsPagerAdapter);
        notificationContainer.setVisibility(View.GONE);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void getUnReadNotifications() {
        RetrofitServiceSingleton.getInstance(MainActivity.this.getApplication()).getUnReadNotificationNum().enqueue(new retrofit2.Callback<UnReadNotificationNum>() {
            @Override
            public void onResponse(Call<UnReadNotificationNum> call, Response<UnReadNotificationNum> response) {
                try {
                    int num = response.body().getNum();
                    if (num != 0) {
                        DrawableCompat.setTint(notificationImage.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                        notificationText.setText(num + "");
                        notificationText.setVisibility(View.VISIBLE);
                    } else {
                        DrawableCompat.setTint(notificationImage.getDrawable(), ContextCompat.getColor(MainActivity.this, R.color.colorWhite));
                        notificationText.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UnReadNotificationNum> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getNewSignoutOnce() {
        RetrofitServiceSingleton.getInstance(getApplication()).getSignoutOnce().enqueue(new Callback<SignoutOnce>() {
            @Override
            public void onResponse(Call<SignoutOnce> call, Response<SignoutOnce> response) {
                try {
                    int once = response.body().getOnce();
                    if (once != -1) {
                        SigninResult signinResult = V2EXUtil.readLoginResult(MainActivity.this);
                        signinResult.setSignoutOnce(once);
                        V2EXUtil.writeLoginResult(MainActivity.this, signinResult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SignoutOnce> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void signout() {
        final SigninResult signinResult = V2EXUtil.readLoginResult(MainActivity.this);
        if (signinResult != null && signinResult.getSignoutOnce() != -1) {
            final ProgressDialog progressDialog = V2EXUtil.showProgressDialog(this, "正在登出");
            RetrofitServiceSingleton.getInstance(MainActivity.this.getApplication()).signout(signinResult.getSignoutOnce()).enqueue(new Callback<SignoutResult>() {
                @Override
                public void onResponse(Call<SignoutResult> call, Response<SignoutResult> response) {
                    progressDialog.dismiss();
                    SignoutResult result = response.body();
                    boolean success = false;
                    if (result != null) {
                        if (result.isSignout()) {
                            success = true;
                        } else {
                            if (result.getNewSignoutOnce() != -1) {
                                signinResult.setSignoutOnce(result.getNewSignoutOnce());
                                V2EXUtil.writeLoginResult(MainActivity.this, signinResult);
                            }
                        }
                    }
                    if (success) {
                        Toast.makeText(MainActivity.this, "登出成功", Toast.LENGTH_SHORT).show();
                        onSignout();
                    } else {
                        Toast.makeText(MainActivity.this, "登出失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SignoutResult> call, Throwable throwable) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "登出失败", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }
            });
        }
    }
}
