package dansul.namethiscountrycapital;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Daniel on 3/26/14.
 */
public class ScoresDialogFragment extends DialogFragment {

    private static final String FIRST_SCORE = "firstScore";
    private static final String SECOND_SCORE = "secondScore";
    private static final String THIRD_SCORE = "thirdScore";

    private static final String PREFERENCE_KEY = "dansul.namethiscountrycapital.SHARED_PREF";

    TextView score1, score2, score3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);

        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scores, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);

        score1 = (TextView) v.findViewById(R.id.firstScore);
        score2 = (TextView) v.findViewById(R.id.secondScore);
        score3 = (TextView) v.findViewById(R.id.thirdScore);

        int firstScore = prefs.getInt(FIRST_SCORE, 0);
        int secondScore = prefs.getInt(SECOND_SCORE, 0);
        int thirdScore = prefs.getInt(THIRD_SCORE, 0);

        score1.setText(String.valueOf(firstScore));
        score2.setText(String.valueOf(secondScore));
        score3.setText(String.valueOf(thirdScore));

        return v;
    }

}
