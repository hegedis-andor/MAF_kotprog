package com.example.shakeit;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int counter;
    private ConstraintLayout constraintLayout;
    private ImageView emojiImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.constraintLayout = findViewById(R.id.container);
        this.counter = 0;
        this.emojiImageView = findViewById(R.id.emojiImageView);
    }

    public void changeBackground(View view) {
        switch (this.counter%4) {
            case 1:
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.START_GREEN));
                emojiImageView.setBackgroundResource(R.drawable.happy);
                this.counter++;
                break;
            case 2:
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.MID_YELLOW));
                emojiImageView.setBackgroundResource(R.drawable.surprised);
                this.counter++;
                break;
            case 3:
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.MID_ORANGE));
                emojiImageView.setBackgroundResource(R.drawable.confused);
                this.counter++;
                break;
            case 0:
                constraintLayout.setBackgroundColor(getResources().getColor(R.color.HIGH_RED));
                emojiImageView.setBackgroundResource(R.drawable.sick);
                this.counter++;
                break;
        }
    }

}
