package com.zack.enderplan.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.zack.enderplan.common.Constant;

public class Plan implements Parcelable {

    private static final int PLAN_STAR_STATUS_NOT_STARRED = 0;
    private static final int PLAN_STAR_STATUS_STARRED = 1;

    private String mPlanCode;
    private String mContent;
    private String mTypeCode;
    private long mCreationTime;
    private long mDeadline;
    private long mCompletionTime;
    private int mStarStatus;
    private long mReminderTime;

    public Plan(String planCode) {
        this.mPlanCode = planCode;
        this.mContent = null;
        this.mTypeCode = null;
        this.mCreationTime = Constant.TIME_UNDEFINED;
        this.mDeadline = Constant.TIME_UNDEFINED;
        this.mCompletionTime = Constant.TIME_UNDEFINED;
        this.mStarStatus = PLAN_STAR_STATUS_NOT_STARRED;
        this.mReminderTime = Constant.TIME_UNDEFINED;
    }

    public Plan(String planCode, String content, String typeCode, long creationTime, long deadline,
                long completionTime, int starStatus, long reminderTime) {
        this.mPlanCode = planCode;
        this.mContent = content;
        this.mTypeCode = typeCode;
        this.mCreationTime = creationTime;
        this.mDeadline = deadline;
        this.mCompletionTime = completionTime;
        this.mStarStatus = starStatus;
        this.mReminderTime = reminderTime;
    }

    protected Plan(Parcel in) {
        mPlanCode = in.readString();
        mContent = in.readString();
        mTypeCode = in.readString();
        mCreationTime = in.readLong();
        mDeadline = in.readLong();
        mCompletionTime = in.readLong();
        mStarStatus = in.readInt();
        mReminderTime = in.readLong();
    }

    public static final Creator<Plan> CREATOR = new Creator<Plan>() {
        @Override
        public Plan createFromParcel(Parcel in) {
            return new Plan(in);
        }

        @Override
        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };

    public String getPlanCode() {
        return mPlanCode;
    }

    public void setPlanCode(String planCode) {
        this.mPlanCode = planCode;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getTypeCode() {
        return mTypeCode;
    }

    public void setTypeCode(String typeCode) {
        this.mTypeCode = typeCode;
    }

    public long getCreationTime() {
        return mCreationTime;
    }

    public void setCreationTime(long creationTime) {
        this.mCreationTime = creationTime;
    }

    public long getDeadline() {
        return mDeadline;
    }

    public void setDeadline(long deadline) {
        this.mDeadline = deadline;
    }

    public long getCompletionTime() {
        return mCompletionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.mCompletionTime = completionTime;
    }

    public int getStarStatus() {
        return mStarStatus;
    }

    public void setStarStatus(int starStatus) {
        this.mStarStatus = starStatus;
    }

    public long getReminderTime() {
        return mReminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.mReminderTime = reminderTime;
    }

    public boolean hasDeadline() {
        return mDeadline != Constant.TIME_UNDEFINED;
    }

    public boolean isCompleted() {
        return mCompletionTime != Constant.TIME_UNDEFINED;
    }

    public boolean isStarred() {
        return mStarStatus == PLAN_STAR_STATUS_STARRED;
    }

    public boolean hasReminder() {
        return mReminderTime != Constant.TIME_UNDEFINED;
    }

    public void invertStarStatus() {
        setStarStatus(isStarred() ? PLAN_STAR_STATUS_NOT_STARRED : PLAN_STAR_STATUS_STARRED);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPlanCode);
        dest.writeString(mContent);
        dest.writeString(mTypeCode);
        dest.writeLong(mCreationTime);
        dest.writeLong(mDeadline);
        dest.writeLong(mCompletionTime);
        dest.writeInt(mStarStatus);
        dest.writeLong(mReminderTime);
    }
}
