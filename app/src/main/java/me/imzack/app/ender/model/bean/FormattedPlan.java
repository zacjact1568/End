package me.imzack.app.ender.model.bean;

/** The formatted plan object that is used to show plan details. */
public class FormattedPlan {

    private String content;
    private boolean isStarred;
    private int spinnerPos;
    private CharSequence deadline;
    private CharSequence reminderTime;
    private boolean isCompleted;

    public FormattedPlan(String content, boolean isStarred, int spinnerPos, CharSequence deadline,
                         CharSequence reminderTime, boolean isCompleted) {
        this.content = content;
        this.isStarred = isStarred;
        this.spinnerPos = spinnerPos;
        this.deadline = deadline;
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

    public CharSequence getDeadline() {
        return deadline;
    }

    public void setDeadline(CharSequence deadline) {
        this.deadline = deadline;
    }

    public CharSequence getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(CharSequence reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
