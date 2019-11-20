
/**
 * This program expands on PA6 adding in new functionality which include persistent data
 * storage using a SQLite database, new action menus which allow the user to add notes, delete all notes, and
 * delete specific notes. We are also not displaying directly to a listView, we are storing all notes in a database
 * then using an adapter to display them to a listView.
 *
 * CPSC 312-01, Fall 2019
 * Programming Assignment #7
 <div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
 <div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
 <div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
 <div>Icons made by <a href="https://www.flaticon.com/authors/smashicons" title="Smashicons">Smashicons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
 *
 * @author Armando Valdez
 * @version v1.0 11/19/19
 */



package com.example.notetaker2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "mainActivity";
    static final int LOGIN_REQUEST_CODE = 1;

    private SimpleCursorAdapter cursorAdapter;

    /**
     This onActivityResult allows the program to get the results from my second activity when the user presses the "Done" button.
     It also differentiates between a new note and an edited note which prevents a new note showing up in the programs ListView if it was edited.
     Otherwise if a new note was created it will create a new note and add it to the ListView
     * @param requestCode Used to hold and check the requestCode and check it is equals LOGIN_REQUEST_CODE
     * @param resultCode code returned by the second activity to validate the results before sending results back
     * @param data our call to Intent when the program wants to get anything the second activity put into putExtra
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            String title = data.getStringExtra("title");
            String type = data.getStringExtra("type");
            String content = data.getStringExtra("content");
            int edited = data.getIntExtra("index", -1);
            NoteOpenHelper openHelper = new NoteOpenHelper(this);

            if (edited > -1) {
               openHelper.updateNoteById(edited, new Note(title, type, content));
               Cursor cursor = openHelper.getSelectAllNotesCursor();
               cursorAdapter.changeCursor(cursor);
            } else {
                Note note = new Note(title, type, content);
                openHelper.insertNote(note);
                Cursor cursor = openHelper.getSelectAllNotesCursor();
                cursorAdapter.changeCursor(cursor);
            }
        }
    }

    /**
     This method allows for the add and delete button to show up in the menu bar of our app.
     * @param menu Reference to our option menu we created in our XML files
     * @return Displays the option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     This method allows us to assign functionality to our add new note and delete buttons in our
     option menu. When the plus sign is pressed it passes and empty title, type, and content to the second activity which represents the new note.
     There is also an index being passed which is used to distinguish this as a new note.
     If the trashcan is pressed then then an alert dialogue is show prompting the user if they are sure
     they want to delete all their notes.
     * @param item Represents the button being pressed
     * @return Action being conducted
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "HERE Before switch");
        switch(id){
            case R.id.addNote:
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("type", "");
                intent.putExtra("content", "");
                intent.putExtra("index", -1);
                Log.d(TAG, "HERE after switch");
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                return true;
            case R.id.deleteNote:
                AlertDialog.Builder alertBuilder =
                        new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("Delete All Notes")
                        .setMessage("Are you sure you want to delete all of your notes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NoteOpenHelper openHelper = new NoteOpenHelper(MainActivity.this);
                                openHelper.deleteAllNotes();
                                Cursor cursor = openHelper.getSelectAllNotesCursor();
                                cursorAdapter.changeCursor(cursor);
                            }
                        })
                        .setNegativeButton("No", null);
                alertBuilder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting my activity layout to the GridLayout I made in a separate class(MyGridLayout).
        MyGridLayout myGridLayout = new MyGridLayout(this);
        setContentView(myGridLayout);
        Log.d(TAG, "Oncreate");

        final ListView listView = findViewById(R.id.listView);
        final NoteOpenHelper openHelper = new NoteOpenHelper(this);
        Cursor cursor = openHelper.getSelectAllNotesCursor();

        /**
         Creating a new cursor adapter to be able to update our list view when we create or update a note.
         */
        cursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_activated_1,
                cursor,
                new String[] {NoteOpenHelper.TITLE},
                new int[] {android.R.id.text1},
                0);

        listView.setAdapter(cursorAdapter);

        /**
         This setChoiceMode and setMultipleChoiceListener allow the user to highlight certain notes
         and delete those highlighted notes when the user longCLicks on one. The onActionItemChecked method
         deletes all the selected notes.
         The onCreateActionMode method switches the display to the cam_menu display when a note is
         long clicked.
         */
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.cam_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()){
                    case R.id.deleteListItem:
                        SparseBooleanArray check = listView.getCheckedItemPositions();
                        NoteOpenHelper openHelper = new NoteOpenHelper(MainActivity.this);
                        for(int i = 0; i < check.size(); i++) {
                            if (check.valueAt(i)) {
                                int id = (int) cursorAdapter.getItemId(check.keyAt(i));
                                openHelper.deleteIndividualNote(id);
                            }
                        }
                        Cursor cursor = openHelper.getSelectAllNotesCursor();
                        cursorAdapter.changeCursor(cursor);
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        /**
         This setOnItemClickListener is used to tell when a note in the ListView is being clicked.
         When the note is clicked, its title, type, and contents are sent to the second activity and are able to be edited.
         The notes index is also sent over to distinguish it as a note that is being edited.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                int id = (int) cursorAdapter.getItemId(position);
                Note n1 = openHelper.getNoteById(id);
                int index = (int) cursorAdapter.getItemId(position);
                String noteTitle = n1.getTitle();
                String type = n1.getType();
                String content = n1.getContent();
                intent.putExtra("title", noteTitle);
                intent.putExtra("type", type);
                intent.putExtra("content", content);
                intent.putExtra("index", index);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });
    }
}

