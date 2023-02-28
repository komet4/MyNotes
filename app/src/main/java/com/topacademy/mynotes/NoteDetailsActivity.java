package com.topacademy.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.Bundle;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {
    EditText titleEditText, contentEditText;
    ImageButton saveNoteButton, deleteNoteButton;
    TextView pageTitleTextView;
    String title, content, docId;
    //TextView deleteNoteTextViewButton;
    boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_title_edit_text);
        contentEditText = findViewById(R.id.notes_content_edit_text);
        saveNoteButton = findViewById(R.id.save_note_button);
        pageTitleTextView = findViewById(R.id.page_title_text_view);
        //deleteNoteTextViewButton = findViewById(R.id.delete_note_text_view_button);
        deleteNoteButton = findViewById(R.id.delete_note_button);

        //receive data
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode) {
            pageTitleTextView.setText("Edit your note");
            //deleteNoteTextViewButton.setVisibility(View.VISIBLE);
            deleteNoteButton.setVisibility(View.VISIBLE);
        }

        saveNoteButton.setOnClickListener(v -> saveNote());
        //deleteNoteTextViewButton.setOnClickListener(v -> deleteNoteFromFirebase());
        deleteNoteButton.setOnClickListener(v -> deleteNoteFromFirebase());
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
        if (!isEditMode) {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        } else {
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }

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

    void deleteNoteFromFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //note is deleted
                Utility.showToast(NoteDetailsActivity.this, "Note deleted successfully");
                finish();
            } else {
                Utility.showToast(NoteDetailsActivity.this, "Failed while deleting note");
            }
        });
    }
}