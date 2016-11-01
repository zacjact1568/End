package com.zack.enderplan.model.bean;

/** The formatted plan object that is used to show plan details. */
public class FormattedPlan {

    private String content;
    private boolean isStarred;
    private int spinnerPos;
    private boolean hasDeadline;
    private String deadline;
    private boolean hasReminder;
    private String reminderTime;
    private boolean isCompleted;

    public FormattedPlan(String content, boolean isStarred, int spinnerPos, boolean hasDeadline,
                         String deadline, boolean hasReminder, String reminderTime, boolean isCompleted) {
        this.content = content;
        this.isStarred = isStarred;
        this.spinnerPos = spinnerPos;
        this.hasDeadline = hasDeadline;
        this.deadline = deadline;
        this.hasReminder = hasReminder;
        this.reminderTime = reminderTime;
        this.isCompleted = isCompleted;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public int getSpinnerPos() {
        return spinnerPos;
    }

    public void setSpinnerPos(int spinnerPos) {
        this.spinnerPos = spinnerPos;
    }

    public boolean isHasDeadline() {
        return hasDeadline;
    }

    public void setHasDeadline(boolean hasDeadline) {
        this.hasDeadline = hasDeadline;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
