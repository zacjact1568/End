package com.zack.enderplan.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeFragment extends BaseFragment {

    @BindView(R.id.ic_logo)
    ImageView mLogoIcon;
    @BindView(R.id.text_title)
    TextView mTitleText;
    @BindView(R.id.text_slogan)
    TextView mSloganText;
    @BindView(R.id.btn_start)
    Button mStartButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick(R.id.btn_start)
    public void onClick() {
        makeMoveAnimation();
    }

    private void makeMoveAnimation() {
        Animation logoIconAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_logo_icon_exit_wf);
        logoIconAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, new FirstPlanFragment()).commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLogoIcon.startAnimation(logoIconAnim);

        Animation disappearAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_disappear_wf);
        mTitleText.startAnimation(disappearAnim);
        mSloganText.startAnimation(disappearAnim);
        mStartButton.startAnimation(disappearAnim);
    }
}
