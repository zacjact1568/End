package net.zackzhang.app.end.presenter

import android.graphics.Color
import android.text.TextUtils
import net.zackzhang.app.end.R
import net.zackzhang.app.end.event.TypeDetailChangedEvent
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.model.bean.FormattedType
import net.zackzhang.app.end.model.bean.TypeMarkColor
import net.zackzhang.app.end.model.bean.TypeMarkPattern
import net.zackzhang.app.end.util.CommonUtil
import net.zackzhang.app.end.util.ResourceUtil
import net.zackzhang.app.end.util.StringUtil
import net.zackzhang.app.end.view.contract.TypeEditViewContract
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class TypeEditPresenter @Inject constructor(
        private var mTypeEditViewContract: TypeEditViewContract?,
        private val mTypeListPosition: Int,
        private val mEventBus: EventBus
) : BasePresenter() {
    
    private val mType = DataManager.getType(mTypeListPosition)

    override fun attach() {
        mTypeEditViewContract!!.showInitialView(FormattedType(
                // TODO 处理...
                Color.parseColor(mType.markColor),
                DataManager.getTypeMarkColorName(mType.markColor),
                mType.hasMarkPattern,
                if (mType.hasMarkPattern) ResourceUtil.getDrawableResourceId(mType.markPattern!!) else 0,
                DataManager.getTypeMarkPatternName(mType.markPattern),
                mType.name,
                StringUtil.getFirstChar(mType.name)
        ))
    }

    override fun detach() {
        mTypeEditViewContract = null
    }

    fun notifySettingTypeName() {
        mTypeEditViewContract!!.showTypeNameEditorDialog(mType.name)
    }

    fun notifySettingTypeMarkColor() {
        // TODO 处理...
        mTypeEditViewContract!!.showTypeMarkColorPickerDialog(Color.parseColor(mType.markColor))
    }

    fun notifySettingTypeMarkPattern() {
        mTypeEditViewContract!!.showTypeMarkPatternPickerDialog(mType.markPattern)
    }

    fun notifyUpdatingTypeName(newTypeName: String) {
        when {
            mType.name == newTypeName -> return
            TextUtils.isEmpty(newTypeName) -> mTypeEditViewContract!!.showToast(R.string.toast_empty_type_name)
            StringUtil.getLength(newTypeName) > 20 -> mTypeEditViewContract!!.showToast(R.string.toast_longer_type_name)
            DataManager.isTypeNameUsed(newTypeName) -> mTypeEditViewContract!!.showToast(R.string.toast_type_name_exists)
            else -> {
                DataManager.notifyUpdatingTypeName(mTypeListPosition, newTypeName)
                mTypeEditViewContract!!.onTypeNameChanged(newTypeName, StringUtil.getFirstChar(newTypeName))
                postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_NAME)
            }
        }
    }

    fun notifyTypeMarkColorSelected(typeMarkColor: TypeMarkColor) {
        val colorHex = typeMarkColor.hex
        if (mType.markColor == colorHex) return
        //上一行已经把此类型当前的颜色排除了，所以不存在选择相同的颜色还弹toast的问题
        if (DataManager.isTypeMarkColorUsed(colorHex)) {
            mTypeEditViewContract!!.showToast(R.string.toast_type_mark_color_exists)
        } else {
            DataManager.notifyUpdatingTypeMarkColor(mTypeListPosition, colorHex)
            mTypeEditViewContract!!.onTypeMarkColorChanged(Color.parseColor(colorHex), typeMarkColor.name)
            postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_COLOR)
        }
    }

    fun notifyTypeMarkPatternSelected(typeMarkPattern: TypeMarkPattern?) {
        val patternFn = typeMarkPattern?.file
        if (CommonUtil.isObjectEqual(mType.markPattern, patternFn)) return
        DataManager.notifyUpdatingTypeMarkPattern(mTypeListPosition, patternFn)
        mTypeEditViewContract!!.onTypeMarkPatternChanged(
                mType.hasMarkPattern,
                if (patternFn != null) ResourceUtil.getDrawableResourceId(patternFn) else 0,
                typeMarkPattern?.name
        )
        postTypeDetailChangedEvent(TypeDetailChangedEvent.FIELD_TYPE_MARK_PATTERN)
    }

    private fun postTypeDetailChangedEvent(changedField: Int) {
        mEventBus.post(TypeDetailChangedEvent(presenterName, mType.code, mTypeListPosition, changedField))
    }
}
