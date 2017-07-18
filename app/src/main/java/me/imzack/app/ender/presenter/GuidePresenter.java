package me.imzack.app.ender.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import me.imzack.app.ender.R;
import me.imzack.app.ender.event.PlanCreatedEvent;
import me.imzack.app.ender.event.TypeCreatedEvent;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.model.bean.Plan;
import me.imzack.app.ender.model.bean.Type;
import me.imzack.app.ender.util.ColorUtil;
import me.imzack.app.ender.util.CommonUtil;
import me.imzack.app.ender.util.ResourceUtil;
import me.imzack.app.ender.view.adapter.GuidePagerAdapter;
import me.imzack.app.ender.view.contract.GuideViewContract;
import me.imzack.app.ender.view.fragment.SimpleGuidePageFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GuidePresenter extends BasePresenter {

    private GuideViewContract mGuideViewContract;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private GuidePagerAdapter mGuidePagerAdapter;
    private long mLastBackKeyPressedTime;

    @Inject
    GuidePresenter(GuideViewContract guideViewContract, FragmentManager fragmentManager, DataManager dataManager, EventBus eventBus) {
        mGuideViewContract = guideViewContract;
        mDataManager = dataManager;
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
                endGuide(true);
            }
        }
    }

    public void notifyPageSelected(int selectedPage) {
        mGuideViewContract.onPageSelected(selectedPage == 0, selectedPage == mGuidePagerAdapter.getCount() - 1);
    }

    public void notifyBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastBackKeyPressedTime < 1500) {
            endGuide(false);
        } else {
            mLastBackKeyPressedTime = currentTime;
            mGuideViewContract.showToast(R.string.toast_double_press_exit);
        }
    }

    private List<Fragment> getGuidePages() {
        List<Fragment> fragmentList = new ArrayList<>();
        //欢迎页
        fragmentList.add(new SimpleGuidePageFragment.Builder()
                .setImage(R.drawable.img_logo_with_bg)
                .setTitle(R.string.title_guide_page_welcome)
                .setDescription(R.string.text_slogan)
                .setButton(R.string.button_start, new SimpleGuidePageFragment.OnButtonClickListener() {
                    @Override
                    public void onClick(View v) {
                        endGuide(true);
                    }
                })
                .create()
        );
        //引导结束页
//        fragmentList.add(new SimpleGuidePageFragment.Builder()
//                .setImage(R.drawable.ic_check_black_24dp)
//                .setTitle(R.string.title_guide_page_ready)
//                .setDescription(R.string.dscpt_guide_page_ready)
//                .create()
//        );
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

    private void endGuide(boolean isNormally) {
        if (isNormally) {
            createDefaultData();
            mDataManager.getPreferenceHelper().setNeedGuideValue(false);
        }
        mGuideViewContract.exitWithResult(isNormally);
    }
}
