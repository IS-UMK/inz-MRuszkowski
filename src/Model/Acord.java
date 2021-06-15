package Model;

import java.util.LinkedList;

/**
 *
 * Public class that is part of model. It represents a Acord (more than one note) for JFugue library
 */
public class Acord implements IPlayable{

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
        
        String str = "";
        
        for(Note n : notes)
            str += n.ExtractJFugueSoundString();
        
        return str;
    }
    
}
