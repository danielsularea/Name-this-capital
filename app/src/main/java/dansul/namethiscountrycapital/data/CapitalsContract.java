package dansul.namethiscountrycapital.data;

import android.provider.BaseColumns;


public class CapitalsContract {

    public static abstract class CapitalsEntry implements BaseColumns {
        public static final String TABLE_NAME = "capitals";

        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_CAPITAL = "capital";
        public static final String COLUMN_CHOICE1 = "choice1";
        public static final String COLUMN_CHOICE2 = "choice2";
        public static final String COLUMN_CHOICE3 = "choice3";
        public static final String COLUMN_CHOICE4 = "choice4";
        public static final String COLUMN_CHOICE5 = "choice5";
    }
}
