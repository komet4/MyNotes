package com.topacademy.mynotes;

import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText titleEditText, contentEditText;
    ImageButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveButton = findViewById(R.id.save_note_button);

        saveButton.setOnClickListener(v -> saveNote());
    }

    void saveNote() {
        String titleNote = titleEditText.getText().toString();
        String contentNote = contentEditText.getText().toString();
        if (titleNote.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(titleNote);
        note.setContent(contentNote);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionsReferenceForNotes().document();

        documentReference.set(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //note is added
                Utility.showToast(NoteDetailsActivity.this, "Note added successfully");
                finish();
            } else {
                Utility.showToast(NoteDetailsActivity.this, "Failed while adding note");
            }
        });
    }
}