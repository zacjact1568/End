package com.zack.enderplan.domain.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.fragment.WelcomeFragment;
import com.zack.enderplan.domain.view.GuideView;
import com.zack.enderplan.interactor.presenter.GuidePresenter;

public class GuideActivity extends BaseActivity implements GuideView {

    private static final String LOG_TAG = "GuideActivity";

    private GuidePresenter mGuidePresenter;

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
    public void navigateBack() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void endGuide() {
        finish();
    }
}
