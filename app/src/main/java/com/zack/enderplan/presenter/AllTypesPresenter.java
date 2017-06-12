package com.zack.enderplan.presenter;

import android.content.SharedPreferences;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.zack.enderplan.common.Constant;
import com.zack.enderplan.event.TypeDeletedEvent;
import com.zack.enderplan.event.DataLoadedEvent;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.PlanDeletedEvent;
import com.zack.enderplan.event.PlanDetailChangedEvent;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.event.TypeDetailChangedEvent;
import com.zack.enderplan.view.adapter.TypeListAdapter;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.view.callback.TypeListItemTouchCallback;
import com.zack.enderplan.view.contract.AllTypesViewContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class AllTypesPresenter extends BasePresenter implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AllTypesViewContract mAllTypesViewContract;
    private DataManager mDataManager;
    private TypeListAdapter mTypeListAdapter;
    private EventBus mEventBus;

    @Inject
    AllTypesPresenter(AllTypesViewContract allTypesViewContract, DataManager dataManager, EventBus eventBus) {
        mAllTypesViewContract = allTypesViewContract;
        mDataManager = dataManager;
        mEventBus = eventBus;

        mTypeListAdapter = new TypeListAdapter(mDataManager);
        mTypeListAdapter.setOnTypeItemClickListener(new TypeListAdapter.OnTypeItemClickListener() {
            @Override
            public void onTypeItemClick(int position, View typeItem) {
                mAllTypesViewContract.onTypeItemClicked(position, typeItem);
            }
        });
    }

    @Override
    public void attach() {
        mEventBus.register(this);
        mDataManager.getPreferenceHelper().registerOnChangeListener(this);

        TypeListItemTouchCallback typeListItemTouchCallback = new TypeListItemTouchCallback(mDataManager);
        typeListItemTouchCallback.setOnItemMovedListener(new TypeListItemTouchCallback.OnItemMovedListener() {
            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                mDataManager.swapTypesInTypeList(fromPosition, toPosition);
                mTypeListAdapter.notifyItemMoved(fromPosition, toPosition);
            }
        });

        mAllTypesViewContract.showInitialView(mTypeListAdapter, new ItemTouchHelper(typeListItemTouchCallback));
    }

    @Override
    public void detach() {
        mAllTypesViewContract = null;
        mDataManager.getPreferenceHelper().unregisterOnChangeListener(this);
        mEventBus.unregister(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY)) {
            //直接全部刷新
            mTypeListAdapter.notifyDataSetChanged();
        }
    }

    public void notifyPlanListScrolled(boolean top, boolean bottom) {
        int scrollEdge;
        if (top) {
            //触顶
            scrollEdge = TypeListAdapter.SCROLL_EDGE_TOP;
        } else if (bottom) {
            //触底
            scrollEdge = TypeListAdapter.SCROLL_EDGE_BOTTOM;
        } else {
            //中间
            scrollEdge = TypeListAdapter.SCROLL_EDGE_MIDDLE;
        }
        mTypeListAdapter.notifyListScrolled(scrollEdge);
    }

    @Subscribe
    public void onDataLoaded(DataLoadedEvent event) {
        mTypeListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onTypeCreated(TypeCreatedEvent event) {
        int position = mDataManager.getRecentlyCreatedTypeLocation();
        mTypeListAdapter.notifyItemInsertedAndChangingFooter(position);
        mAllTypesViewContract.onTypeCreated(position);
    }

    @Subscribe
    public void onPlanCreated(PlanCreatedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        //由于判断是否需要更新的逻辑略麻烦，简单起见，就不进行判断，直接更新了
        mTypeListAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(mDataManager.getPlan(event.getPosition()).getTypeCode()), TypeListAdapter.PAYLOAD_PLAN_COUNT);
    }

    @Subscribe
    public void onTypeDetailChanged(TypeDetailChangedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        Object payload = null;
        switch (event.getChangedField()) {
            case TypeDetailChangedEvent.FIELD_TYPE_NAME:
                payload = TypeListAdapter.PAYLOAD_TYPE_NAME;
                break;
            case TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR:
                payload = TypeListAdapter.PAYLOAD_TYPE_MARK_COLOR;
                break;
            case TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN:
                payload = TypeListAdapter.PAYLOAD_TYPE_MARK_PATTERN;
                break;
        }
        mTypeListAdapter.notifyItemChanged(event.getPosition(), payload);
    }

    @Subscribe
    public void onTypeDeleted(TypeDeletedEvent event) {
        mTypeListAdapter.notifyItemRemovedAndChangingFooter(event.getPosition());
    }

    @Subscribe
    public void onPlanDetailChanged(PlanDetailChangedEvent event) {
        if (event.getChangedField() == PlanDetailChangedEvent.FIELD_PLAN_STATUS || event.getChangedField() == PlanDetailChangedEvent.FIELD_TYPE_OF_PLAN) {
            //类型或完成情况改变后的刷新（其他改变未在此界面上呈现）
            //因为可能有多个item需要刷新，比较麻烦，所以直接全部刷新了
            mTypeListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onPlanDeleted(PlanDeletedEvent event) {
        if (event.getEventSource().equals(getPresenterName())) return;
        //由于判断是否需要更新的逻辑略麻烦，简单起见，就不进行判断，直接更新了
        mTypeListAdapter.notifyItemChanged(mDataManager.getTypeLocationInTypeList(event.getDeletedPlan().getTypeCode()), TypeListAdapter.PAYLOAD_PLAN_COUNT);
    }
}
