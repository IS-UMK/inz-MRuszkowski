package Images;

import javafx.scene.image.Image;

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

    Image _pianoImage;
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
    public Image getAltoKey()
    {
        if(reloadImage || _altoKeyImage == null)
        {
            _altoKeyImage = getImageByResource("/Images/alto_key.png");
            setReloadFlag(false);
        }

        return _altoKeyImage;
    }

    public Image getViolinKey()
    {
        if(reloadImage || _violinKeyImage == null)
        {
            _violinKeyImage = getImageByResource("/Images/violin_key.png");
            setReloadFlag(false);
        }

        return _violinKeyImage;
    }

    public Image getBassKey()
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
    public Image getPiano()
    {
        if(reloadImage || _pianoImage == null)
        {
            _pianoImage = getImageByResource("/Images/piano.png");
            setReloadFlag(false);
        }

        return _pianoImage;
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
