package com.example.mobiletrainer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExercisesActivity extends AppCompatActivity {
    private ListView lstExercises;
    private ArrayList<Exercise> exercises;

    //Remove
    private ArrayList<String> testList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        // Initialize listview and list of exercises
        lstExercises = findViewById(R.id.lstExercises);
        exercises = new ArrayList<Exercise>(10);
        //Remove
        testList = new ArrayList<String>(10);

        // Set okhttp handler
        String url = "https://wger.de/api/v2/exercise.json?page=" + "2";

        OkHttpHandler okHttpHandler = new OkHttpHandler();

        okHttpHandler.execute(url);

    }

    // TODO: Make another async task

    public class OkHttpHandler extends AsyncTask {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(Object[] params) {

            Request.Builder builder = new Request.Builder();
            builder.url(params[0].toString());
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();

                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            parseResponse(o.toString());
            Log.d("curtis", Integer.toString(exercises.size()));

            //Change
            ArrayAdapter adapter = new ArrayAdapter(ExercisesActivity.this, android.R.layout.simple_expandable_list_item_1, testList);
            lstExercises.setAdapter(adapter);
        }
    }

    private void parseResponse(String response) {
        try{
            JSONObject json = new JSONObject(response);
            JSONArray results = (JSONArray) json.get("results");

            for(int i = 0; i < results.length(); i++) {
                JSONObject exercise = results.getJSONObject(i);
                Exercise exerciseToAdd = new Exercise(exercise.getString("name"), exercise.getString("description"), mapIdToCategory(exercise.getInt("category")));

                // If name and description fields exist in api
                if (exercise.getString("name").trim() != "" && exercise.getString("description").trim() != "") {
                    //remove
                    String test = exercise.getString("name");
                    testList.add(test);
                    exercises.add(exerciseToAdd);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("parseresponseerror", e.getMessage());
        }
    }

    // The exercise is linked to it's category by ID so this method is used to get the name of the category for the exercise
    private String mapIdToCategory(int categoryId) {
        String categoryName = "";

        switch (categoryId) {
            case 10:
                categoryName = "Abs";
                break;
            case 8:
                categoryName = "Arms";
                break;
            case 12:
                categoryName = "Back";
                break;
            case 14:
                categoryName = "calves";
                break;
            case 11:
                categoryName = "Chest";
                break;
            case 9:
                categoryName = "Legs";
                break;
            case 13:
                categoryName = "Shoulders";
                break;
        }

        return categoryName;
    }
}