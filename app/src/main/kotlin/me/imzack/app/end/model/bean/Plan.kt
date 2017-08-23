package me.imzack.app.end.model.bean

import android.os.Parcel
import android.os.Parcelable
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.util.CommonUtil

data class Plan(
        val code: String = CommonUtil.makeCode(),
        var content: String = "",
        var typeCode: String = DataManager.getType(0).code,
        var creationTime: Long = System.currentTimeMillis(),
        var deadline: Long = 0L,
        var completionTime: Long = 0L,
        var starStatus: Int = STAR_STATUS_NOT_STARRED,
        var reminderTime: Long = 0L
) : Parcelable {

    companion object {

        private val STAR_STATUS_NOT_STARRED = 0
        private val STAR_STATUS_STARRED = 1

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<Plan> {

            override fun createFromParcel(src: Parcel) = Plan(src)

            override fun newArray(size: Int): Array<Plan?> = arrayOfNulls(size)
        }
    }

    constructor(src: Parcel) : this(
            src.readString(),
            src.readString(),
            src.readString(),
            src.readLong(),
            src.readLong(),
            src.readLong(),
            src.readInt(),
            src.readLong()
    )

    val hasDeadline
        get() = deadline != 0L

    val isCompleted
        get() = completionTime != 0L

    val isStarred
        get() = starStatus == STAR_STATUS_STARRED

    val hasReminder
        get() = reminderTime != 0L

    fun invertStarStatus() {
        starStatus = if (isStarred) STAR_STATUS_NOT_STARRED else STAR_STATUS_STARRED
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(code)
        dest.writeString(content)
        dest.writeString(typeCode)
        dest.writeLong(creationTime)
        dest.writeLong(deadline)
        dest.writeLong(completionTime)
        dest.writeInt(starStatus)
        dest.writeLong(reminderTime)
    }
}
