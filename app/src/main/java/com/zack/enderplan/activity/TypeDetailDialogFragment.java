package com.zack.enderplan.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.manager.TypeManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TypeDetailDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_POSITION = "position";

    private int position;
    private TypeManager typeManager;
    private Type type;

    //private OnFragmentInteractionListener mListener;

    public TypeDetailDialogFragment() {
        // Required empty public constructor
    }

    public static TypeDetailDialogFragment newInstance(int position) {
        TypeDetailDialogFragment fragment = new TypeDetailDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }

        typeManager = TypeManager.getInstance();
        List<Type> typeList = typeManager.getTypeList();
        type = typeList.get(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_fragment_type_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CircleImageView typeMarkIcon = (CircleImageView) view.findViewById(R.id.ic_type_mark);
        TextView typeNameText = (TextView) view.findViewById(R.id.text_type_name);

        typeMarkIcon.setImageResource(typeManager.findColorResByTypeMark(type.getTypeMark()));
        typeNameText.setText(type.getTypeName());
    }

    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
