package com.group9.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button sadButton = findViewById(R.id.button2);
        sadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResponseToFirebase("sad");
                displayPopupMessage("Response saved. Why are you feeling sad?");
            }
        });
    }

    private void saveResponseToFirebase(String response) {
        // Save response to Firebase database
        String key = mDatabase.child("responses").push().getKey();
        mDatabase.child("responses").child(key).setValue(response);
    }

    private void displayPopupMessage(String message) {
        // Display popup message using Toast
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
