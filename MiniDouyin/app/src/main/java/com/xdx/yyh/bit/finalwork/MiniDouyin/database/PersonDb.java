package com.xdx.yyh.bit.finalwork.MiniDouyin.database;

import android.provider.BaseColumns;

public final class PersonDb {

    public static  class PersonInfo implements BaseColumns{
        /**
         * 数据库信息
         */
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "MiniTikTok.db";
        /**
         * 信息表，及其字段
         */
        public static final String TABLE_NAME = "personInfo";
        public static final  String COLUMN_ID_TITLE = "id";
        public static final  String COLUMN_NUM_TITLE = "num";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_BIRTHDAY_TITLE = "birthday";
        public static final String COLUMN_DATE_TITLE = "date";
        public static final String COLUMN_PORTRAIT_TITLE = "portrait";

        /**
         * 时间字段的格式
         */
        public static final String DATE_FORMAT="YYYY-MM-DD";


    }
}
