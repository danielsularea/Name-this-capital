package dansul.namethiscountrycapital;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class PlayDialogFragment extends DialogFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_play_chooser, container, false);

        Button tenPlays = (Button) v.findViewById(R.id.ten_plays);
        Button twentyPlays = (Button) v.findViewById(R.id.twenty_plays);
        Button threeErrors = (Button) v.findViewById(R.id.three_errors);

        final Intent intent = new Intent(getActivity().getApplicationContext(), GamePlayActivity.class);

        tenPlays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(GamePlayActivity.NUMBER_TO_PLAY, GamePlayActivity.TEN_PLAYS);

                getActivity().startActivityForResult(intent, MainActivity.START_GAME_REQUEST);
                dismiss();
            }
        });

        twentyPlays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(GamePlayActivity.NUMBER_TO_PLAY, GamePlayActivity.TWENTY_PLAYS);

                getActivity().startActivityForResult(intent, MainActivity.START_GAME_REQUEST);
                dismiss();
            }
        });

        threeErrors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(GamePlayActivity.NUMBER_TO_PLAY, GamePlayActivity.UNLIMITED_PLAYS);

                getActivity().startActivityForResult(intent, MainActivity.START_GAME_REQUEST);
                dismiss();
            }
        });

        return v;
    }
}
