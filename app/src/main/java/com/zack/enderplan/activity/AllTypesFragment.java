package com.zack.enderplan.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Type;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.manager.TypeManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.widget.TypeAdapter;

import java.util.Collections;
import java.util.List;

public class AllTypesFragment extends Fragment {

    private List<Type> typeList;
    private TypeAdapter typeAdapter;
    private EnderPlanDB enderplanDB;
    private RecyclerView recyclerView;

    //private static final String ARG_PLAN_COUNT_OF_EACH_TYPE = "plan_count_of_each_type";

    //private Bundle planCountOfEachType;

    public AllTypesFragment() {
        // Required empty public constructor
    }

    /*public static AllTypesFragment newInstance(Bundle planCountOfEachType) {
        AllTypesFragment fragment = new AllTypesFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PLAN_COUNT_OF_EACH_TYPE, planCountOfEachType);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            planCountOfEachType = getArguments().getBundle(ARG_PLAN_COUNT_OF_EACH_TYPE);
        }*/
        enderplanDB = EnderPlanDB.getInstance();
        TypeManager typeManager = TypeManager.getInstance();
        typeList = typeManager.getTypeList();
        typeAdapter = new TypeAdapter(getActivity(), typeList, typeManager.getPlanCountOfEachTypeMap());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_types, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(typeAdapter);
        new ItemTouchHelper(new TypeListItemTouchCallback()).attachToRecyclerView(recyclerView);
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public TypeAdapter getTypeAdapter() {
        return typeAdapter;
    }

    private class TypeListItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getLayoutPosition();
            int toPosition = target.getLayoutPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(typeList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(typeList, i, i - 1);
                }
            }
            typeAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //TODO 如果有存在的plan是这个type的话，会出错（判断count？=0）
            final int position = viewHolder.getLayoutPosition();
            final Type type = typeList.get(position);

            Util.makeShortVibrate();

            typeList.remove(position);
            typeAdapter.notifyItemRemoved(position);
            String text = type.getTypeName() + " " + getResources().getString(R.string.deleted_prompt);
            Snackbar snackbar = Snackbar.make(recyclerView, text, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeList.add(position, type);
                    typeAdapter.notifyItemInserted(position);
                }
            });
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event != DISMISS_EVENT_ACTION) {
                        enderplanDB.deleteType(type.getTypeCode());
                    }
                }
            });
            snackbar.show();
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return .7f;
        }

        @Override
        public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
            return .5f;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            }
        }
    }
}
