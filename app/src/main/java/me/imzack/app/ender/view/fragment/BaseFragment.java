package me.imzack.app.ender.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onInjectPresenter();
    }

    public void onInjectPresenter() {

    }

    protected String getFragmentName() {
        return getClass().getSimpleName();
    }

    public void showToast(@StringRes int msgResId) {
        Toast.makeText(getContext(), msgResId, Toast.LENGTH_SHORT).show();
    }

    protected void remove() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
