package me.imzack.app.end.view.activity;

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

import me.imzack.app.end.App;

import me.imzack.app.end.R;
import me.imzack.app.end.injector.component.DaggerTypeSearchComponent;
import me.imzack.app.end.injector.module.TypeSearchPresenterModule;
import me.imzack.app.end.presenter.TypeSearchPresenter;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.view.adapter.TypeSearchListAdapter;
import me.imzack.app.end.view.contract.TypeSearchViewContract;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeSearchActivity extends BaseActivity implements TypeSearchViewContract {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.editor_type_search)
    EditText mTypeSearchEditor;
    @BindView(R.id.ic_clear_text)
    ImageView mClearTextIcon;
    @BindView(R.id.list_type_search)
    RecyclerView mTypeSearchList;
    @BindView(R.id.img_no_input)
    ImageView mNoInputImage;
    @BindView(R.id.text_empty)
    TextView mEmptyText;

    @BindColor(R.color.colorPrimary)
    int mPrimaryColor;

    @Inject
    TypeSearchPresenter mTypeSearchPresenter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TypeSearchActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeSearchPresenter.attach();
    }

    @Override
    protected void onInjectPresenter() {
        DaggerTypeSearchComponent.builder()
                .typeSearchPresenterModule(new TypeSearchPresenterModule(this))
                .appComponent(App.getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTypeSearchPresenter.detach();
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
    public void showInitialView(int typeCount, TypeSearchListAdapter typeSearchListAdapter) {
        setContentView(R.layout.activity_type_search);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupActionBar();

        mTypeSearchEditor.setHint(ResourceUtil.getQuantityString(R.string.hint_editor_type_search, R.plurals.text_type_count, typeCount));
        mTypeSearchEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTypeSearchPresenter.notifySearchTextChanged(s.toString());
            }
        });

        mTypeSearchList.setAdapter(typeSearchListAdapter);
        mTypeSearchList.setHasFixedSize(true);
    }

    @Override
    public void onSearchChanged(boolean isNoSearchInput, boolean isTypeSearchEmpty) {
        mClearTextIcon.setVisibility(isNoSearchInput ? View.GONE : View.VISIBLE);
        mTypeSearchList.setVisibility(isNoSearchInput || isTypeSearchEmpty ? View.GONE : View.VISIBLE);
        mNoInputImage.setVisibility(isNoSearchInput ? View.VISIBLE : View.GONE);
        mEmptyText.setVisibility(!isNoSearchInput && isTypeSearchEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTypeItemClicked(int typeListPos) {
        TypeDetailActivity.start(this, typeListPos, false, null, null);
        exit();
    }

    @OnClick({R.id.ic_clear_text})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_clear_text:
                mTypeSearchEditor.setText(null);
                break;
        }
    }
}
