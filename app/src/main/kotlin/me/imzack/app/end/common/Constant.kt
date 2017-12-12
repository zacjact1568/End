package me.imzack.app.end.common

// 这个类虽然全是常量，可以直接写，不放在类里，但是在其他类里使用的时候，每一个常量都要import一次，很麻烦
object Constant {

    val DEVELOPER_EMAIL = "zacjact1568@hotmail.com"

    val DB_TYPE_MARK = "type_mark.db"

    val PLAN_LIST_POSITION = "plan_list_position"
    val TYPE_LIST_POSITION = "type_list_position"

    val PLAN = "plan"
    val TYPE = "type"
    val CODE = "code"

    val CONTENT = "content"
    val TYPE_CODE = "type_code"
    val CREATION_TIME = "creation_time"
    val DEADLINE = "deadline"
    val COMPLETION_TIME = "completion_time"
    val STAR_STATUS = "star_status"
    val REMINDER_TIME = "reminder_time"

    val NAME = "name"
    val MARK = "mark"
    val MARK_COLOR = "mark_color"
    val MARK_PATTERN = "mark_pattern"
    val SEQUENCE = "sequence"

    val COLOR = "color"
    val COLOR_HEX = "color_hex"
    val COLOR_NAME = "color_%s"

    val PATTERN = "pattern"
    val PATTERN_FN = "pattern_fn"
    val PATTERN_NAME = "pattern_%s"

    val ZH_CN = "zh_cn"
    val ZH_TW = "zh_tw"
    val EN = "en"

    val OFF = "off"
    val ON = "on"
    val AUTO = "auto"
    val DEF = "def"

    val ONE_HOUR = "one_hour"
    val TOMORROW = "tomorrow"

    val FAB_COORDINATE = 44

    val GUIDE = "guide"
    val MY_PLANS = "my_plans"
    val ALL_TYPES = "all_types"
    val PLAN_CREATION = "plan_creation"
    val TYPE_CREATION = "type_creation"
    val PLAN_SEARCH = "plan_search"
    val TYPE_SEARCH = "type_search"
    val SETTINGS = "settings"
    val ABOUT = "about"

    val CURRENT_FRAGMENT = "current_fragment"

    val TRANSITION_NAME = "transition_name"
    val ENABLE_TRANSITION = "enable_transition"

    /** 128是刚好合适的大小，小的话在通知中会很模糊，大的话会有锯齿（系统会把图片缩放到固定的大小）  */
    val NOTIFICATION_LARGE_ICON_SIZE = 128

    val NOTIFICATION_ACTION = "notification_action"

    val ACTION_PREFIX = "me.imzack.app.end.action."
    val ACTION_REMINDER = ACTION_PREFIX + "REMINDER_%s"
    val ACTION_REMINDER_NOTIFICATION_ACTION = ACTION_PREFIX + "REMINDER_%s_NOTIFICATION_ACTION_%s"

    val PREF_KEY_NEED_GUIDE = "need_guide"
    val PREF_KEY_NIGHT_MODE = "night_mode"
    val PREF_KEY_DRAWER_HEADER_DISPLAY = "drawer_header_display"
    val PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY = "type_list_item_end_display"
    val PREF_KEY_NEED_NOTIFICATION_CHANNELS_INITIALIZATION = "need_notification_channels_initialization"

    val PREF_VALUE_DHD_UPC = "uc_plan_count"
    val PREF_VALUE_DHD_PC = "plan_count"
    val PREF_VALUE_DHD_TUPC = "today_uc_plan_count"

    val PREF_VALUE_TLIED_STUPC = "single_type_uc_plan_count"
    val PREF_VALUE_TLIED_STPC = "single_type_plan_count"

    val APP_BAR_STATE_EXPANDED = 0
    val APP_BAR_STATE_INTERMEDIATE = 1
    val APP_BAR_STATE_COLLAPSED = 2

    val SCROLL_EDGE_TOP = 0
    val SCROLL_EDGE_BOTTOM = 1
    val SCROLL_EDGE_MIDDLE = 2
    
    val VIEW_TYPE_HEADER = 0
    val VIEW_TYPE_ITEM = 1
    val VIEW_TYPE_FOOTER = 2

    val PLAN_PAYLOAD_CONTENT = 0
    val PLAN_PAYLOAD_TYPE_CODE = 1
    val PLAN_PAYLOAD_DEADLINE = 2
    val PLAN_PAYLOAD_STAR_STATUS = 3
    val PLAN_PAYLOAD_REMINDER_TIME = 4

    val TYPE_PAYLOAD_NAME = 0
    val TYPE_PAYLOAD_MARK_COLOR = 1
    val TYPE_PAYLOAD_MARK_PATTERN = 2
    val TYPE_PAYLOAD_PLAN_COUNT = 3

    val NOTIFICATION_CHANNEL_ID_REMINDER = "reminder"
}
