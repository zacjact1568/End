package com.zack.enderplan.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.view.contract.FirstPlanViewContract;
import com.zack.enderplan.presenter.FirstPlanPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FirstPlanFragment extends BaseFragment implements FirstPlanViewContract {

    @BindView(R.id.ic_logo)
    ImageView mLogoIcon;
    @BindView(R.id.layout_editor)
    LinearLayout mEditorLayout;
    @BindView(R.id.editor_content)
    EditText mContentEditor;
    @BindView(R.id.fab_enter)
    FloatingActionButton mEnterFab;

    private FirstPlanPresenter mFirstPlanPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirstPlanPresenter = new FirstPlanPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mFirstPlanPresenter.setInitialView(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFirstPlanPresenter.detachView();
    }

    @Override
    public void showInitialView(boolean shouldShowEnterAnimation) {
        if (shouldShowEnterAnimation) {
            mEditorLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mEditorLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    makeEnterAnimation();
                    return false;
                }
            });
        }
    }

    @Override
    public void onDetectedEmptyContent() {
        Toast.makeText(getContext(), R.string.toast_empty_content, Toast.LENGTH_SHORT).show();
        mEnterFab.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake_fpf));
    }

    @Override
    public void onFirstPlanCreated() {
        makeExitAnimation();
    }

    @OnClick(R.id.fab_enter)
    public void onClick() {
        hideKeyboard();
        mFirstPlanPresenter.notifyEnterButtonClicked(mContentEditor.getText().toString());
    }

    private void makeEnterAnimation() {
        Animation editorLayoutEnterAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_editor_layout_enter_fpf);
        editorLayoutEnterAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showKeyboard();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mEditorLayout.startAnimation(editorLayoutEnterAnim);
    }

    private void makeExitAnimation() {
        mLogoIcon.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_logo_icon_exit_fpf));
        Animation editorLayoutExitAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_editor_layout_exit_fpf);
        editorLayoutExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFirstPlanPresenter.notifyExitAnimationEnded();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mEditorLayout.startAnimation(editorLayoutExitAnim);
    }

    private void showKeyboard() {
        mContentEditor.requestFocus();
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mContentEditor.hasFocus()) {
            manager.showSoftInput(mContentEditor, 0);
        }
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive(mContentEditor)) {
            manager.hideSoftInputFromWindow(mContentEditor.getWindowToken(), 0);
        }
    }

    @Override
    public void exit() {
        remove();
    }
}
