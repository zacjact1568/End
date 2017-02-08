package com.zack.enderplan.view.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zack.enderplan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleGuidePageFragment extends BaseFragment {

    @BindView(R.id.image_subject)
    ImageView mSubjectImage;
    @BindView(R.id.text_title)
    TextView mTitleText;
    @BindView(R.id.text_dscpt)
    TextView mDscptText;

    private static final String ARG_IMAGE_RES_ID = "image_res_id";
    private static final String ARG_TITLE_RES_ID = "title_res_id";
    private static final String ARG_DSCPT_RES_ID = "dscpt_res_id";

    private int mImageResId, mTitleResId, mDscptResId;

    public static SimpleGuidePageFragment newInstance(@DrawableRes int imageResId, @StringRes int titleResId, @StringRes int descriptionResId) {
        SimpleGuidePageFragment fragment = new SimpleGuidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        args.putInt(ARG_TITLE_RES_ID, titleResId);
        args.putInt(ARG_DSCPT_RES_ID, descriptionResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mImageResId = args.getInt(ARG_IMAGE_RES_ID);
            mTitleResId = args.getInt(ARG_TITLE_RES_ID);
            mDscptResId = args.getInt(ARG_DSCPT_RES_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_guide_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mSubjectImage.setImageResource(mImageResId);
        mTitleText.setText(mTitleResId);
        mDscptText.setText(mDscptResId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
