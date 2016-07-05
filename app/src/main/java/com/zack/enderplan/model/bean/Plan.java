package com.zack.enderplan.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class Plan implements Parcelable {

    public static final int PLAN_STAR_STATUS_NOT_STARRED = 0;
    public static final int PLAN_STAR_STATUS_STARRED = 1;

    private String planCode, content, typeCode;
    private long creationTime, deadline, completionTime, reminderTime;
    private int starStatus;

    public Plan(String planCode) {
        this.planCode = planCode;
        this.content = null;
        this.typeCode = null;
        this.creationTime = 0;
        this.deadline = 0;
        this.completionTime = 0;
        this.starStatus = PLAN_STAR_STATUS_NOT_STARRED;
        this.reminderTime = 0;
    }

    public Plan(String planCode, String content, String typeCode, long creationTime, long deadline,
                long completionTime, int starStatus, long reminderTime) {
        this.planCode = planCode;
        this.content = content;
        this.typeCode = typeCode;
        this.creationTime = creationTime;
        this.deadline = deadline;
        this.completionTime = completionTime;
        this.starStatus = starStatus;
        this.reminderTime = reminderTime;
    }

    protected Plan(Parcel in) {
        planCode = in.readString();
        content = in.readString();
        typeCode = in.readString();
        creationTime = in.readLong();
        deadline = in.readLong();
        completionTime = in.readLong();
        starStatus = in.readInt();
        reminderTime = in.readLong();
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
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public int getStarStatus() {
        return starStatus;
    }

    public void setStarStatus(int starStatus) {
        this.starStatus = starStatus;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(planCode);
        dest.writeString(content);
        dest.writeString(typeCode);
        dest.writeLong(creationTime);
        dest.writeLong(deadline);
        dest.writeLong(completionTime);
        dest.writeInt(starStatus);
        dest.writeLong(reminderTime);
    }

    @Override
    public String toString() {
        String format = "Plan Code: %s%nContent: %s%nType Code: %s%nCreation Time: %d%nDeadline: %d%nCompletion Time: %d%nStar Status: %d%nReminder Time: %d";
        return String.format(Locale.getDefault(), format,
                planCode, content, typeCode, creationTime, deadline, completionTime, starStatus, reminderTime);
    }
}
