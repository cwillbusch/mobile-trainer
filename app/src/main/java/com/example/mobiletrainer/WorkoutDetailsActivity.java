package com.example.mobiletrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class WorkoutDetailsActivity extends AppCompatActivity {
    private ListView lstExercises;
    private Bundle bundle;

    private ArrayList<Exercise> exercises;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        lstExercises = findViewById(R.id.lstExercises);

        exercises = new ArrayList<Exercise>(0);

        db = new DatabaseHelper(this);

        bundle = new Bundle();
        bundle = getIntent().getExtras();

        WorkoutDetailsHandler handler = new WorkoutDetailsHandler();
        handler.execute();
    }

    public class WorkoutDetailsHandler extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try{
                getAllExercises();
                return exercises;
            }
            catch (Exception e) {
                Toast.makeText(WorkoutDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            lstExercises.setAdapter(new AddedExerciseAdapter(WorkoutDetailsActivity.this, exercises));

            lstExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Exercise selectedExercise = exercises.get(position);

                    Intent intent = new Intent(WorkoutDetailsActivity.this, ExerciseDetailsActivity.class);
                    // Add name, category and descrition to intent to show on details page
                    intent.putExtra("name", selectedExercise.getName());
                    intent.putExtra("description", selectedExercise.getDescription());
                    intent.putExtra("category", selectedExercise.getCategory());
                    intent.putExtra("alreadyAdded", true);
                    // Start exercise detail activity
                    startActivity(intent);
                }
            });
        }
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.workouts_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addWorkout) {
            Intent intent = new Intent(WorkoutDetailsActivity.this, ExercisesActivity.class);
            intent.putExtra("workoutId", bundle.getInt("workoutId"));
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.deleteWorkout) {
            boolean deleted = db.deleteWorkout(bundle.getInt("workoutId"));

            if(deleted) {
                Intent main = new Intent(WorkoutDetailsActivity.this, WorkoutActivity.class);
                startActivity(main);
                Toast.makeText(WorkoutDetailsActivity.this, "Workout deleted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(WorkoutDetailsActivity.this, "Could not be deleted", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void getAllExercises(){
        Cursor cursor = db.getExercises(bundle.getInt("workoutId"));

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
           exercises.add(new Exercise(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5)));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(WorkoutDetailsActivity.this, WorkoutActivity.class);
        startActivity(intent);
    }
}
