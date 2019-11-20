package com.example.notetaker2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = "NoteActivity";

    private int id;
    Button button;
    String noteTitle;
    String noteType;
    String noteContent;
    final int PERSONAL = 0;
    final int SCHOOL = 1;
    final int WORK = 2;
    final int OTHER = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Setting my activity layout to the GridLayout I made in a separate class(MyNoteGridLayout).
        MyNoteGridLayout myGridLayout = new MyNoteGridLayout(this);
        setContentView(myGridLayout);

        Log.d(TAG, "WE are here");
        Intent intent = getIntent();
        final EditText title = findViewById(R.id.editTitle);
        final Spinner spinner = findViewById(R.id.typeSpinner);
        final EditText content = findViewById(R.id.editContent);
        id = -1;

        //Getting my values from Extra, which are coming from MainActivity.
        if (intent != null) {
            noteTitle = intent.getStringExtra("title");
            noteType = intent.getStringExtra("type");
            noteContent = intent.getStringExtra("content");
            id = intent.getIntExtra("index", -1);
        }
        //Setting the text and position of my EditText and Spinner equal to the content the program grabbed from extra.
        title.setText(noteTitle);
        spinner.setSelection(findType(noteType));
        content.setText(noteContent);


        /**
         This Override function is used to send the contents of "title", "type", and "content" back to the mainActivity when the "DONE" button is pressed.
         It also checks to see that the user entered a title for their note and wont allow them to go back unless they enter a title.
         */
        button = findViewById(R.id.doneButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                EditText title = findViewById(R.id.editTitle);
                Spinner spinner = findViewById(R.id.typeSpinner);
                EditText content = findViewById(R.id.editContent);

                String noteTitle = title.getText().toString();
                String type = spinner.getSelectedItem().toString();
                String note = content.getText().toString();


                if (noteTitle.isEmpty()) {
                    Toast.makeText(NoteActivity.this, "Please enter a title for your note!", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("title", noteTitle);
                    intent.putExtra("type", type);
                    intent.putExtra("content", note);
                    intent.putExtra("index", id);
                    setResult(Activity.RESULT_OK, intent);
                    NoteActivity.this.finish();
                }
            }
        });
    }

    /**
     This method is used to tell what "type" the note is and sets the spinner selection to that "type".
     * @param type This is the type passed from the mainActivity, which is used to tell what type the note is
     *             when either editing or creating a new note.
     * @return The index the "type" is at in the Spinner.
     */
    public int findType(String type){
        if(type.equals("PERSONAL")){
            return PERSONAL;
        }else if(type.equals("SCHOOL")) {
            return SCHOOL;
        }else if(type.equals("WORK")){
            return WORK;
        }else if(type.equals("OTHER")){
            return OTHER;
        }else{
            return 0;
        }
    }
}
