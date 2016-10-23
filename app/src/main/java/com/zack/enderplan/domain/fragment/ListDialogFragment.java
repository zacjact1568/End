package com.zack.enderplan.domain.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zack.enderplan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link BottomSheetDialogFragment} that includes a {@link ListView}.
 * @author Zack
 */
public class ListDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.text_title)
    TextView mTitleText;
    @BindView(R.id.list_content)
    ListView mContentList;

    private static final String ARG_TITLE_TEXT = "title_text";

    private String mTitleTextStr;
    private BaseAdapter mAdapter;
    private OnListItemClickListener mOnListItemClickListener;

    public ListDialogFragment() {
        // Required empty public constructor
    }

    public static ListDialogFragment newInstance(String titleText) {
        ListDialogFragment fragment = new ListDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_TEXT, titleText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTitleTextStr = args.getString(ARG_TITLE_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (TextUtils.isEmpty(mTitleTextStr)) {
            mTitleText.setVisibility(View.GONE);
        } else {
            mTitleText.setText(mTitleTextStr);
        }
        if (mAdapter != null) {
            mContentList.setAdapter(mAdapter);
        }
        mContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnListItemClickListener != null) {
                    mOnListItemClickListener.onListItemClick(view, position);
                }
                getDialog().dismiss();
            }
        });
    }

    public void setListAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        if (mContentList != null) {
            mContentList.setAdapter(mAdapter);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        mOnListItemClickListener = listener;
        if (mContentList != null) {
            mContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mOnListItemClickListener.onListItemClick(view, position);
                    getDialog().dismiss();
                }
            });
        }
    }

    public void notifyListDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public interface OnListItemClickListener {
        void onListItemClick(View view, int position);
    }
}
