package com.example.mynoteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynoteapp.data.NoteDetail;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.Date;

import jp.wasabeef.richeditor.RichEditor;

public class NoteDetailActivity extends AppCompatActivity implements ColorPickerDialogListener, View.OnClickListener {
    private static final int DIALOG_BACK_COLOR_ID = 0;
    private static final int DIALOG_TEXT_BACK_COLOR_ID = 1;
    ChipGroup chipGroup;

    EditText titleEditText;
    RichEditor notesEditText;
    TextView modifiedDateTextView;
    NoteDetail noteDetail;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);


        this.chipGroup = findViewById(R.id.tags_chipgroup);
        this.titleEditText = findViewById(R.id.title_edit);

        this.notesEditText = findViewById(R.id.content_edit);

        this.modifiedDateTextView = findViewById((R.id.modifiedDateDetail_view));


        // Attaching the layout to the toolbar object
        MaterialToolbar toolbar = findViewById(R.id.topAppBarDetail);


        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        getNoteDataForSharePreference();

        setUpNoteEditor();

        // initials content

        this.titleEditText.setText(noteDetail.getTitle());

        this.modifiedDateTextView.setText(noteDetail.getEditDate().toString());


        displayAllTag();
    }

    private void getNoteDataForSharePreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.editor = sharedPreferences.edit();

        Intent gettedIntent = this.getIntent();
        long ID = gettedIntent.getExtras().getLong("NoteID");

        String noteData = sharedPreferences.getString(String.valueOf(ID), "");
        if (noteData.length() != 0) {
            noteDetail = NoteDetail.parseString(noteData);
        } else {
            noteDetail = new NoteDetail();
        }
    }

    private void setUpNoteEditor() {

        //Buttons handling
        ViewGroup toolBar = findViewById(R.id.editor_toolbar);
        for (int i = 0; i < toolBar.getChildCount(); i++) {
            toolBar.getChildAt(i).setOnClickListener(this);
        }

        this.notesEditText.setHtml(this.noteDetail.getContent());

        this.notesEditText.getSettings().setMinimumFontSize(20);

        this.notesEditText.setBackgroundColor(this.getWindow().getDecorView().getSolidColor());

        this.notesEditText.setPlaceholder("Notes");

        TypedValue typedValue = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.colorOnSecondary,typedValue,true);

        this.notesEditText.setEditorFontColor(typedValue.data);

    }

    @Override
    public void onBackPressed() {
        NoteDetail tmp = this.getNewNoteDetail();
        tmp.setID(this.noteDetail.getID());
        if (this.noteDetail.compare(tmp)) {
            super.onBackPressed();
            return;
        }

        AlertDialog.Builder notSavedAlertBuilder = new AlertDialog.Builder(this);
        notSavedAlertBuilder.setMessage("You have not saved your note. Do you want to save changes?")
                .setPositiveButton("Save", (dialog, which) -> {
                    this.noteDetail = tmp;
                    saveNote();
                    NoteDetailActivity.super.onBackPressed();
                })
                .setNegativeButton("Discard", (dialog, which) -> NoteDetailActivity.super.onBackPressed());
        AlertDialog notesaveDialog = notSavedAlertBuilder.create();
        notesaveDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // save button
        if (item.getItemId() == R.id.save_item) {
            NoteDetail tmp = this.getNewNoteDetail();
            tmp.setID(this.noteDetail.getID());
            this.noteDetail = tmp;
            this.saveNote();
        }
        //delete item
        else if (item.getItemId() == R.id.delete_item) {
            this.deleteNote();
        }

        // add tag item
        else if (item.getItemId() == R.id.addtag_item) {
            this.addTag();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.undo_item) {
            this.notesEditText.undo();
        } else if (item.getItemId() == R.id.redo_item) {
            this.notesEditText.redo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editnote_menu, menu);
        getSupportActionBar().setTitle("");
        return true;
    }

    public void addChip(String tag) {
        Chip newChip = new Chip(this);

        newChip.setText(tag);
        newChip.setCloseIconVisible(true);
        newChip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(newChip);
            }
        });
        this.chipGroup.addView(newChip);
    }


    public void displayAllTag() {
        // Display all tag into ChipGroup
        for (String tag : this.noteDetail.getAllTags()) {
            this.addChip(tag);
        }
    }


    @Override
    public void onColorSelected(int dialogId, int color) {
        if (dialogId == DIALOG_TEXT_BACK_COLOR_ID) {
            this.notesEditText.setTextBackgroundColor(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public NoteDetail getNewNoteDetail(){
        NoteDetail newNoteDetail = new NoteDetail();
        newNoteDetail.setTitle(titleEditText.getText().toString());
        newNoteDetail.setContent(this.notesEditText.getHtml());
        newNoteDetail.setEditDate(new Date(System.currentTimeMillis()));
        newNoteDetail.saveRawContent();
        // save all tag
        newNoteDetail.resetTag();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            int id = this.chipGroup.getChildAt(i).getId();
            Chip curChip = findViewById(id);
            String tag = curChip.getText().toString();
            newNoteDetail.addTag(tag);
        }
        return newNoteDetail;
    }

    public void saveNote() {
        String noteData = noteDetail.toString();
        editor.putString(String.valueOf(noteDetail.getID()), noteData);
        editor.apply();


        // if note has not saved into database yet
        if (!noteDetail.isSaved()) {
            NoteDetail.updateID();
            noteDetail.setSaved();
        }
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
    }

    public void deleteNote() {
        editor.remove(String.valueOf(noteDetail.getID()));
        editor.apply();
        super.onBackPressed();
    }

    public void addTag() {
        AlertDialog.Builder addtagBuilder = new AlertDialog.Builder(this);
        addtagBuilder.setTitle("Add a new tag");
        EditText tagNameEditText = new EditText(this);
        tagNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        addtagBuilder.setView(tagNameEditText);

        // Set up the buttons
        addtagBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addChip(tagNameEditText.getText().toString());
            }
        });
        addtagBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }

        });
        addtagBuilder.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bold_button) {
            notesEditText.setBold();
        } else if (v.getId() == R.id.italic_button) {
            notesEditText.setItalic();
        } else if (v.getId() == R.id.underline_button) {
            notesEditText.setUnderline();
        } else if (v.getId() == R.id.checkbox_button) {
            notesEditText.insertTodo();
        }else if (v.getId() == R.id.alignCenter_button) {
            notesEditText.setAlignCenter();
        } else if (v.getId() == R.id.alignLeft_button) {
            notesEditText.setAlignLeft();
        } else if (v.getId() == R.id.alignRight_button) {
            notesEditText.setAlignRight();
        } else if (v.getId() == R.id.list_numbered_button) {
            notesEditText.setNumbers();
        } else if (v.getId() == R.id.list_bulleted_button) {
            notesEditText.setBullets();
        } else if (v.getId() == R.id.textBackColor_button) {
            ColorPickerDialog.newBuilder()
                    .setDialogId(DIALOG_TEXT_BACK_COLOR_ID)
                    .setDialogTitle(R.string.dialog_title_text_color)
                    .setShowAlphaSlider(false)
                    .setAllowCustom(true)
                    .show(NoteDetailActivity.this);
        } else if (v.getId() == R.id.strikethrough_button) {
            notesEditText.setStrikeThrough();
        } else if (v.getId() == R.id.removeFormat_button) {
            notesEditText.removeFormat();
        }
    }
}