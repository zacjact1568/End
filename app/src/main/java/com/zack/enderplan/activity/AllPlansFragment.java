package com.zack.enderplan.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zack.enderplan.R;
import com.zack.enderplan.bean.Plan;
import com.zack.enderplan.database.EnderPlanDB;
import com.zack.enderplan.manager.ReminderManager;
import com.zack.enderplan.manager.TypeManager;
import com.zack.enderplan.util.Util;
import com.zack.enderplan.widget.EnhancedRecyclerView;
import com.zack.enderplan.widget.PlanAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllPlansFragment extends Fragment {

    private static final String CLASS_NAME = "AllPlansFragment";

    private EnderPlanDB enderplanDB;
    private List<Plan> planList;
    private PlanAdapter planAdapter;
    private ReminderManager reminderManager;
    private TypeManager typeManager;
    private EnhancedRecyclerView recyclerView;
    private int uncompletedPlanCount;
    private int planItemClickPosition;

    private OnUncompletedPlanCountChangedListener onUncompletedPlanCountChangedListener;

    private static final int REQ_CODE_PLAN_DETAIL = 1;

    public AllPlansFragment() {
        // Required empty public constructor
    }

    /*public static AllPlansFragment newInstance(List<Plan> planList) {
        AllPlansFragment fragment = new AllPlansFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PLAN_LIST, (ArrayList<Plan>) planList);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enderplanDB = EnderPlanDB.getInstance();
        typeManager = TypeManager.getInstance();
        planList = new ArrayList<>();
        planAdapter = new PlanAdapter(planList);

        planAdapter.setOnPlanItemClickListener(new PlanAdapter.OnPlanItemClickListener() {
            @Override
            public void onPlanItemClick(View itemView, int position) {
                planItemClickPosition = position;
                Intent intent = new Intent(getActivity(), PlanDetailActivity.class);
                intent.putExtra("plan_detail", planList.get(position));
                startActivityForResult(intent, REQ_CODE_PLAN_DETAIL);
            }
        });

        planAdapter.setOnPlanItemLongClickListener(new PlanAdapter.OnPlanItemLongClickListener() {
            @Override
            public void onPlanItemLongClick(View itemView, int position) {
                planItemClickPosition = position;
                DateTimePickerDialogFragment dialog = DateTimePickerDialogFragment.newInstance(planList.get(position).getReminderTime());
                dialog.show(getFragmentManager(), "reminder");
            }
        });

        planAdapter.setOnStarMarkClickListener(new PlanAdapter.OnStarMarkClickListener() {
            @Override
            public void onStarMarkClick(ImageView starMark, int itemPosition) {
                Plan plan = planList.get(itemPosition);
                boolean isStarred = plan.getStarStatus() == Plan.PLAN_STAR_STATUS_STARRED;
                int newStarStatus = isStarred ? Plan.PLAN_STAR_STATUS_NOT_STARRED : Plan.PLAN_STAR_STATUS_STARRED;
                plan.setStarStatus(newStarStatus);
                starMark.setImageResource(isStarred ? R.drawable.ic_star_outline_grey600_24dp :
                        R.drawable.ic_star_color_accent_24dp);

                ContentValues values = new ContentValues();
                values.put("star_status", newStarStatus);
                enderplanDB.editPlan(plan.getPlanCode(), values);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_plans, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (EnhancedRecyclerView) view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(view.findViewById(R.id.text_empty_view));
        recyclerView.setAdapter(planAdapter);
        new LoadPlanTask().execute();
        new ItemTouchHelper(new PlanListItemTouchCallback()).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onUncompletedPlanCountChangedListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                break;
            case Activity.RESULT_FIRST_USER:
                Plan plan = planList.get(planItemClickPosition);
                planList.remove(planItemClickPosition);
                planAdapter.notifyItemRemoved(planItemClickPosition);
                if (plan.getCompletionTime() == 0) {
                    onUncompletedPlanCountChanged(--uncompletedPlanCount);
                }
                typeManager.updatePlanCountOfEachType(plan.getTypeCode(), -1);
                String text = plan.getContent() + " " + getResources().getString(R.string.deleted_prompt);
                Snackbar.make(recyclerView, text, Snackbar.LENGTH_SHORT).show();
                break;
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case REQ_CODE_PLAN_DETAIL:
                        String fromTypeCode = planList.get(planItemClickPosition).getTypeCode();
                        Plan editedPlan = data.getParcelableExtra("plan_detail");
                        planList.set(planItemClickPosition, editedPlan);
                        planAdapter.notifyItemChanged(planItemClickPosition);
                        typeManager.updatePlanCountOfEachType(fromTypeCode, editedPlan.getTypeCode());
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    public List<Plan> getPlanList() {
        return planList;
    }

    public PlanAdapter getPlanAdapter() {
        return planAdapter;
    }

    public int getUncompletedPlanCount() {
        return uncompletedPlanCount;
    }

    public void setUncompletedPlanCount(int uncompletedPlanCount) {
        this.uncompletedPlanCount = uncompletedPlanCount;
    }

    public int getPlanItemClickPosition() {
        return planItemClickPosition;
    }

    public void setPlanItemClickPosition(int planItemClickPosition) {
        this.planItemClickPosition = planItemClickPosition;
    }

    private ReminderManager getReminderManager() {
        if (reminderManager == null) {
            reminderManager = new ReminderManager(getActivity());
        }
        return reminderManager;
    }

    private class LoadPlanTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            planList.addAll(enderplanDB.loadPlan());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            planAdapter.notifyDataSetChanged();

            //计算未完成的Plan的数量
            uncompletedPlanCount = 0;
            for (Plan plan : planList) {
                if (plan.getCreationTime() == 0) {
                    break;
                }
                uncompletedPlanCount++;
            }
            onUncompletedPlanCountChanged(uncompletedPlanCount);

            //计算具有各个Type的Plan数量
            typeManager.initPlanCountOfEachTypeMap(planList);
        }
    }

    private class PlanListItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getLayoutPosition();
            final Plan plan = planList.get(position);
            final boolean isReminderEnabled = plan.getReminderTime() != 0;
            final boolean isCompleted = plan.getCompletionTime() != 0;

            if (!isCompleted && isReminderEnabled) {
                getReminderManager().cancelAlarm(plan.getPlanCode());
            }

            planList.remove(position);
            planAdapter.notifyItemRemoved(position);

            switch (direction) {
                case ItemTouchHelper.START:
                    //Delete
                    Util.makeShortVibrate();

                    if (!isCompleted) {
                        onUncompletedPlanCountChanged(--uncompletedPlanCount);
                    }

                    String text = plan.getContent() + " " + getResources().getString(R.string.deleted_prompt);
                    Snackbar snackbar = Snackbar.make(recyclerView, text, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isCompleted) {
                                onUncompletedPlanCountChanged(++uncompletedPlanCount);
                            }
                            if (isReminderEnabled) {
                                getReminderManager().setAlarm(plan.getPlanCode(), plan.getDeadline());
                            }
                            planList.add(position, plan);
                            planAdapter.notifyItemInserted(position);
                        }
                    });
                    snackbar.setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if (event != DISMISS_EVENT_ACTION) {
                                enderplanDB.deletePlan(plan.getPlanCode());
                                typeManager.updatePlanCountOfEachType(plan.getTypeCode(), -1);
                            }
                        }
                    });
                    snackbar.show();

                    break;
                case ItemTouchHelper.END:
                    //Complete
                    long currentTimeMillis = System.currentTimeMillis();

                    long newCreationTime = isCompleted ? currentTimeMillis : 0;
                    long newCompletionTime = isCompleted ? 0 : currentTimeMillis;

                    plan.setCreationTime(newCreationTime);
                    plan.setCompletionTime(newCompletionTime);

                    uncompletedPlanCount = uncompletedPlanCount + (isCompleted ? 1 : -1);
                    onUncompletedPlanCountChanged(uncompletedPlanCount);

                    int newPosition = isCompleted ? 0 : uncompletedPlanCount;
                    planList.add(newPosition, plan);
                    planAdapter.notifyItemInserted(newPosition);

                    ContentValues values = new ContentValues();
                    values.put(EnderPlanDB.DB_STR_CREATION_TIME, newCreationTime);
                    values.put(EnderPlanDB.DB_STR_COMPLETION_TIME, newCompletionTime);
                    enderplanDB.editPlan(plan.getPlanCode(), values);

                    break;
                default:
                    break;
            }
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return .7f;
        }
    }

    public void onUncompletedPlanCountChanged(int newUncompletedPlanCount) {
        if (onUncompletedPlanCountChangedListener != null) {
            onUncompletedPlanCountChangedListener.onUncompletedPlanCountChanged(newUncompletedPlanCount);
        }
    }

    public interface OnUncompletedPlanCountChangedListener {
        void onUncompletedPlanCountChanged(int newUncompletedPlanCount);
    }

    public void setOnUncompletedPlanCountChangedListener(OnUncompletedPlanCountChangedListener listener) {
        this.onUncompletedPlanCountChangedListener = listener;
    }
}
