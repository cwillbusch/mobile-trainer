package com.example.mobiletrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewWorkoutActivity extends AppCompatActivity {
    DatabaseHelper db;
    private EditText txtWorkoutName;
    private Button btnAddWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);

        View screen = this.getWindow().getDecorView();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean darkMode = getPrefs.getBoolean("colour", false);

        if(darkMode) {
            screen.setBackgroundResource(getPrefs.getInt("background", R.color.differentBackground));
        }
        else {
            screen.setBackgroundResource(getPrefs.getInt("background", R.color.whiteBackground));
        }

        db = new DatabaseHelper(this);

        txtWorkoutName = findViewById(R.id.txtWorkoutName);
        btnAddWorkout = findViewById(R.id.btnAddWorkout);

        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtWorkoutName.getText().toString().matches("")) {
                    Toast.makeText(NewWorkoutActivity.this, "No workout name set!", Toast.LENGTH_SHORT).show();
                }
                else{
                    boolean isInserted = db.insertWorkout(txtWorkoutName.getText().toString(), 0);

                    if(isInserted) {
                        Intent intent = new Intent(NewWorkoutActivity.this, WorkoutActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(NewWorkoutActivity.this, "Workout could not be added!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        View screen = this.getWindow().getDecorView();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean darkMode = getPrefs.getBoolean("colour", false);

        if(darkMode) {
            screen.setBackgroundResource(getPrefs.getInt("background", R.color.differentBackground));
        }
        else {
            screen.setBackgroundResource(getPrefs.getInt("background", R.color.whiteBackground));
        }
    }
}
