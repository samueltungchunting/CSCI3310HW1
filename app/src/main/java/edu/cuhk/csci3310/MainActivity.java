package edu.cuhk.csci3310;

//Name: Tung Chun Ting
//SID: 1155160200

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mShowPasscode;
    private Button mUnlockBtn;
    private Button[] mButtons;
    private ImageView mHiddenBird;
    private int validLastDigit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mShowPasscode = findViewById(R.id.passcodeView);
        mHiddenBird = findViewById(R.id.hidden_bird);
        mUnlockBtn = findViewById(R.id.unlockBtn);

        mButtons = new Button[]{
                findViewById(R.id.button0),
                findViewById(R.id.button1),
                findViewById(R.id.button2),
                findViewById(R.id.button3),
                findViewById(R.id.button4),
                findViewById(R.id.button5),
                findViewById(R.id.button6),
                findViewById(R.id.button7),
                findViewById(R.id.button8),
                findViewById(R.id.button9)
        };
    }

    public void updatePasscode(View view) {
        Button btn = (Button) view;
        String passcodeText = mShowPasscode.getText().toString();
        // append the passcode to the original passcode
        String btnText = passcodeText + btn.getText().toString();

        if(btnText.length() == 4) {
            int i = 0;
            // kind of bru force to try the last dit from 0 to 9 and see which one pass the checking
            for (i = 0; i < 10; i++){
                String tempText = btnText;
                tempText = tempText + String.valueOf(i);
                if(isCorrectPasscode(tempText)) {
                    validLastDigit = i;
                    mButtons[i].setBackgroundColor(Color.GREEN);
                }
            }
        }

        mShowPasscode.setText(btnText);
    }

    public void unlock(View view) {
        String enteredPasscode = mShowPasscode.getText().toString();
        if (isCorrectPasscode(enteredPasscode)) {
            // Correct passcode
            Toast.makeText(this, "Passcode Accepted", Toast.LENGTH_SHORT).show();
            mHiddenBird.setVisibility(View.VISIBLE);
            playDingSound();
            disableButtons();
            mUnlockBtn.setEnabled(false);
        } else {
            // Incorrect passcode
            showAlertDialog(this, "Incorrect", "Passcode Incorrect, try again");
            mShowPasscode.setText("");  // Clear the entered passcode
        }
    }

    private boolean isCorrectPasscode(String enteredPasscode) {
        // Check if the entered passcode is a five-digit code
        if (enteredPasscode.length() != 5) {
            return false;
        }

        String passcodeDigits = enteredPasscode.substring(0, 4);
        // Using the regex to check if all 4 char are digit
        if (!passcodeDigits.matches("\\d{4}")) {
            return false;
        }

        // Calculate the Verhoeffs Dihedral Check Digit
        int vdcDigit = calculateVDCD(passcodeDigits);

        // Check if the calculated digit matches the last digit of the entered passcode
        return vdcDigit == Character.getNumericValue(enteredPasscode.charAt(4));
    }

    private int calculateVDCD(String digits) {
        int[][] dihedralTable = {
                {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
                {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
                {3, 4, 0, 1, 2, 8, 9, 5, 6, 7},
                {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
                {5, 9, 8, 7, 6, 0, 4, 3, 2, 1},
                {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
                {7, 6, 5, 9, 8, 2, 1, 0, 4, 3},
                {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
                {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}
        };

        int perm = 0;
        int[] reversedDigits = new int[digits.length()];

        // Reverse the digits
        for (int i = 0; i < digits.length(); i++) {
            reversedDigits[i] = Character.getNumericValue(digits.charAt(digits.length() - i - 1));
        }

        // Calculate the Verhoeffs Dihedral Check Digit
        for (int i = 0; i < reversedDigits.length; i++) {
            perm = dihedralTable[perm][reversedDigits[i]];
        }

        return perm;
    }

    private void disableButtons() {
        // get the default background from other button, as the validLastDigit turned to green

        // disable all buttons from 0 to 9
        for (Button button : mButtons) {
            button.setEnabled(false);
        }
    }

    private void playDingSound() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.ding);
//        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(final MediaPlayer mp_temp) {
//                mp_temp.start();
//            }
//        });
//
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp_temp) {
//                mp_temp.stop();
//            }
//        });
        mp.start();
    }

    private void showAlertDialog(Context context, String title, String message) {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog incorrectDialog = builder.create();
        incorrectDialog.show();
    }
}