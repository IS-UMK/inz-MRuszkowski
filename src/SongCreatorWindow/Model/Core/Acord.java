package SongCreatorWindow.Model.Core;

import java.util.LinkedList;

/**
 *
 * Public class that is part of model. It represents a Acord (more than one note) for JFugue library
 */
public class Acord implements IPlayable{
    /**
     * In which moment the note occurs
     */
    int TimeX;
    int NoteHeight;

    @Override
    public int getTimeX() {
        return TimeX;
    }

    @Override
    public void setTimeX(int x) {
        TimeX = x;
    }

    @Override
    public int getSoundHeight() { return NoteHeight; }

    @Override
    public void setSoundHeight(int y) {
        NoteHeight = y;
    }

    /**
     * Collection of Notes
     */
    LinkedList<Note> notes;
    
    public Acord(Note[] notes)
    {
        this.notes = new LinkedList<Note>();
        
        for(Note n : notes)
            this.notes.add(n);
    }
    
    public void AddNote(Note note)
    {
        notes.add(note);
    }
    
    public void RemoveNoteAtIndex(int index)
    {
        notes.remove(index);
    }

    @Override
    public String ExtractJFugueSoundString() {
        
        var musicString = new StringBuilder();
        
        for(Note n : notes)
            musicString.append(String.format("%s+", n.ExtractJFugueSoundString()));

        musicString.deleteCharAt(musicString.length() - 1); //Remove the last '+' symbol
        return musicString.toString();
    }
    
}
