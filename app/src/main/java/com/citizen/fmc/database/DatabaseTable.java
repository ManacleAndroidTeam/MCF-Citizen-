package com.citizen.fmc.database;

import android.provider.BaseColumns;

/**
 * ======> Created by dheeraj-gangwar on 2018-03-16 <======
 */

public class DatabaseTable {

    public abstract class NotificationTable implements BaseColumns {
        public static final String TABLE_NAME = "notification_table";

        public static final String COLUMN_NAME_NOTIFICATION_MESSAGE_ID = "message_id";
        public static final String COLUMN_NAME_NOTIFICATION_MESSAGE_FROM = "message_from";
        public static final String COLUMN_NAME_NOTIFICATION_DATE_TIME_STAMP = "message_date_time";
        public static final String COLUMN_NAME_NOTIFICATION_TITLE = "message_title";
        public static final String COLUMN_NAME_NOTIFICATION_BODY = "message_body";
        public static final String COLUMN_NAME_NOTIFICATION_IMAGE = "message_image";
    }

    public abstract class GenderTable implements BaseColumns {
        public static final String TABLE_NAME = "gender_table";

        public static final String COLUMN_NAME_GENDER_ID = "id";
        public static final String COLUMN_NAME_GENDER_TITLE = "title";
    }

    public abstract class aqiTable implements BaseColumns {
        public static final String TABLE_NAME = "aqi_table";

        public static final String COLUMN_AQI_CITY = "city";
        public static final String COLUMN_AQI_COUNTRY = "country";
        public static final String COLUMN_AQI_LOCATIONS = "locations";
        public static final String COLUMN_AQI_COUNT = "number";
    }
}
