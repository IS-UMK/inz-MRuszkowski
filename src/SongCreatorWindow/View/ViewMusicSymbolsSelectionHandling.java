package SongCreatorWindow.View;

import Images.ImageManager;
import SongCreatorWindow.Model.Core.*;
import SongCreatorWindow.Model.Events.IMusicSoundEditionEvent;
import SongCreatorWindow.Model.GlobalSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;

import static SongCreatorWindow.Model.GlobalSettings.InstrumentChoice;

public class ViewMusicSymbolsSelectionHandling implements IMusicSoundEditionEvent
{
    AnchorPane anchorPaneWithNotesAndAccordsSelection;
    VBox vBoxWithNotesAndAccordsProperties;
    VBox vBoxPaneWithCurrentlySelectedNoteOrAccordProperties;

    //region Duration selection
    int notesInRow = 2;
    Canvas musicSymbolsCanvas;
    Canvas interactionCanvas;

    HashMap<Character, Image> musicSymbolsNamesWithImages;
    Character[][] choiceMap;
    double nextImageInterval;
    //endregion

    public ViewMusicSymbolsSelectionHandling(AnchorPane anchorPaneWithNotesAndAccordsSelection, VBox vBoxWithNotesAndAccordsProperties, VBox anchorPaneWithCurrentlySelectedNoteOrAccordProperties)
    {
        this.anchorPaneWithNotesAndAccordsSelection = anchorPaneWithNotesAndAccordsSelection;
        this.vBoxWithNotesAndAccordsProperties = vBoxWithNotesAndAccordsProperties;
        this.vBoxPaneWithCurrentlySelectedNoteOrAccordProperties = anchorPaneWithCurrentlySelectedNoteOrAccordProperties;

        //Duration selection configuration
        musicSymbolsCanvas = new Canvas(anchorPaneWithNotesAndAccordsSelection.getWidth(), anchorPaneWithNotesAndAccordsSelection.getHeight());
        interactionCanvas = new Canvas(0, 0);

        musicSymbolsNamesWithImages = ImageManager.getInstance().setDimensions(GlobalSettings.noteWidth, GlobalSettings.noteHeight).getAllNotesWithNames();
        choiceMap = new Character[notesInRow][(musicSymbolsNamesWithImages.size() + 1) / 2];

        anchorPaneWithNotesAndAccordsSelection.getChildren().add(musicSymbolsCanvas);
        anchorPaneWithNotesAndAccordsSelection.getChildren().add(interactionCanvas);

        //Properties selection configuration
        Label[] accordLabels = new Label[]{
                new Label("Type: "),
                new Label("Tie inclusion: "),
                new Label("WARNING: Instrument for particular\nsound is used only when\nnone instrument for path is selected."),
                new Label("Instrument: ")
        };

        //Properties selection configuration - user selects between note and accord
        ToggleGroup groupForSoundType = new ToggleGroup();

        RadioButton symbolNote = new RadioButton("Single Note ");
        symbolNote.setUserData(SoundTypeSelection.Note);

        RadioButton symbolAccord = new RadioButton("Accord ");
        symbolAccord.setUserData(SoundTypeSelection.Accord);

        String[] accordNames = Accord.AccordType.getAccordTypeNames();
        ChoiceBox accordChoiceBox = new ChoiceBox(FXCollections.observableArrayList(accordNames));
        accordChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String accordName = (String) observableValue.getValue();
                System.out.println(String.format("Accord %s selected", accordName));
                GlobalSettings.accordSelectionName = accordName;
            }
        });
        accordChoiceBox.setValue(accordNames[0]);
        accordChoiceBox.setDisable(true);

        groupForSoundType.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                SoundTypeSelection choice = (SoundTypeSelection) t1.getUserData();
                switch (choice)
                {
                    case Note -> {
                        GlobalSettings.selectedTypeOfSoundToInsertInPath = SoundTypeSelection.Note;
                        accordChoiceBox.setDisable(true);
                    }
                    case Accord -> {
                        GlobalSettings.selectedTypeOfSoundToInsertInPath = SoundTypeSelection.Accord;
                        accordChoiceBox.setDisable(false);
                        System.out.println(String.format("Accord %s selected", GlobalSettings.accordSelectionName));
                    }
                }
                System.out.println(String.format("Sound type set to %s", choice.toString()));
            }
        });

        groupForSoundType.getToggles().addAll(symbolNote, symbolAccord);
        groupForSoundType.selectToggle(symbolNote);

        //Properties selection configuration - user selects tie type
        ToggleGroup groupForTie = new ToggleGroup();

        RadioButton noneTie = new RadioButton("None ");
        noneTie.setUserData(TieSelection.None);

        RadioButton beginOfTie = new RadioButton("Begin ");
        beginOfTie.setUserData(TieSelection.Begin);

        RadioButton continueOfTie = new RadioButton("Continue ");
        continueOfTie.setUserData(TieSelection.Continue);
        continueOfTie.setDisable(true);

        RadioButton endOfTie = new RadioButton("End ");
        endOfTie.setUserData(TieSelection.End);
        endOfTie.setDisable(true);

        groupForTie.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                TieSelection choice = (TieSelection) t1.getUserData();
                switch (choice)
                {
                    case None -> {
                        GlobalSettings.TieBetweenNotes = TieSelection.None;
                    }
                    case Begin -> {
                        GlobalSettings.TieBetweenNotes = TieSelection.Begin;
                    }
                    case Continue -> {
                        GlobalSettings.TieBetweenNotes = TieSelection.Continue;
                    }
                    case End -> {
                        GlobalSettings.TieBetweenNotes = TieSelection.End;
                    }
                }
                System.out.println(String.format("Tie selection set to %s", choice.toString()));
            }
        });

        groupForTie.getToggles().addAll(noneTie, beginOfTie, continueOfTie, endOfTie);
        groupForTie.selectToggle(noneTie);

        //Properties selection configuration - user selects instrument for particular sound
        var instruments = Instrument.getAllInstruments();
        ChoiceBox instrumentChoiceBox = new ChoiceBox(FXCollections.observableArrayList(instruments));
        instrumentChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String instrumentName = (String) observableValue.getValue();
                GlobalSettings.InstrumentForParticularNoteChoice = Instrument.getInstrumentValueByChosenName(instrumentName);
                System.out.println(String.format("Instrument for next notes is set to %s", instrumentName));
            }
        });
        instrumentChoiceBox.setValue(instruments[InstrumentChoice]);

        //Add all components
        var padding = new Insets(10);

        HBox hbox1 = new HBox(accordLabels[0], symbolNote, symbolAccord, accordChoiceBox);
        hbox1.setPadding(padding);

        HBox hbox2 = new HBox(accordLabels[1], noneTie, beginOfTie, continueOfTie, endOfTie);
        hbox2.setPadding(padding);

        HBox hbox3 = new HBox(accordLabels[2]);
        hbox3.setPadding(padding);

        HBox hbox4 = new HBox(accordLabels[3], instrumentChoiceBox);
        hbox4.setPadding(padding);

        vBoxWithNotesAndAccordsProperties.getChildren().addAll(hbox1, hbox2, hbox3, hbox4);

        refreshPanel();
        drawSelectionOfNote(0, 0);
    }

    /**
     * Method that refreshes the entire panel including: sound duration choose panel, properties of new sounds and options of already existing sound
     */
    public void refreshPanel()
    {
        redrawMusicSymbols();
        redrawOptionsOfCurrentlySelectedSound();
    }

    /**
     * Method draws music symbols so user can make up his mind which to choose
     */
    private void redrawMusicSymbols()
    {
        double currentWidth = musicSymbolsCanvas.getWidth();
        nextImageInterval = currentWidth / notesInRow;

        var gc = musicSymbolsCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, currentWidth, musicSymbolsCanvas.getHeight());

        double drawXCord = 0 , drawYCord = 0;

        for (Character musicSymbolName : musicSymbolsNamesWithImages.keySet())
        {
            gc.drawImage(musicSymbolsNamesWithImages.get(musicSymbolName), drawXCord, drawYCord, nextImageInterval, nextImageInterval);
            System.out.println(String.format("Symbol loaded at %f %f", drawXCord, drawYCord));

            choiceMap[(int)(drawXCord/nextImageInterval)][(int)(drawYCord/nextImageInterval)] = musicSymbolName;

            drawXCord += nextImageInterval;

            if(drawXCord >= notesInRow * nextImageInterval)
            {
                drawXCord = 0;
                drawYCord += nextImageInterval;
            }
        }
    }

    /**
     * Method draws rectangle to highlight the user's choice of sound duration
     * @param mouseClickX X coordinate of mouse on selection canvas
     * @param mouseClickY Y  coordinate of mouse on selection canvas
     */
    public void drawSelectionOfNote(double mouseClickX, double mouseClickY)
    {
        double drawX = 0, drawY = 0;
        while(drawX + nextImageInterval < mouseClickX) drawX += nextImageInterval;
        while(drawY + nextImageInterval < mouseClickY) drawY += nextImageInterval;

        try {
            GlobalSettings.chosenNote = choiceMap[(int) (drawX / nextImageInterval)][(int) (drawY / nextImageInterval)];
            System.out.println(String.format("User chose %s", GlobalSettings.chosenNote));

            clearMusicSymbolSelection();

            interactionCanvas.setLayoutX(drawX);
            interactionCanvas.setLayoutY(drawY);
            interactionCanvas.setWidth(nextImageInterval);
            interactionCanvas.setHeight(nextImageInterval);

            var gc = interactionCanvas.getGraphicsContext2D();
            gc.setStroke(GlobalSettings.selectionColor);

            gc.setLineWidth(GlobalSettings.strokeLineBorderWidth);
            gc.strokeRect(0, 0, nextImageInterval, nextImageInterval);
        }
        catch (Exception e) { }
    }

    /**
     * Method that removes the rectangle highlighting the user's choice of sound duration
     */
    private void clearMusicSymbolSelection()
    {
        var gc = interactionCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, interactionCanvas.getWidth(), interactionCanvas.getHeight());

        interactionCanvas.setWidth(0);
        interactionCanvas.setHeight(0);
    }

    private void redrawOptionsOfCurrentlySelectedSound()
    {

    }

    @Override
    public void onMusicSoundSelectedToEdition(Path path, IPlayable musicSound)
    {
        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().clear();

        //options music sound configuration
        Label[] optionLabels = new Label[]{
                new Label("Type: "),
                new Label("Duration: "),
                new Label("Sound: "),
                new Label("Octave: "),
                new Label("Occurrence time: "),
                new Label("Instrument: "),
                new Label("Modification: ")
        };

        ToggleGroup groupForSoundTypeOption = new ToggleGroup();

        RadioButton symbolNoteOption = new RadioButton("Single Note ");
        symbolNoteOption.setUserData(SoundTypeSelection.Note);

        RadioButton symbolAccordOption = new RadioButton("Accord ");
        symbolAccordOption.setUserData(SoundTypeSelection.Accord);

        String[] accordNames = Accord.AccordType.getAccordTypeNames();
        ChoiceBox accordChoiceBoxOption = new ChoiceBox(FXCollections.observableArrayList(accordNames));
        accordChoiceBoxOption.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String accordName = (String) observableValue.getValue();
                System.out.println(String.format("Accord %s selected", accordName));
                //add code for change
            }
        });
        accordChoiceBoxOption.setValue(accordNames[0]);

        groupForSoundTypeOption.getToggles().addAll(symbolNoteOption, symbolAccordOption);
        boolean isNote = musicSound.getSoundType().equals("Note") ? true : false;
        groupForSoundTypeOption.selectToggle(isNote ? symbolNoteOption : symbolAccordOption);
        accordChoiceBoxOption.setDisable(!isNote);

        ChoiceBox musicSymbolDuration = new ChoiceBox(FXCollections.observableArrayList(Duration.getDurations()));
        musicSymbolDuration.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String duration = (String) observableValue.getValue();
                System.out.println(String.format("Duration '%s' selected", duration));
                //add code for change
            }
        });
        musicSymbolDuration.setValue(Duration.getDurationNameByCharacter(musicSound.getDuration()));

        //Symbols
        String symbols = musicSound.getValue();
        String sound = symbols.split("[0-9]")[0];
        String octave = symbols.substring(symbols.length() - 1);

        ChoiceBox soundChoiceBox = new ChoiceBox(FXCollections.observableArrayList(new String[] { "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"} ));
        soundChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                String sound = (String) observableValue.getValue();
                System.out.println(String.format("Sound %s selected", sound));
                //add code for change
            }
        });
        soundChoiceBox.setValue(sound);

        TextField octaveTextField = new TextField(octave);
        octaveTextField.setPrefColumnCount(2);
        octaveTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    String octave = (String) observable.getValue();
                    octaveTextField.setText(newValue.replaceAll("[^\\d]", ""));
                    System.out.println(String.format("Octave %s selected", octave));
                }
            }
        });
        octaveTextField.setText(octave);

        //Occurrence Time
        String time = Double.toString(Path.getSoundTimeOccurrence(musicSound));
        TextField occurrenceTimeTextField = new TextField(time);
        occurrenceTimeTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                String occurrenceTime = (String) observableValue.getValue();

                if (!newValue.matches("(?<=^| )\\d+(\\.\\d+)?(?=$| )|(?<=^| )\\.\\d+(?=$| )")) {
                    occurrenceTimeTextField.setText(oldValue);
                    return;
                }
                
                occurrenceTimeTextField.setText(newValue);
                System.out.println(String.format("Occurrence time set to %s", occurrenceTime));
            }
        });
        occurrenceTimeTextField.setText(time);

        //Instrument
        var instruments = Instrument.getAllInstruments();
        ChoiceBox instrumentChoiceBox = new ChoiceBox(FXCollections.observableArrayList(instruments));
        instrumentChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String instrumentName = (String) observableValue.getValue();
                //change instrument for sound
                System.out.println(String.format("Instrument for chosen sound is set to %s", instrumentName));
            }
        });
        instrumentChoiceBox.setValue(instruments[InstrumentChoice]);

        Insets padding = new Insets(10);

        HBox hbox11 = new HBox(optionLabels[0], symbolNoteOption, symbolAccordOption, accordChoiceBoxOption);
        hbox11.setPadding(padding);

        HBox hbox12 = new HBox(optionLabels[1], musicSymbolDuration);
        hbox12.setPadding(padding);

        HBox hbox13 = new HBox(optionLabels[2], soundChoiceBox);
        hbox13.setPadding(padding);

        HBox hbox14 = new HBox(optionLabels[3], octaveTextField);
        hbox14.setPadding(padding);

        HBox hbox15 = new HBox(optionLabels[4], occurrenceTimeTextField);
        hbox15.setPadding(padding);

        HBox hbox16 = new HBox(optionLabels[5], instrumentChoiceBox);
        hbox16.setPadding(padding);

        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().addAll(hbox11, hbox12, hbox13, hbox14, hbox15, hbox16);

        ToggleGroup modificationOfSound = new ToggleGroup();
        RadioButton noneModificationOfSound = new RadioButton("None ");
        modificationOfSound.getToggles().add(noneModificationOfSound);

        HBox hbox17 = new HBox(optionLabels[6], noneModificationOfSound);
        hbox17.setPadding(padding);

        if(sound.length() == 1)
            modificationOfSound.selectToggle(noneModificationOfSound);

        //sharp
        if(sound.charAt(0) != 'E' && sound.charAt(0) != 'B')
        {
            RadioButton SharpModificationOfSound = new RadioButton("Sharp ");
            modificationOfSound.getToggles().add(SharpModificationOfSound);
            hbox17.getChildren().add(SharpModificationOfSound);

            if(sound.length() > 1 && sound.charAt(1) == '#')
                modificationOfSound.selectToggle(SharpModificationOfSound);
        }

        //flat
        if(sound.charAt(0) != 'C' && sound.charAt(0) != 'F')
        {
            RadioButton flatModificationOfSound = new RadioButton("Flat ");
            modificationOfSound.getToggles().add(flatModificationOfSound);
            hbox17.getChildren().add(flatModificationOfSound);

            if(sound.length() > 1 && sound.charAt(1) == 'b')
                modificationOfSound.selectToggle(flatModificationOfSound);
        }

        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().add(hbox17);
    }

    @Override
    public void onMusicSoundClearSelection(Path path, IPlayable musicSound)
    {

    }
}
