package com.zack.enderplan.domain.fragment;

import android.support.v4.app.Fragment;

public abstract class BaseListFragment extends Fragment {

    protected OnListScrolledListener mOnListScrolledListener;

    public interface OnListScrolledListener {
        void onListScrolled(int variation);
    }

    public void setOnListScrolledListener(OnListScrolledListener listener) {
        mOnListScrolledListener = listener;
    }

    public void onListScrolled(int variation) {
        if (mOnListScrolledListener != null) {
            mOnListScrolledListener.onListScrolled(variation);
        }
    }
}
