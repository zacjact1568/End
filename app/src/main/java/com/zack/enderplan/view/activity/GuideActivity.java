package com.zack.enderplan.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.view.fragment.WelcomeFragment;
import com.zack.enderplan.view.contract.GuideViewContract;
import com.zack.enderplan.presenter.GuidePresenter;

public class GuideActivity extends BaseActivity implements GuideViewContract {

    private GuidePresenter mGuidePresenter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, GuideActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGuidePresenter = new GuidePresenter(this);
        mGuidePresenter.setInitialView();
    }

    @Override
    public void onBackPressed() {
        mGuidePresenter.notifyBackPressed();
    }

    @Override
    public void showInitialView() {
        setContentView(R.layout.activity_guide);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new WelcomeFragment()).commit();
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exit() {
        finish();
        //TODO 添加动画
    }
}
