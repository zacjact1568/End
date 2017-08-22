package me.imzack.app.end.presenter

import android.graphics.Color
import android.text.TextUtils
import me.imzack.app.end.R
import me.imzack.app.end.event.TypeCreatedEvent
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.model.bean.FormattedType
import me.imzack.app.end.model.bean.Type
import me.imzack.app.end.model.bean.TypeMarkColor
import me.imzack.app.end.model.bean.TypeMarkPattern
import me.imzack.app.end.util.ColorUtil
import me.imzack.app.end.util.ResourceUtil
import me.imzack.app.end.util.StringUtil
import me.imzack.app.end.view.contract.TypeCreationViewContract
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class TypeCreationPresenter @Inject constructor(
        private var mTypeCreationViewContract: TypeCreationViewContract?,
        private val mEventBus: EventBus
) : BasePresenter() {

    private val mType = getNewType()

    override fun attach() {
        mTypeCreationViewContract!!.showInitialView(FormattedType(
                typeMarkColorInt = Color.parseColor(mType.markColor),
                typeMarkColorName = DataManager.getTypeMarkColorName(mType.markColor),
                typeName = mType.name,
                firstChar = StringUtil.getFirstChar(mType.name)
        ))
    }

    override fun detach() {
        mTypeCreationViewContract = null
    }

    fun notifySettingTypeName() {
        mTypeCreationViewContract!!.showTypeNameEditorDialog(mType.name)
    }

    fun notifyTypeNameEdited(typeName: String) {
        when {
            TextUtils.isEmpty(typeName) -> mTypeCreationViewContract!!.showToast(R.string.toast_empty_type_name)
            StringUtil.getLength(typeName) > 20 -> mTypeCreationViewContract!!.showToast(R.string.toast_longer_type_name)
            DataManager.isTypeNameUsed(typeName) -> mTypeCreationViewContract!!.showToast(R.string.toast_type_name_exists)
            else -> {
                mType.name = typeName
                mTypeCreationViewContract!!.onTypeNameChanged(typeName, StringUtil.getFirstChar(typeName))
            }
        }
    }

    fun notifySettingTypeMarkColor() {
        mTypeCreationViewContract!!.showTypeMarkColorPickerDialog(mType.markColor)
    }

    fun notifyTypeMarkColorSelected(typeMarkColor: TypeMarkColor) {
        val colorHex = typeMarkColor.colorHex
        //颜色必须唯一
        if (DataManager.isTypeMarkColorUsed(colorHex)) {
            mTypeCreationViewContract!!.showToast(R.string.toast_type_mark_color_exists)
        } else {
            mType.markColor = colorHex
            mTypeCreationViewContract!!.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.colorName)
        }
    }

    fun notifySettingTypeMarkPattern() {
        mTypeCreationViewContract!!.showTypeMarkPatternPickerDialog(mType.markPattern)
    }

    fun notifyTypeMarkPatternSelected(typeMarkPattern: TypeMarkPattern?) {
        val patternFn = typeMarkPattern?.patternFn
        mType.markPattern = patternFn
        mTypeCreationViewContract!!.onTypeMarkPatternChanged(
                mType.hasMarkPattern,
                if (patternFn != null) ResourceUtil.getDrawableResourceId(patternFn) else 0,
                typeMarkPattern?.patternName
        )
    }

    fun notifyCreateButtonClicked() {
        DataManager.notifyTypeCreated(mType)
        val position = DataManager.recentlyCreatedTypeLocation
        mEventBus.post(TypeCreatedEvent(
                presenterName,
                DataManager.getType(position).code,
                position
        ))
        mTypeCreationViewContract!!.exit()
    }

    fun notifyCancelButtonClicked() {
        //TODO 判断是否已编辑过
        mTypeCreationViewContract!!.exit()
    }

    private fun getNewType(): Type {
        //按顺序产生未使用过的新类型名称
        val base = ResourceUtil.getString(R.string.text_new_type_name)
        val typeName = StringBuilder(base)
        var i = 1
        while (DataManager.isTypeNameUsed(typeName.toString())) {
            if (base.length == typeName.length) {
                //还没加空格
                typeName.append(" ")
            }
            typeName.replace(base.length + 1, typeName.length, i.toString())
            i++
        }
        //随机产生未使用过的颜色
        var color: String
        while (true) {
            color = ColorUtil.makeColor()
            if (!DataManager.isTypeMarkColorUsed(color)) break
        }
        return Type(name = typeName.toString(), markColor = color)
    }
}
