package com.zack.enderplan.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.zack.enderplan.App;
import com.zack.enderplan.R;
import com.zack.enderplan.injector.component.DaggerGuideComponent;
import com.zack.enderplan.injector.module.GuidePresenterModule;
import com.zack.enderplan.view.fragment.WelcomeFragment;
import com.zack.enderplan.view.contract.GuideViewContract;
import com.zack.enderplan.presenter.GuidePresenter;

import javax.inject.Inject;

public class GuideActivity extends BaseActivity implements GuideViewContract {

    @Inject
    GuidePresenter mGuidePresenter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, GuideActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGuidePresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerGuideComponent.builder()
                .guidePresenterModule(new GuidePresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onBackPressed() {
        mGuidePresenter.notifyBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGuidePresenter.detach();
    }

    @Override
    public void showInitialView() {
        setContentView(R.layout.activity_guide);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new WelcomeFragment()).commit();
    }

    @Override
    public void exit() {
        super.exit();
        //TODO 添加动画
    }
}
