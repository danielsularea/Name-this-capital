package dansul.namethiscountrycapital;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class GameOverDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Check for arguments
        int score = 0;
        Bundle b = getArguments();

        if (b != null) {
            score = b.getInt(MainActivity.SCORE);
        }

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialog, null);

        TextView tv = (TextView) v.findViewById(R.id.dialog_textview);
        tv.setText("Well done! Your score is " + score);

        builder.setView(v)
                .setPositiveButton(getString(R.string.end_game), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GamePlayActivity activity;

                        try {
                            activity = (GamePlayActivity) getActivity();

                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }

                        getActivity().finish();
                    }
                });

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);

        return builder.create();
    }

}
