package com.zack.enderplan.util;

public class Constant {

    public static final String DEVELOPER_EMAIL = "zacjact1568@hotmail.com";

    /** 未定义的时间 */
    public static final long UNDEFINED_TIME = -1L;

    public static final String DB_TYPE_MARK = "type_mark.db";

    public static final String PLAN_LIST_POSITION = "plan_list_position";
    public static final String TYPE_LIST_POSITION = "type_list_position";

    public static final String TYPE = "type";
    public static final String TYPE_CODE = "type_code";
    public static final String TYPE_NAME = "type_name";
    public static final String TYPE_MARK = "type_mark";
    public static final String TYPE_MARK_COLOR = "type_mark_color";
    public static final String TYPE_MARK_PATTERN = "type_mark_pattern";
    public static final String TYPE_SEQUENCE = "type_sequence";

    public static final String PLAN = "plan";
    public static final String PLAN_CODE = "plan_code";
    public static final String CONTENT = "content";
    public static final String CREATION_TIME = "creation_time";
    public static final String DEADLINE = "deadline";
    public static final String COMPLETION_TIME = "completion_time";
    public static final String STAR_STATUS = "star_status";
    public static final String REMINDER_TIME = "reminder_time";

    public static final String COLOR = "color";
    public static final String COLOR_HEX = "color_hex";
    public static final String COLOR_NAME = "color_%s";

    public static final String PATTERN = "pattern";
    public static final String PATTERN_FN = "pattern_fn";
    public static final String PATTERN_NAME = "pattern_%s";

    public static final String ZH_CN = "zh_cn";
    public static final String ZH_TW = "zh_tw";
    public static final String EN = "en";

    public static final String OFF = "off";
    public static final String ON = "on";
    public static final String AUTO = "auto";
    public static final String DEF = "def";

    public static final String ONE_HOUR = "one_hour";
    public static final String TOMORROW = "tomorrow";

    public static final int FAB_COORDINATE = 44;

    public static final String GUIDE = "guide";
    public static final String MY_PLANS = "my_plans";
    public static final String ALL_TYPES = "all_types";
    public static final String PLAN_CREATION = "plan_creation";
    public static final String TYPE_CREATION = "type_creation";
    public static final String SETTINGS = "settings";
    public static final String ABOUT = "about";

    public static final String CURRENT_FRAGMENT = "current_fragment";

    public static final String TRANSITION_NAME = "transition_name";

    /** 128是刚好合适的大小，小的话在通知中会很模糊，大的话会有锯齿（系统会把图片缩放到固定的大小） */
    public static final int NOTIFICATION_LARGE_ICON_SIZE = 128;

    public static final String NOTIFICATION_ACTION = "notification_action";

    public static final String ACTION_PREFIX = "com.zack.enderplan.action.";
    public static final String ACTION_REMINDER = ACTION_PREFIX + "REMINDER_%s";
    public static final String ACTION_REMINDER_NOTIFICATION_ACTION = ACTION_PREFIX + "REMINDER_%s_NOTIFICATION_ACTION_%s";
}
