package me.imzack.app.end.presenter;

import android.graphics.Color;
import android.text.TextUtils;

import me.imzack.app.end.R;
import me.imzack.app.end.util.ColorUtil;
import me.imzack.app.end.util.CommonUtil;
import me.imzack.app.end.util.ResourceUtil;
import me.imzack.app.end.util.StringUtil;
import me.imzack.app.end.model.bean.FormattedType;
import me.imzack.app.end.model.bean.Type;
import me.imzack.app.end.model.bean.TypeMarkColor;
import me.imzack.app.end.event.TypeCreatedEvent;
import me.imzack.app.end.model.DataManager;
import me.imzack.app.end.model.bean.TypeMarkPattern;
import me.imzack.app.end.view.contract.TypeCreationViewContract;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class TypeCreationPresenter extends BasePresenter {

    private TypeCreationViewContract mTypeCreationViewContract;
    private DataManager mDataManager;
    private EventBus mEventBus;
    private Type mType;

    @Inject
    TypeCreationPresenter(TypeCreationViewContract typeCreationViewContract, DataManager dataManager, EventBus eventBus) {
        mTypeCreationViewContract = typeCreationViewContract;
        mDataManager = dataManager;
        mEventBus = eventBus;

        //按顺序产生未使用过的新类型名称
        String base = ResourceUtil.getString(R.string.text_new_type_name);
        StringBuilder typeName = new StringBuilder(base);
        int i = 1;
        while (mDataManager.isTypeNameUsed(typeName.toString())) {
            if (base.length() == typeName.length()) {
                //还没加空格
                typeName.append(" ");
            }
            typeName.replace(base.length() + 1, typeName.length(), String.valueOf(i));
            i++;
        }
        //随机产生未使用过的颜色
        String color;
        while (true) {
            color = ColorUtil.makeColor();
            if (!mDataManager.isTypeMarkColorUsed(color)) break;
        }
        mType = new Type(
                CommonUtil.makeCode(),
                typeName.toString(),
                color,
                mDataManager.getTypeCount()
        );
    }

    @Override
    public void attach() {
        mTypeCreationViewContract.showInitialView(new FormattedType(
                Color.parseColor(mType.getTypeMarkColor()),
                mDataManager.getTypeMarkColorName(mType.getTypeMarkColor()),
                mType.getTypeName(),
                StringUtil.getFirstChar(mType.getTypeName())
        ));
    }

    @Override
    public void detach() {
        mTypeCreationViewContract = null;
    }

    public void notifySettingTypeName() {
        mTypeCreationViewContract.showTypeNameEditorDialog(mType.getTypeName());
    }

    public void notifyTypeNameEdited(String typeName) {
        if (TextUtils.isEmpty(typeName)) {
            mTypeCreationViewContract.showToast(R.string.toast_empty_type_name);
        } else if (StringUtil.getLength(typeName) > 20) {
            mTypeCreationViewContract.showToast(R.string.toast_longer_type_name);
        } else if (mDataManager.isTypeNameUsed(typeName)) {
            mTypeCreationViewContract.showToast(R.string.toast_type_name_exists);
        } else {
            mType.setTypeName(typeName);
            mTypeCreationViewContract.onTypeNameChanged(typeName, StringUtil.getFirstChar(typeName));
        }
    }

    public void notifySettingTypeMarkColor() {
        mTypeCreationViewContract.showTypeMarkColorPickerDialog(mType.getTypeMarkColor());
    }

    public void notifyTypeMarkColorSelected(TypeMarkColor typeMarkColor) {
        String colorHex = typeMarkColor.getColorHex();
        //颜色必须唯一
        if (mDataManager.isTypeMarkColorUsed(colorHex)) {
            mTypeCreationViewContract.showToast(R.string.toast_type_mark_color_exists);
        } else {
            mType.setTypeMarkColor(colorHex);
            mTypeCreationViewContract.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.getColorName());
        }
    }

    public void notifySettingTypeMarkPattern() {
        mTypeCreationViewContract.showTypeMarkPatternPickerDialog(mType.getTypeMarkPattern());
    }

    public void notifyTypeMarkPatternSelected(TypeMarkPattern typeMarkPattern) {
        boolean hasPattern = typeMarkPattern != null;
        String patternFn = hasPattern ? typeMarkPattern.getPatternFn() : null;
        mType.setTypeMarkPattern(patternFn);
        mTypeCreationViewContract.onTypeMarkPatternChanged(
                hasPattern,
                ResourceUtil.getDrawableResourceId(patternFn),
                hasPattern ? typeMarkPattern.getPatternName() : null
        );
    }

    public void notifyCreateButtonClicked() {
        mDataManager.notifyTypeCreated(mType);
        int position = mDataManager.getRecentlyCreatedTypeLocation();
        mEventBus.post(new TypeCreatedEvent(
                getPresenterName(),
                mDataManager.getType(position).getTypeCode(),
                position
        ));
        mTypeCreationViewContract.exit();
    }

    public void notifyCancelButtonClicked() {
        //TODO 判断是否已编辑过
        mTypeCreationViewContract.exit();
    }
}
