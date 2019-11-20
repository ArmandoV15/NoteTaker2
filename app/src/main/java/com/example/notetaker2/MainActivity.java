
/**
 * This program expands on PA6 adding in new functionality which include persistent data
 * storage using a SQLite database, new action menus which allow the user to add delete
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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

        cursorAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_activated_1,
                cursor,
                new String[] {NoteOpenHelper.TITLE},
                new int[] {android.R.id.text1},
                0);

        listView.setAdapter(cursorAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int selected;
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                selected++;
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

