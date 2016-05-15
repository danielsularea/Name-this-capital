package dansul.namethiscountrycapital.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.LinkedList;
import java.util.List;

import dansul.namethiscountrycapital.OnLoadFinished;
import dansul.namethiscountrycapital.data.CapitalsContract.CapitalsEntry;


public class CapitalsDbHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "capitals.db";
    private static final int DATABASE_VERSION = 1;

    public CapitalsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void getDataAsync(OnLoadFinished callback) {
        new DbReadTask(callback).execute();
    }

    private class DbReadTask extends AsyncTask<Void, Void, List<CountryCapital>> {

        OnLoadFinished callback;

        public DbReadTask(OnLoadFinished callback) {
            this.callback = callback;
        }

        @Override
        protected List<CountryCapital> doInBackground(Void... params) {
            List<CountryCapital> countriesCapitals = null;
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.query(true,
                    CapitalsEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (c.moveToFirst()) {
                countriesCapitals = new LinkedList<>();

                while (!c.isLast()) {
                    String country = c.getString(c.getColumnIndex(CapitalsEntry.COLUMN_NAME_COUNTRY));
                    String capital = c.getString(c.getColumnIndex(CapitalsEntry.COLUMN_NAME_CAPITAL));
                    String choice1 = c.getString(c.getColumnIndex(CapitalsEntry.COLUMN_CHOICE1));
                    String choice2 = c.getString(c.getColumnIndex(CapitalsEntry.COLUMN_CHOICE2));
                    String choice3 = c.getString(c.getColumnIndex(CapitalsEntry.COLUMN_CHOICE3));
                    String choice4 = c.getString(c.getColumnIndex(CapitalsEntry.COLUMN_CHOICE4));
                    String choice5 = c.getString(c.getColumnIndex(CapitalsEntry.COLUMN_CHOICE5));
                    String[] choices = {choice1, choice2, choice3, choice4, choice5};

                    CountryCapital cc = new CountryCapital(country, capital, choices);
                    countriesCapitals.add(cc);
                    c.moveToNext();
                }
            }

            if (c != null) {
                c.close();
            }

            if (db != null) {
                db.close();
            }

            return countriesCapitals;
        }

        @Override
        protected void onPostExecute(List<CountryCapital> countries) {
            callback.finished(countries);
        }
    }
}
