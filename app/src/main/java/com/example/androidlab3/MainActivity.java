package com.example.androidlab3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ListView listViewNotes;
    private ArrayAdapter<String> adapter;
    private Set<String> notesSet;

    private static final String PREF_NAME = "NotesPref";
    private static final int ADD_NOTE_REQUEST = 1;

    public class MenuLabels {
        public static final String CREATE_NOTE = "Create Note";
        public static final String DELETE_NOTE = "Delete Note";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listViewNotes = findViewById(R.id.listViewNotes);

        notesSet = new HashSet<>();
        notesSet.add("Sample Note");

        Set<String> savedNotes = getNotesFromPreferences();

        if (savedNotes != null) {
            notesSet = savedNotes;
        } else {
            notesSet = new HashSet<>();
            notesSet.add("Sample Note");
        }

        if (notesSet.isEmpty()) {
            notesSet.add("Sample Note");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(notesSet));
        listViewNotes.setAdapter(adapter);

        // Handle incoming data from AddNoteActivity
        handleIncomingData();
    }

    private void handleIncomingData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("noteName")) {
            String noteName = intent.getStringExtra("noteName");
            addNoteToSet(noteName);
            intent.removeExtra("noteName");
            setIntent(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            Drawable icon = item.getIcon();
            if (icon != null) {
                icon.setTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pink_menu_background)));
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {
            case MenuLabels.CREATE_NOTE:
                startActivityForResult(new Intent(MainActivity.this, AddNoteActivity.class), ADD_NOTE_REQUEST);
                return true;
            case MenuLabels.DELETE_NOTE:
                if (!notesSet.isEmpty()) {
                    Intent deleteNoteIntent = new Intent(MainActivity.this, DeleteNoteActivity.class);
                    deleteNoteIntent.putExtra("MainActivity", MainActivity.class);
                    startActivity(deleteNoteIntent);
                } else {
                    showToast(getString(R.string.no_notes_to_delete));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK && data != null) {
            String noteName = data.getStringExtra("noteName");
            if (noteName != null) {
                addNoteToSet(noteName);
            }
        }
    }

    public static Set<String> getNotesFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getStringSet("notes", new HashSet<>());
    }

    public static void deleteNoteFromSet(String note, Context context) {
        MainActivity mainActivity = new MainActivity();
        Set<String> notesSet = mainActivity.getNotesFromPreferences(context);
        notesSet.remove(note);
        mainActivity.saveNotesToPreferences(notesSet, context);
    }

    public Set<String> getNotesFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getStringSet("notes", new HashSet<>());
    }

    public void addNoteToSet(String note) {
        notesSet.add(note);
        updateListView();
        saveNotesToPreferences(notesSet, this);
    }

    public void deleteNoteFromSet(String note) {
        notesSet.remove(note);
        updateListView();
        saveNotesToPreferences(notesSet, this); // Pass the required arguments
    }

    public Set<String> getNotesSet() {
        return notesSet;
    }

    private void updateListView() {
        adapter.clear();
        adapter.addAll(new ArrayList<>(notesSet));
        adapter.notifyDataSetChanged();
    }

    public void saveNotesToPreferences(Set<String> notesSet, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("notes", notesSet);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}