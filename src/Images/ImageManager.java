package Images;

import SongCreatorWindow.Model.Core.MusicKeySelection;
import SongCreatorWindow.Model.Core.NoteSelection;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ImageManager
{
    //region Initialazing
    static ImageManager instance;
    private ImageManager() { _width = 100; _height = 100; }

    /**
     * Creates InstrumentManager Instance or returns existing one. This is Singleton
     * @return InstrumentManager Instance
     */
    public static ImageManager getInstance()
    {
        if(instance == null)
            synchronized (ImageManager.class) {
                if(instance == null)
                    instance = new ImageManager();
            }
        return instance;
    }
    //endregion

    //region ImageDimensions
    private double _width;
    private double _height;
    private boolean reloadImage = false;
    private ImageManager setReloadFlag(boolean b)
    {
        reloadImage = b;

        return this;
    }

    /**
     * Sets up dimensions of image that will be requested
     * @param width Image width
     * @param height Image Height
     * @return The same utilized ImageManager instance
     */
    public ImageManager setDimensions(double width, double height)
    {
        _width = width;
        _height = height;

        return this;
    }
    //end region

    //region Images references
    Image _altoKeyImage;
    Image _violinKeyImage;
    Image _bassKeyImage;

    Image _whole_NoteImage;
    Image _half_NoteImage;
    Image _quarter_NoteImage;
    Image _eighth_NoteImage;
    Image _sixteenth_NoteImage;
    Image _thirtySecond_NoteNoteImage;
    Image _sixtyFourth_NoteImage;
    Image _oneHundredTwentyEighth_NoteImage;

    Image _soundEffectsImage;
    Image _percussiveImage;
    Image _ethnicImage;
    Image _synthEffectsImage;
    Image _synthPadImage;
    Image _synthLeadImage;
    Image _pipeImage;
    Image _reedImage;
    Image _brassImage;
    Image _ensembleImage;
    Image _stringsImage;
    Image _bassImage;
    Image _guitarImage;
    Image _organImage;
    Image _noneImage;
    Image _pianoImage;
    Image _ChromaticPercussionImage;
    Image _speakerImage;

    /**
     * Get Image by its path
     * @param resource Path to image
     * @return Scaled Image
     */
    private Image getImageByResource(String resource)
    {
        System.out.println(String.format("Image has been loaded from location: %s", resource));
        return new Image(ImageManager.class.getResource(resource).toString(), _width, _height, false, false);
    }
    //endregion

    //region Getters - Flyweight pattern utilized

    //region Music keys
    public Image getMusicKey(MusicKeySelection musicKeySelection)
    {
        Image image = null;

        switch (musicKeySelection)
        {
            case ViolinKey:
                image = getViolinKey();
                break;
            case BassKey:
                image = getBassKey();
                break;
            case AltoKey:
                image = getAltoKey();
                break;
        }

        return image;
    }

    private Image getAltoKey()
    {
        if(reloadImage || _altoKeyImage == null)
        {
            _altoKeyImage = getImageByResource("/Images/alto_key.png");
            setReloadFlag(false);
        }

        return _altoKeyImage;
    }

    private Image getViolinKey()
    {
        if(reloadImage || _violinKeyImage == null)
        {
           // _violinKeyImage = getImageByResource("/Images/violin_clef.svg");
            _violinKeyImage = getImageByResource("/Images/violin_key.png");
            setReloadFlag(false);
        }

        return _violinKeyImage;
    }

    private Image getBassKey()
    {
        if(reloadImage || _bassKeyImage == null)
        {
            _bassKeyImage = getImageByResource("/Images/bass_key.png");
            setReloadFlag(false);
        }

        return _bassKeyImage;
    }
    //endregion

    //region Notes
    public HashMap<NoteSelection, Image> getAllNotesWithNames()
    {
        var map = new LinkedHashMap<NoteSelection, Image>();

        map.put(NoteSelection.WholeNote, getWholeNote());
        map.put(NoteSelection.HalfNote, getHalfNote());
        map.put(NoteSelection.QuarterNote, getQuarterNote());
        map.put(NoteSelection.EighthNote, getEighthNote());
        map.put(NoteSelection.SixteenthNote, getSixteenthNote());
        map.put(NoteSelection.ThirtySecondNote, getThirtySecondNote());
        map.put(NoteSelection.SixtyFourthNote, getSixtyFourthNote());
        map.put(NoteSelection.OneHundredTwentyEighthNote, getOneHundredTwentyEighthNote());

        return map;
    }

    public Image[] getAllNotes()
    {
        return new Image[]{
                getWholeNote(),
                getHalfNote(),
                getQuarterNote(),
                getEighthNote(),
                getSixteenthNote(),
                getThirtySecondNote(),
                getSixtyFourthNote(),
                getOneHundredTwentyEighthNote()
        };
    }

    public Image getNote(NoteSelection noteSelection)
    {
        Image note = null;

        switch (noteSelection) {
            case WholeNote -> {
                note = getWholeNote();
            }
            case HalfNote -> {
                note = getHalfNote();
            }
            case QuarterNote -> {
                note = getQuarterNote();
            }
            case EighthNote -> {
                note = getEighthNote();
            }
            case SixteenthNote -> {
                note = getSixteenthNote();
            }
            case ThirtySecondNote -> {
                note = getThirtySecondNote();
            }
            case SixtyFourthNote -> {
                note = getSixtyFourthNote();
            }
            case OneHundredTwentyEighthNote -> {
                note = getOneHundredTwentyEighthNote();
            }
        }

        return note;
    }

    public Image getWholeNote()
    {
        if(reloadImage || _whole_NoteImage == null)
        {
            _whole_NoteImage = getImageByResource("/Images/whole_note.png");
            setReloadFlag(false);
        }

        return _whole_NoteImage;
    }

    public Image getHalfNote()
    {
        if(reloadImage || _half_NoteImage == null)
        {
            _half_NoteImage = getImageByResource("/Images/half_note.png");
            setReloadFlag(false);
        }

        return _half_NoteImage;
    }

    public Image getQuarterNote()
    {
        if(reloadImage || _quarter_NoteImage == null)
        {
            _quarter_NoteImage = getImageByResource("/Images/quarter_note.png");
            setReloadFlag(false);
        }

        return _quarter_NoteImage;
    }

    public Image getEighthNote()
    {
        if(reloadImage || _eighth_NoteImage == null)
        {
            _eighth_NoteImage = getImageByResource("/Images/eighth_note.png");
            setReloadFlag(false);
        }

        return _eighth_NoteImage;
    }

    public Image getSixteenthNote()
    {
        if(reloadImage || _sixteenth_NoteImage == null)
        {
            _sixteenth_NoteImage = getImageByResource("/Images/sixteenth_note.png");
            setReloadFlag(false);
        }

        return _sixteenth_NoteImage;
    }

    public Image getThirtySecondNote()
    {
        if(reloadImage || _thirtySecond_NoteNoteImage == null)
        {
            _thirtySecond_NoteNoteImage = getImageByResource("/Images/thirty-second_note.png");
            setReloadFlag(false);
        }

        return _thirtySecond_NoteNoteImage;
    }

    public Image getSixtyFourthNote()
    {
        if(reloadImage || _sixtyFourth_NoteImage == null)
        {
            _sixtyFourth_NoteImage = getImageByResource("/Images/sixty-fourth_note.png");
            setReloadFlag(false);
        }

        return _sixtyFourth_NoteImage;
    }

    public Image getOneHundredTwentyEighthNote()
    {
        if(reloadImage || _oneHundredTwentyEighth_NoteImage == null)
        {
            _oneHundredTwentyEighth_NoteImage = getImageByResource("/Images/one-hundred-twenty-eighth_note.png");
            setReloadFlag(false);
        }

        return _oneHundredTwentyEighth_NoteImage;
    }
    //endregion

    //region Instruments
    public Image getInstrumentByName(String instrumentName)
    {
        Image instrument = null;

        switch (instrumentName)
        {
            case "Sound_Effects":
                instrument = getSoundEffects();
                break;
            case "Percussive":
                instrument = getPercussive();
                break;
            case "Ethnic":
                instrument = getEthnic();
                break;
            case "Synth_Effects":
                instrument = getSynthEffects();
                break;
            case "Synth_Pad":
                instrument = getSynthPad();
                break;
            case "Synth_Lead":
                instrument = getSynthLead();
                break;
            case "Pipe":
                instrument = getPipe();
                break;
            case "Reed":
                instrument = getReed();
                break;
            case "Brass":
                instrument = getBrass();
                break;
            case "Ensemble":
                instrument = getEnsemble();
                break;
            case "Strings":
                instrument = getStrings();
                break;
            case "Bass":
                instrument = getBass();
                break;
            case "Guitar":
                instrument = getGuitar();
                break;
            case "Organ":
                instrument = getOrgan();
                break;
            case "Chromatic_Percussion":
                instrument = getChromaticPercussion();
                break;
            case "Piano":
                instrument = getPiano();
                break;
            case "None":
                instrument = getNone();
                break;
        }

        return instrument;
    }

    public Image getSoundEffects()
    {
        if(reloadImage || _soundEffectsImage == null)
        {
            _soundEffectsImage = getImageByResource("/Images/sound_Effects.png");
            setReloadFlag(false);
        }

        return _soundEffectsImage;
    }

    public Image getPercussive()
    {
        if(reloadImage || _percussiveImage == null)
        {
            _percussiveImage = getImageByResource("/Images/percussive.png");
            setReloadFlag(false);
        }

        return _percussiveImage;
    }

    public Image getEthnic()
    {
        if(reloadImage || _ethnicImage == null)
        {
            _ethnicImage = getImageByResource("/Images/ethnic.png");
            setReloadFlag(false);
        }

        return _ethnicImage;
    }

    public Image getSynthEffects()
    {
        if(reloadImage || _synthEffectsImage == null)
        {
            _synthEffectsImage = getImageByResource("/Images/synth_pad_effects.png");
            setReloadFlag(false);
        }

        return _synthEffectsImage;
    }

    public Image getSynthPad()
    {
        if(reloadImage || _synthPadImage == null)
        {
            _synthPadImage = getImageByResource("/Images/synth_pad_effects.png");
            setReloadFlag(false);
        }

        return _synthPadImage;
    }

    public Image getSynthLead()
    {
        if(reloadImage || _synthLeadImage == null)
        {
            _synthLeadImage = getImageByResource("/Images/synth_lead.png");
            setReloadFlag(false);
        }

        return _synthLeadImage;
    }

    public Image getPipe()
    {
        if(reloadImage || _pipeImage == null)
        {
            _pipeImage = getImageByResource("/Images/pipe.png");
            setReloadFlag(false);
        }

        return _pipeImage;
    }

    public Image getReed()
    {
        if(reloadImage || _reedImage == null)
        {
            _reedImage = getImageByResource("/Images/reed.png");
            setReloadFlag(false);
        }

        return _reedImage;
    }

    public Image getBrass()
    {
        if(reloadImage || _brassImage == null)
        {
            _brassImage = getImageByResource("/Images/brass.png");
            setReloadFlag(false);
        }

        return _brassImage;
    }

    public Image getEnsemble()
    {
        if(reloadImage || _ensembleImage == null)
        {
            _ensembleImage = getImageByResource("/Images/ensemble.png");
            setReloadFlag(false);
        }

        return _ensembleImage;
    }

    public Image getStrings()
    {
        if(reloadImage || _stringsImage == null)
        {
            _stringsImage = getImageByResource("/Images/strings.png");
            setReloadFlag(false);
        }

        return _stringsImage;
    }

    public Image getBass()
    {
        if(reloadImage || _bassImage == null)
        {
            _bassImage = getImageByResource("/Images/bass.png");
            setReloadFlag(false);
        }

        return _bassImage;
    }

    public Image getGuitar()
    {
        if(reloadImage || _guitarImage == null)
        {
            _guitarImage = getImageByResource("/Images/guitar.png");
            setReloadFlag(false);
        }

        return _guitarImage;
    }

    public Image getOrgan()
    {
        if(reloadImage || _organImage == null)
        {
            _organImage = getImageByResource("/Images/organs.png");
            setReloadFlag(false);
        }

        return _organImage;
    }

    public Image getChromaticPercussion()
    {
        if(reloadImage || _ChromaticPercussionImage == null)
        {
            _ChromaticPercussionImage = getImageByResource("/Images/chromatic_percussion.png");
            setReloadFlag(false);
        }

        return _ChromaticPercussionImage;
    }

    public Image getPiano()
    {
        if(reloadImage || _pianoImage == null)
        {
            _pianoImage = getImageByResource("/Images/piano.png");
            setReloadFlag(false);
        }

        return _pianoImage;
    }

    public Image getNone()
    {
        if(reloadImage || _noneImage == null)
        {
            _noneImage = getImageByResource("/Images/red_x.png");
            setReloadFlag(false);
        }

        return _noneImage;
    }
    //endregion

    public Image getSpeaker()
    {
        if(reloadImage || _speakerImage == null)
        {
            _speakerImage = getImageByResource("/Images/speaker.png");
            setReloadFlag(false);
        }

        return _speakerImage;
    }

    //endregion
}
