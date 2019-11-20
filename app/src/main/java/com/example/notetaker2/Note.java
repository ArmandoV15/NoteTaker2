package com.example.notetaker2;

public class Note {
    private int id;
    private String title;
    private String content;
    private String type;


    /**
     This is the EVC of class Note
     * @param title Users desired title for their note
     * @param type Users desired type of their note
     * @param content Users content/body of the note.
     */
    public Note(String title, String type, String content){
        this.title = title;
        this.type = type;
        this.content = content;
    }

    /**
     This is the DVC of class Note
     */
    public Note(){
        id = -1;
        title = "BLANK TITLE";
        content = "NO CONTENT";
        type = "NO TYPE";
    }

    /**
     This toString is used to display the title of a new note in the ListView
     * @return title of the note created
     */
    @Override
    public String toString(){
        return title;
    }

    //Getter used to get the title of a note
    public String getTitle() {
        return title;
    }
    //Setter used to set the title of a note
    public void setTitle(String title) {
        this.title = title;
    }

    //Getter used to get the content of a note
    public String getContent() {
        return content;
    }
    //Setter used to set the content of a note
    public void setContent(String content) {
        this.content = content;
    }

    //Getter used to get the type of a note
    public String getType() {
        return type;
    }
    //Setter used to set the type of a note
    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

