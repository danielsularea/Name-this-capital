package dansul.namethiscountrycapital;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import dansul.namethiscountrycapital.data.CountryCapital;


public class MainActivity extends Activity implements View.OnClickListener {

    protected final static int START_GAME_REQUEST = 1;

    private final static String PREF_SCORE = "dansul.namethiscountrycapital.SCORE";
    public final static String SCORE = "score";

    private List<CountryCapital> countries;
    private int score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start_game).setOnClickListener(this);
        findViewById(R.id.btn_settings).setOnClickListener(this);
        findViewById(R.id.btn_leaderboard).setOnClickListener(this);
        findViewById(R.id.btn_moreapps).setOnClickListener(this);

        // Initialize preference settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (response == RESULT_OK && request == START_GAME_REQUEST) {
            score = data.getIntExtra(SCORE, 0);

            if (score != 0) {
                SharedPreferences prefScore = getSharedPreferences(PREF_SCORE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefScore.edit();

                editor.putInt(SCORE, score);
                editor.commit();
            }

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        Uri site;

        switch (v.getId()) {
            case R.id.btn_start_game:
                PlayDialogFragment playChooser = new PlayDialogFragment();
                Bundle b = new Bundle();
                playChooser.show(getFragmentManager(), null);

                break;

            case R.id.btn_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                break;

            case R.id.btn_leaderboard:
                Toast.makeText(this, getString(R.string.feature_missing), Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_moreapps:
                site = Uri.parse("https://play.google.com/store/apps/developer?id=<ID>");

                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(site);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                break;
        }
    }
}
