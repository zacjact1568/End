package me.imzack.app.ender.view.fragment;

public abstract class BaseListFragment extends BaseFragment {

    private OnListScrolledListener mOnListScrolledListener;

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
