package com.example.androidlab3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DeleteNoteActivity extends AppCompatActivity {

    private Spinner spinnerNotes;
    private Button buttonDeleteNote;
    private List<String> notesList;

    MainActivity mainActivity = new MainActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_note);

        mainActivity = (MainActivity) getIntent().getSerializableExtra("MainActivity");

        spinnerNotes = findViewById(R.id.spinnerNotes);
        buttonDeleteNote = findViewById(R.id.buttonDeleteNote);

        notesList = new ArrayList<>(MainActivity.getNotesFromPreferences(this));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, notesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNotes.setAdapter(adapter);

        spinnerNotes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });

        buttonDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });
    }

    private void deleteNote() {
        String selectedNote = spinnerNotes.getSelectedItem().toString();
        MainActivity.deleteNoteFromSet(selectedNote, getApplicationContext());
        MainActivity mainActivity = new MainActivity();
        mainActivity.saveNotesToPreferences(mainActivity.getNotesSet(), getApplicationContext());
        finish();
    }
}