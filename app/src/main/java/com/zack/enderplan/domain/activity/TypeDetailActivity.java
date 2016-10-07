package com.zack.enderplan.domain.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zack.enderplan.R;
import com.zack.enderplan.domain.view.TypeDetailView;
import com.zack.enderplan.interactor.adapter.PlanSingleTypeAdapter;
import com.zack.enderplan.interactor.presenter.TypeDetailPresenter;
import com.zack.enderplan.model.bean.FormattedType;
import com.zack.enderplan.widget.CircleColorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeDetailActivity extends BaseActivity implements TypeDetailView {

    @BindView(R.id.layout_app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_header)
    LinearLayout mHeaderLayout;
    @BindView(R.id.ic_type_mark)
    CircleColorView typeMarkIcon;
    @BindView(R.id.text_type_name)
    TextView typeNameText;
    @BindView(R.id.text_uc_plan_count)
    TextView ucPlanCountText;
    @BindView(R.id.editor_content)
    EditText contentEditor;
    @BindView(R.id.list_uc_plan)
    RecyclerView ucPlanList;
    @BindView(R.id.layout_editor)
    FrameLayout mEditorLayout;

    private TypeDetailPresenter typeDetailPresenter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeDetailPresenter = new TypeDetailPresenter(this, getIntent().getIntExtra("position", -1));
        typeDetailPresenter.setInitialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        typeDetailPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_type_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_edit:
                //TODO add action
                break;
            case R.id.action_delete:
                //TODO 可能需要收起软键盘
                typeDetailPresenter.notifyTypeDeletionButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        typeDetailPresenter.notifyBackPressed();
    }

    @Override
    public void showInitialView(FormattedType formattedType, PlanSingleTypeAdapter planSingleTypeAdapter) {
        setContentView(R.layout.activity_type_detail);
        ButterKnife.bind(this);

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> typeDetailPresenter.notifyAppBarScrolled(verticalOffset, appBarLayout.getTotalScrollRange()));

        setSupportActionBar(toolbar);
        setupActionBar();

        typeMarkIcon.setFillColor(formattedType.getTypeMarkColorInt());
        typeMarkIcon.setInnerText(formattedType.getFirstChar());
        typeNameText.setText(formattedType.getTypeName());
        ucPlanCountText.setText(formattedType.getUcPlanCountStr());

        contentEditor.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                typeDetailPresenter.notifyPlanCreation(v.getText().toString());
            }
            return false;
        });

        planSingleTypeAdapter.setOnPlanItemClickListener(position -> typeDetailPresenter.notifyPlanItemClicked(position));

        planSingleTypeAdapter.setOnStarMarkIconClickListener(position -> typeDetailPresenter.notifyPlanStarStatusChanged(position));

        ucPlanList.setLayoutManager(new LinearLayoutManager(this));
        ucPlanList.setHasFixedSize(true);
        ucPlanList.setAdapter(planSingleTypeAdapter);
        new ItemTouchHelper(new SingleTypeUcPlanListItemTouchCallback()).attachToRecyclerView(ucPlanList);
    }

    @Override
    public void onPlanCreationSuccess(String ucPlanCountStr) {
        Toast.makeText(this, R.string.toast_create_plan_success, Toast.LENGTH_SHORT).show();
        ucPlanList.scrollToPosition(0);
        ucPlanCountText.setText(ucPlanCountStr);
        contentEditor.setText("");
    }

    @Override
    public void onPlanCreationFailed() {
        Toast.makeText(this, R.string.toast_create_plan_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUcPlanCountChanged(String ucPlanCountStr) {
        ucPlanCountText.setText(ucPlanCountStr);
    }

    @Override
    public void onPlanItemClicked(int posInPlanList) {
        Intent intent = new Intent(this, PlanDetailActivity.class);
        intent.putExtra("position", posInPlanList);
        startActivity(intent);
    }

    @Override
    public void changeHeaderOpacity(float alpha) {
        mHeaderLayout.setAlpha(alpha);
    }

    @Override
    public void changeTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void changeEditorVisibility(boolean isVisible) {
        Animator animator = AnimatorInflater.loadAnimator(this, isVisible ? R.animator.animator_editor_layout_enter_tda : R.animator.animator_editor_layout_exit_tda);
        animator.setTarget(mEditorLayout);
        animator.start();
    }

    @Override
    public void backToTop() {
        mAppBarLayout.setExpanded(true);
        //TODO 滑动到顶部
        //ucPlanList.scrollToPosition(0);
    }

    @Override
    public void pressBack() {
        super.onBackPressed();
    }

    @Override
    public void showToast(int msgResId) {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDeletionConfirmationDialog(String typeName) {
        new AlertDialog.Builder(this)
                .setTitle(typeName)
                .setMessage(R.string.msg_dialog_delete_type)
                .setPositiveButton(R.string.delete, (dialog, which) -> typeDetailPresenter.notifyDeletingType())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void exitTypeDetail() {
        finish();
    }

    @OnClick({R.id.ic_clear_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_clear_text:
                contentEditor.setText("");
                break;
        }
    }

    private class SingleTypeUcPlanListItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int swipeFlags = ItemTouchHelper.END;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getLayoutPosition();
            switch (direction) {
                case ItemTouchHelper.END:
                    typeDetailPresenter.notifyPlanCompleted(position);
                    break;
                default:
                    break;
            }
        }

        @Override
        public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
            return .6f;
        }
    }
}
