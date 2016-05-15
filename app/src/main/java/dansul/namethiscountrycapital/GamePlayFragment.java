package dansul.namethiscountrycapital;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import dansul.namethiscountrycapital.data.CapitalsDbHelper;
import dansul.namethiscountrycapital.data.CountryCapital;


public class GamePlayFragment extends Fragment implements View.OnClickListener, ImageLoadedListener {

    interface OnGameOverListener {
        void onGameOver(int score);
    }

    private static final String PREFERENCE_KEY = "dansul.namethiscountrycapital.SHARED_PREF";

    private String numberToPlay;
    private int counterToPlay;
    private boolean isGameOver = false;

    private TextView tvScore, tvCountry;
    private ImageView ivCountry;
    private Button btnFirstChoice, btnSecondChoice, btnThirdChoice;
    private Button btnClicked, btnCorrectChoice;

    private Bitmap bitmap;

    private CountryCapital currentCountry;

    // Make this static so we can access it from our activity
    protected static int SCORE;

    private int incorrectAnswersFlag;
    private static List<CountryCapital> remCountries, allCountries;

    // Handlers
    private Handler firstHandler, secondHandler;
    private OnGameOverListener gameOverListener;

    CapitalsDbHelper dbHelper;

    // Required empty public constructor
    public GamePlayFragment() {
        SCORE = incorrectAnswersFlag = 0;
        btnClicked = btnCorrectChoice = null;

        firstHandler = new Handler();
        secondHandler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gameplay, container, false);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            gameOverListener = (OnGameOverListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnGameOverListener.");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            gameOverListener = (OnGameOverListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGameOverListener.");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = new CapitalsDbHelper(getActivity());

        btnFirstChoice = (Button) getView().findViewById(R.id.btn_first_choice);
        btnSecondChoice = (Button) getView().findViewById(R.id.btn_second_choice);
        btnThirdChoice = (Button) getView().findViewById(R.id.btn_third_choice);

        btnFirstChoice.setOnClickListener(this);
        btnSecondChoice.setOnClickListener(this);
        btnThirdChoice.setOnClickListener(this);

        // Set custom typeface
        Typeface robotoCondensed = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RCR.ttf");
        btnFirstChoice.setTypeface(robotoCondensed);
        btnSecondChoice.setTypeface(robotoCondensed);
        btnThirdChoice.setTypeface(robotoCondensed);

        tvScore = (TextView) getView().findViewById(R.id.tv_score);
        tvScore.setTypeface(robotoCondensed);

        tvCountry = (TextView) getView().findViewById(R.id.tv_country);
        tvCountry.setTypeface(robotoCondensed);

        ivCountry = (ImageView) getView().findViewById(R.id.iv_country);

        // Get the number to play
        Intent intent = getActivity().getIntent();
        counterToPlay = intent.getIntExtra(GamePlayActivity.NUMBER_TO_PLAY, -1);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if we need to show the initial dialog
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);

        boolean showInitialDialog = prefs.getBoolean("showInitialDialog", true);

        // If this is the first time the app has ran, show the dialog and update the preference
        if (showInitialDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//            builder.setMessage(getActivity().getResources().getString(R.string.initial_dialog_message))
//                    .setCancelable(false)
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).create();
//
//            builder.show();

            SharedPreferences.Editor editor = prefs.edit();

            int highScore = prefs.getInt("highScore", 0);

            editor.putInt(MainActivity.SCORE, SCORE);
            editor.putBoolean("showInitialDialog", false);
            editor.apply();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if we need to hide the country label
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isCountryHidden = prefs.getBoolean(SettingsActivity.KEY_PREF_HIDE_COUNTRY, false);

        if (isCountryHidden) {
            tvCountry.setVisibility(View.INVISIBLE);
        }

        CapitalsDbHelper dbHelper = new CapitalsDbHelper(getActivity());
        dbHelper.getDataAsync(new OnLoadFinished() {

            @Override
            public void finished(List<CountryCapital> countries) {
                remCountries = countries;

                // Load image here but only if the game is not over
                if (!isGameOver) {
                    currentCountry = getRandomCountry();
                    loadImage(currentCountry.getCountry());
                }
            }
        });

//        // Load image here but only if the game is not over
//        if (!isGameOver) {
//            currentCountry = getRandomCountry();
//            loadImage(currentCountry.getCountry());
//        }
    }

    @Override
    public void onPause() {
        super.onPause();

        saveScoreToPrefs();

        // Remove all handlers
        if (firstHandler != null) {
            firstHandler.removeCallbacks(firstDelay);
        }

        if (secondHandler != null) {
            secondHandler.removeCallbacks(secondDelay);
        }

        clearButtonBackground();
        toggleButtonStates(true);
    }

    private void saveScoreToPrefs() {
        // Save the SCORE if it's > the current one
        Context context = getActivity();
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int highScore = prefs.getInt("highScore", 0);
        boolean showInitialDialog = prefs.getBoolean("showInitialDialog", true);

        if (highScore > SCORE) {
            editor.putInt("highScore", highScore);
        } else {
            editor.putInt("highScore", SCORE);
        }

        editor.putBoolean("shotInitialDialog", showInitialDialog);
        editor.apply();
    }

    private void loadImage(String imageFileName) {
        ImageWorkerTask task = new ImageWorkerTask(getView().getContext(), ivCountry, bitmap, this);
        task.execute(imageFileName + ".png");
    }

    private CountryCapital getRandomCountry() {
        // If we're here then we have to decrement the counter
        if (counterToPlay > 0) counterToPlay--;

        int currentCountryImageIndex = new Random().nextInt(remCountries.size());
        CountryCapital removed;

        if (remCountries.size() == 1) {
            // get all the countries again
            removed = remCountries.remove(currentCountryImageIndex);

            dbHelper.getDataAsync(new OnLoadFinished() {
                @Override
                public void finished(List<CountryCapital> countries) {
                    remCountries = countries;
                }
            });
        } else {
            removed = remCountries.remove(currentCountryImageIndex);
        }
        currentCountry = removed;

        return currentCountry;
    }

    @Override
    public void onClick(View v) {
        String text = null;

        switch (v.getId()) {
            case R.id.btn_first_choice:
                text = btnFirstChoice.getText().toString();
                btnFirstChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay_pressed));
                btnClicked = btnFirstChoice;
                break;

            case R.id.btn_second_choice:
                text = btnSecondChoice.getText().toString();
                btnSecondChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay_pressed));
                btnClicked = btnSecondChoice;
                break;

            case R.id.btn_third_choice:
                text = btnThirdChoice.getText().toString();
                btnThirdChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay_pressed));
                btnClicked = btnThirdChoice;
                break;

        }

        toggleButtonStates(false);

//        firstHandler = new Handler();
//        secondHandler = new Handler();

        firstHandler.postDelayed(firstDelay, 500);
        secondHandler.postDelayed(secondDelay, 1500);
    }

    private void updateScore() {
        tvScore.setText("Score: " + SCORE);
    }

    private void setCorrectButtonText() {
        Random rand = new Random();
        int correctAnswerPosition = rand.nextInt(3) + 1;

        String correctAnswer = currentCountry.getCapital();

        switch (correctAnswerPosition) {
            case 1:
                btnFirstChoice.setText(correctAnswer);
                btnCorrectChoice = btnFirstChoice;
                setOtherButtonTexts(btnSecondChoice, btnThirdChoice);
                break;

            case 2:
                btnSecondChoice.setText(correctAnswer);
                btnCorrectChoice = btnSecondChoice;
                setOtherButtonTexts(btnFirstChoice, btnThirdChoice);
                break;

            case 3:
                btnThirdChoice.setText(correctAnswer);
                btnCorrectChoice = btnThirdChoice;
                setOtherButtonTexts(btnFirstChoice, btnSecondChoice);
                break;
        }
    }

    private void setOtherButtonTexts(Button first, Button second) {
        Random rand = new Random();

        String[] choices = currentCountry.getChoices();

        // Something happened and we can't find the country
        // Log it for debug
        if (choices == null) {
            Log.d("dansul", "Can't find country " + currentCountry + " in incorrectCountryCapitals.");

        } else {
            // Extract two cities at random
            int randOne = rand.nextInt(choices.length);
            int randTwo = rand.nextInt(choices.length);

            // Make sure they're unique
            while (randOne == randTwo) randOne = rand.nextInt(choices.length);

            first.setText(choices[randOne]);
            second.setText(choices[randTwo]);
        }
    }

    private int getRandomScore() {
        return new Random().nextInt(9) + 1;
    }

    private void checkGameOver() {
        if (incorrectAnswersFlag < 3 && counterToPlay != 0) return;

        isGameOver = true;
        gameOverListener.onGameOver(SCORE);

        GameOverDialogFragment dialog = new GameOverDialogFragment();
        dialog.setCancelable(false);

        Bundle b = new Bundle();
        b.putInt(MainActivity.SCORE, SCORE);

        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "dansul.namethiscountrycapital.gameoverdialog");
    }

    private void toggleButtonStates(boolean state) {
        btnFirstChoice.setEnabled(state);
        btnSecondChoice.setEnabled(state);
        btnThirdChoice.setEnabled(state);
    }

    private void clearButtonBackground() {
        btnFirstChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay));
        btnSecondChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay));
        btnThirdChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay));
    }

    private Runnable firstDelay = new Runnable() {

        @Override
        public void run() {
            // Check if selection was correct and highlight it
            // red means it wasn't correct, green means it was correct
            // If it was incorrect, mark the correct one in green
            if (btnClicked == btnCorrectChoice) {
                btnClicked.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay_correct));

                // Update the score
                SCORE += getRandomScore();
                updateScore();

            } else {
                btnClicked.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay_incorrect));
                btnCorrectChoice.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_gameplay_correct));

                // Only increment the incorrect answer flag if we're in unlimited mode
                if (counterToPlay == -1) incorrectAnswersFlag++;
            }
        }
    };

    private Runnable secondDelay = new Runnable() {

        @Override
        public void run() {
            // First check if we need to quit
            checkGameOver();
            currentCountry = getRandomCountry();
            loadImage(currentCountry.getCountry());
        }
    };

    public void updateUI() {
        // Update buttons and name of country here
        clearButtonBackground();
        toggleButtonStates(true);
        setCorrectButtonText();

        tvCountry.setText(currentCountry.getCountry());
    }
}


