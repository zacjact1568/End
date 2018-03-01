package me.imzack.app.end.common

// 这个类虽然全是常量，可以直接写，不放在类里，但是在其他类里使用的时候，每一个常量都要import一次，很麻烦
object Constant {

    const val DEVELOPER_EMAIL = "zxjue@outlook.com"

    const val TYPE_MARK_DB_FN = "db_type_mark.db"

    const val PLAN_LIST_POSITION = "plan_list_position"
    const val TYPE_LIST_POSITION = "type_list_position"

    const val PLAN = "plan"
    const val TYPE = "type"
    const val CODE = "code"

    const val CONTENT = "content"
    const val TYPE_CODE = "type_code"
    const val CREATION_TIME = "creation_time"
    const val DEADLINE = "deadline"
    const val COMPLETION_TIME = "completion_time"
    const val STAR_STATUS = "star_status"
    const val REMINDER_TIME = "reminder_time"

    const val NAME = "name"
    const val MARK = "mark"
    const val MARK_COLOR = "mark_color"
    const val MARK_PATTERN = "mark_pattern"
    const val SEQUENCE = "sequence"

    const val COLOR = "color"
    const val HEX = "hex"
    const val NAME_LOCALE = "name_%s"

    const val PATTERN = "pattern"
    const val FILE = "file"

    const val ZH_CN = "zh_cn"
    const val ZH_TW = "zh_tw"
    const val EN = "en"

    const val OFF = "off"
    const val ON = "on"
    const val AUTO = "auto"
    const val DEF = "def"

    const val ONE_HOUR = "one_hour"
    const val TOMORROW = "tomorrow"

    const val FAB_COORDINATE = 44

    const val GUIDE = "guide"
    const val MY_PLANS = "my_plans"
    const val ALL_TYPES = "all_types"
    const val PLAN_CREATION = "plan_creation"
    const val TYPE_CREATION = "type_creation"
    const val PLAN_SEARCH = "plan_search"
    const val TYPE_SEARCH = "type_search"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    const val CURRENT_FRAGMENT = "current_fragment"

    const val TRANSITION_NAME = "transition_name"
    const val ENABLE_TRANSITION = "enable_transition"

    /** 128是刚好合适的大小，小的话在通知中会很模糊，大的话会有锯齿（系统会把图片缩放到固定的大小）  */
    const val NOTIFICATION_LARGE_ICON_SIZE = 128

    const val NOTIFICATION_ACTION = "notification_action"

    const val ACTION_PREFIX = "me.imzack.app.end.action."
    const val ACTION_REMINDER = ACTION_PREFIX + "REMINDER_%s"
    const val ACTION_REMINDER_NOTIFICATION_ACTION = ACTION_PREFIX + "REMINDER_%s_NOTIFICATION_ACTION_%s"

    const val PREF_KEY_NEED_GUIDE = "need_guide"
    const val PREF_KEY_NIGHT_MODE = "night_mode"
    const val PREF_KEY_DRAWER_HEADER_DISPLAY = "drawer_header_display"
    const val PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY = "type_list_item_end_display"
    const val PREF_KEY_NEED_NOTIFICATION_CHANNELS_INITIALIZATION = "need_notification_channels_initialization"

    const val PREF_VALUE_DHD_UPC = "uc_plan_count"
    const val PREF_VALUE_DHD_PC = "plan_count"
    const val PREF_VALUE_DHD_TUPC = "today_uc_plan_count"

    const val PREF_VALUE_TLIED_STUPC = "single_type_uc_plan_count"
    const val PREF_VALUE_TLIED_STPC = "single_type_plan_count"

    const val APP_BAR_STATE_EXPANDED = 0
    const val APP_BAR_STATE_INTERMEDIATE = 1
    const val APP_BAR_STATE_COLLAPSED = 2

    const val SCROLL_EDGE_TOP = 0
    const val SCROLL_EDGE_BOTTOM = 1
    const val SCROLL_EDGE_MIDDLE = 2
    
    const val VIEW_TYPE_HEADER = 0
    const val VIEW_TYPE_ITEM = 1
    const val VIEW_TYPE_FOOTER = 2

    const val PLAN_PAYLOAD_CONTENT = 0
    const val PLAN_PAYLOAD_TYPE_CODE = 1
    const val PLAN_PAYLOAD_DEADLINE = 2
    const val PLAN_PAYLOAD_STAR_STATUS = 3
    const val PLAN_PAYLOAD_REMINDER_TIME = 4

    const val TYPE_PAYLOAD_NAME = 0
    const val TYPE_PAYLOAD_MARK_COLOR = 1
    const val TYPE_PAYLOAD_MARK_PATTERN = 2
    const val TYPE_PAYLOAD_PLAN_COUNT = 3

    const val NOTIFICATION_CHANNEL_ID_REMINDER = "reminder"
}
