package com.zack.enderplan.view.fragment;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {

    public void showToast(@StringRes int msgResId) {
        Toast.makeText(getContext(), msgResId, Toast.LENGTH_SHORT).show();
    }

    protected void remove() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
