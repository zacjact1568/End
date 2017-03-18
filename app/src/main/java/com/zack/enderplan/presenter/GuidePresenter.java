package com.zack.enderplan.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.zack.enderplan.R;
import com.zack.enderplan.event.PlanCreatedEvent;
import com.zack.enderplan.event.TypeCreatedEvent;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.bean.Plan;
import com.zack.enderplan.model.bean.Type;
import com.zack.enderplan.model.preference.PreferenceHelper;
import com.zack.enderplan.util.ColorUtil;
import com.zack.enderplan.util.CommonUtil;
import com.zack.enderplan.util.ResourceUtil;
import com.zack.enderplan.view.adapter.GuidePagerAdapter;
import com.zack.enderplan.view.contract.GuideViewContract;
import com.zack.enderplan.view.fragment.SimpleGuidePageFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GuidePresenter extends BasePresenter {

    private GuideViewContract mGuideViewContract;
    private DataManager mDataManager;
    private PreferenceHelper mPreferenceHelper;
    private EventBus mEventBus;
    private GuidePagerAdapter mGuidePagerAdapter;
    private long mLastBackKeyPressedTime;

    @Inject
    GuidePresenter(GuideViewContract guideViewContract, FragmentManager fragmentManager, DataManager dataManager, PreferenceHelper preferenceHelper, EventBus eventBus) {
        mGuideViewContract = guideViewContract;
        mDataManager = dataManager;
        mPreferenceHelper = preferenceHelper;
        mEventBus = eventBus;

        mGuidePagerAdapter = new GuidePagerAdapter(fragmentManager, getGuidePages());
    }

    @Override
    public void attach() {
        mGuideViewContract.showInitialView(mGuidePagerAdapter);
    }

    @Override
    public void detach() {
        mGuideViewContract = null;
    }

    public void notifyNavigationButtonClicked(boolean isLastPageButton, int currentPage) {
        if (isLastPageButton) {
            if (currentPage != 0) {
                mGuideViewContract.navigateToPage(currentPage - 1);
            }
        } else {
            if (currentPage != mGuidePagerAdapter.getCount() - 1) {
                //不是最后一页
                mGuideViewContract.navigateToPage(currentPage + 1);
            } else {
                //最后一页
                createDefaultData();
                mPreferenceHelper.setNeedGuideValue(false);
                mGuideViewContract.exitWithResult(true);
            }
        }
    }

    public void notifyPageSelected(int selectedPage) {
        mGuideViewContract.onPageSelected(selectedPage == 0, selectedPage == mGuidePagerAdapter.getCount() - 1);
    }

    public void notifyBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastBackKeyPressedTime < 1500) {
            mGuideViewContract.exitWithResult(false);
        } else {
            mLastBackKeyPressedTime = currentTime;
            mGuideViewContract.showToast(R.string.toast_double_click_exit);
        }
    }

    private List<Fragment> getGuidePages() {
        List<Fragment> fragmentList = new ArrayList<>();
        //欢迎页
        fragmentList.add(SimpleGuidePageFragment.newInstance(R.drawable.ic_check_circle_black_24dp, R.string.title_guide_page_welcome, R.string.text_slogan));
        //引导结束页
        //fragmentList.add(SimpleGuidePageFragment.newInstance(R.drawable.ic_check_black_24dp, R.string.title_guide_page_ready, R.string.dscpt_guide_page_ready));
        return fragmentList;
    }

    private void createDefaultData() {
        //Default types
        int[] nameResIds = {R.string.def_type_1, R.string.def_type_2, R.string.def_type_3, R.string.def_type_4};
        int[] colorResIds = {R.color.indigo, R.color.red, R.color.orange, R.color.green};
        String[] patternFns = {"ic_computer_black_24dp", "ic_home_black_24dp", "ic_work_black_24dp", "ic_school_black_24dp"};
        for (int i = 0; i < 4; i++) {
            Type type = new Type(
                    CommonUtil.makeCode(),
                    ResourceUtil.getString(nameResIds[i]),
                    ColorUtil.parseColor(ResourceUtil.getColor(colorResIds[i])),
                    patternFns[i],
                    i
            );
            mDataManager.notifyTypeCreated(type);
            mEventBus.post(new TypeCreatedEvent(getPresenterName(), type.getTypeCode(), mDataManager.getRecentlyCreatedTypeLocation()));
        }

        //Default plans
        int[] contentResIds = {R.string.def_plan_4, R.string.def_plan_3, R.string.def_plan_2, R.string.def_plan_1};
        String defaultTypeCode = mDataManager.getType(0).getTypeCode();
        for (int i = 0; i < 4; i++) {
            Plan plan = new Plan(CommonUtil.makeCode());
            plan.setContent(ResourceUtil.getString(contentResIds[i]));
            plan.setTypeCode(defaultTypeCode);
            plan.setCreationTime(System.currentTimeMillis());
            mDataManager.notifyPlanCreated(plan);
            mEventBus.post(new PlanCreatedEvent(getPresenterName(), plan.getPlanCode(), mDataManager.getRecentlyCreatedPlanLocation()));
        }
    }
}
