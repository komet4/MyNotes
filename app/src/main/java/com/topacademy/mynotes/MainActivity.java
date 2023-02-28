package com.topacademy.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addNoteButton;
    RecyclerView recyclerView;
    ImageButton menuButton;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNoteButton = findViewById(R.id.add_note_button);
        recyclerView = findViewById(R.id.recycler_view);
        menuButton = findViewById(R.id.menu_button);

        addNoteButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,
                NoteDetailsActivity.class)));
        menuButton.setOnClickListener(v -> showMenu());
        setupRecyclerView();
    }

    void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuButton);
        popupMenu.getMenu().add("Sign out");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getTitle() == "Sign out") {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.startListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}