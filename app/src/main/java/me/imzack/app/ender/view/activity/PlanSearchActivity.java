package me.imzack.app.ender.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import me.imzack.app.ender.App;

import me.imzack.app.ender.R;
import me.imzack.app.ender.injector.component.DaggerPlanSearchComponent;
import me.imzack.app.ender.injector.module.PlanSearchPresenterModule;
import me.imzack.app.ender.presenter.PlanSearchPresenter;
import me.imzack.app.ender.util.ResourceUtil;
import me.imzack.app.ender.view.adapter.PlanSearchListAdapter;
import me.imzack.app.ender.view.contract.PlanSearchViewContract;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlanSearchActivity extends BaseActivity implements PlanSearchViewContract {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.editor_plan_search)
    EditText mPlanSearchEditor;
    @BindView(R.id.ic_clear_text)
    ImageView mClearTextIcon;
    @BindView(R.id.list_plan_search)
    RecyclerView mPlanSearchList;
    @BindView(R.id.img_no_input)
    ImageView mNoInputImage;
    @BindView(R.id.text_empty)
    TextView mEmptyText;

    @BindColor(R.color.colorPrimary)
    int mPrimaryColor;

    @Inject
    PlanSearchPresenter mPlanSearchPresenter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PlanSearchActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlanSearchPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerPlanSearchComponent.builder()
                .planSearchPresenterModule(new PlanSearchPresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlanSearchPresenter.detach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showInitialView(int planCount, PlanSearchListAdapter planSearchListAdapter) {
        setContentView(R.layout.activity_plan_search);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        mPlanSearchEditor.setHint(ResourceUtil.getQuantityString(R.string.hint_editor_plan_search, R.plurals.text_plan_count, planCount));
        mPlanSearchEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPlanSearchPresenter.notifySearchTextChanged(s.toString());
            }
        });

        mPlanSearchList.setAdapter(planSearchListAdapter);
        mPlanSearchList.setHasFixedSize(true);
    }

    @Override
    public void onSearchChanged(boolean isNoSearchInput, boolean isPlanSearchEmpty) {
        mClearTextIcon.setVisibility(isNoSearchInput ? View.GONE : View.VISIBLE);
        mPlanSearchList.setVisibility(isNoSearchInput || isPlanSearchEmpty ? View.GONE : View.VISIBLE);
        mNoInputImage.setVisibility(isNoSearchInput ? View.VISIBLE : View.GONE);
        mEmptyText.setVisibility(!isNoSearchInput && isPlanSearchEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPlanItemClicked(int planListPos) {
        PlanDetailActivity.start(this, planListPos);
        exit();
    }

    @OnClick({R.id.ic_clear_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_clear_text:
                mPlanSearchEditor.setText(null);
                break;
        }
    }
}
