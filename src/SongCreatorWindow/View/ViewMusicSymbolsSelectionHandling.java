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

import java.util.*;

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

    Insets padding = new Insets(10);

    //region Properties selection configuration
    Label[] accordLabels;

    ToggleGroup groupForSoundType;
    RadioButton symbolNote;
    RadioButton symbolAccord;

    ChoiceBox accordChoiceBox;

    ToggleGroup groupForTie;
    RadioButton noneTie;
    RadioButton beginOfTie;
    RadioButton continueOfTie;
    RadioButton endOfTie;

    ChoiceBox instrumentChoiceBoxForNextSound;
    //endregion

    //region Options music sound configuration
    Label[] optionLabels;

    ToggleGroup groupForSoundTypeOption;
    RadioButton symbolNoteOption;
    RadioButton symbolAccordOption;

    ChoiceBox accordChoiceBoxOption;

    ChoiceBox musicSymbolDuration;

    List<String> availableSounds = Arrays.asList( "C", "D", "E", "F", "G", "A", "B" );
    List<Integer> octaves = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    ChoiceBox soundChoiceBox;

    ChoiceBox octaveChoiceBox;

    TextField occurrenceTimeTextField;

    ChoiceBox instrumentChoiceBoxForCurrentlySelectedSoundToEdition;

    Label soundTieLabel;

    List<HBox> optionsContent;
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
        accordLabels = new Label[]{
                new Label("Type: "),
                new Label("Tie inclusion: "),
                new Label("WARNING: Instrument for particular\nsound is used only when\nnone instrument for path is selected."),
                new Label("Instrument: ")
        };

        //Properties selection configuration - user selects between note and accord
        groupForSoundType = new ToggleGroup();

        symbolNote = new RadioButton("Single Note ");
        symbolNote.setUserData(SoundTypeSelection.Note);

        symbolAccord = new RadioButton("Accord ");
        symbolAccord.setUserData(SoundTypeSelection.Accord);

        String[] accordNames = Accord.AccordType.getAccordTypeNames();
        accordChoiceBox = new ChoiceBox(FXCollections.observableArrayList(accordNames));
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
        groupForTie = new ToggleGroup();

        noneTie = new RadioButton("None ");
        noneTie.setUserData(TieSelection.None);

        beginOfTie = new RadioButton("Begin ");
        beginOfTie.setUserData(TieSelection.Begin);

        continueOfTie = new RadioButton("Continue ");
        continueOfTie.setUserData(TieSelection.Continue);

        endOfTie = new RadioButton("End ");
        endOfTie.setUserData(TieSelection.End);

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
        instrumentChoiceBoxForNextSound = new ChoiceBox(FXCollections.observableArrayList(instruments));
        instrumentChoiceBoxForNextSound.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String instrumentName = (String) observableValue.getValue();
                GlobalSettings.InstrumentForParticularNoteChoice = Instrument.getInstrumentValueByChosenName(instrumentName);
                System.out.println(String.format("Instrument for next notes is set to %s", instrumentName));
            }
        });
        instrumentChoiceBoxForNextSound.setValue(instruments[InstrumentChoice]);

        //Add all components

        HBox hbox1 = new HBox(accordLabels[0], symbolNote, symbolAccord, accordChoiceBox);
        hbox1.setPadding(padding);

        HBox hbox2 = new HBox(accordLabels[1], noneTie, beginOfTie, continueOfTie, endOfTie);
        hbox2.setPadding(padding);

        HBox hbox3 = new HBox(accordLabels[2]);
        hbox3.setPadding(padding);

        HBox hbox4 = new HBox(accordLabels[3], instrumentChoiceBoxForNextSound);
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
        optionsContent = new LinkedList<>();

        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().clear();

        //Options music sound configuration
        optionLabels = new Label[]{
                new Label("Type: "),
                new Label("Duration: "),
                new Label("Sound: "),
                new Label("Octave: "),
                new Label("Occurrence time: "),
                new Label("Instrument: "),
                new Label("Tie type: "),
                new Label("Modification: ")
        };

        groupForSoundTypeOption = new ToggleGroup();

        symbolNoteOption = new RadioButton("Single Note ");
        symbolNoteOption.setUserData(SoundTypeSelection.Note);

        symbolAccordOption = new RadioButton("Accord ");
        symbolAccordOption.setUserData(SoundTypeSelection.Accord);

        String[] accordNames = Accord.AccordType.getAccordTypeNames();

        groupForSoundTypeOption.getToggles().addAll(symbolNoteOption, symbolAccordOption);
        boolean isNote = musicSound.getSoundType().equals("Note") ? true : false;
        groupForSoundTypeOption.selectToggle(isNote ? symbolNoteOption : symbolAccordOption);

        accordChoiceBoxOption = new ChoiceBox(FXCollections.observableArrayList(accordNames));
        accordChoiceBoxOption.setDisable(isNote);
        accordChoiceBoxOption.setValue(accordNames[0]);
        accordChoiceBoxOption.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object newValue, Object oldValue) {
                String accordName = (String) observableValue.getValue();
                System.out.println(String.format("Accord %s selected", accordName));

                path.ChangeAccordName(musicSound, accordName);
            }
        });

        musicSymbolDuration = new ChoiceBox(FXCollections.observableArrayList(Duration.getDurations()));
        musicSymbolDuration.setValue(Duration.getDurationNameByCharacter(musicSound.getDuration()));
        musicSymbolDuration.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newVale) {
                String duration = (String) observableValue.getValue();
                System.out.println(String.format("Duration '%s' selected", duration));

                path.changeSoundDuration(musicSound, Duration.getCharacterByDurationName(duration));
            }
        });

        //Symbols
        String symbols = musicSound.getValue();
        String sound = symbols.split("[0-9]")[0];
        String octave = symbols.substring(symbols.length() - 1);

        soundChoiceBox = new ChoiceBox(FXCollections.observableArrayList(availableSounds));
        soundChoiceBox.setValue(sound.substring(0, 1));
        var soundChoiceBoxListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if(oldValue.equals("") || newValue.equals("")){
                    System.err.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    System.err.println("Old " + oldValue + " new " + newValue);
                    return;
                }

                String sound = (String) observableValue.getValue();
                int oldIndex = availableSounds.indexOf(((String)oldValue).substring(0, 1));
                int newIndex = availableSounds.indexOf(((String)newValue).substring(0, 1));

                path.changeSoundHeight(musicSound, newIndex - oldIndex, 0);

                System.out.println(String.format("Sound %s selected", sound));
            }
        };
        soundChoiceBox.getSelectionModel().selectedItemProperty().addListener(soundChoiceBoxListener);

        octaveChoiceBox = new ChoiceBox(FXCollections.observableArrayList(octaves));
        octaveChoiceBox.setValue(Integer.parseInt(octave));
        octaveChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue observableValue, Integer oldValue, Integer newValue) {
                if(oldValue == newValue)
                    return;

                if(oldValue == 10)
                {
                    soundChoiceBox.getSelectionModel().selectedItemProperty().removeListener(soundChoiceBoxListener);
                    Object sound = soundChoiceBox.getValue();
                    soundChoiceBox.setItems(FXCollections.observableArrayList(availableSounds));
                    soundChoiceBox.setValue(sound);
                    soundChoiceBox.getSelectionModel().selectedItemProperty().addListener(soundChoiceBoxListener);
                }
                if(newValue == 10)
                {
                    Object sound = soundChoiceBox.getValue();
                    if(sound.toString().equals("A") || sound.toString().equals("B"))
                    {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Chosen sound does not exist in this octave");
                        alert.showAndWait();
                        octaveChoiceBox.setValue(oldValue);
                        return;
                    }

                    soundChoiceBox.getSelectionModel().selectedItemProperty().removeListener(soundChoiceBoxListener);

                    soundChoiceBox.setItems(FXCollections.observableArrayList(availableSounds.subList(0, availableSounds.size() - 2)));
                    soundChoiceBox.setValue(sound);
                    soundChoiceBox.getSelectionModel().selectedItemProperty().addListener(soundChoiceBoxListener);
                }

                int move = newValue - oldValue;
                path.changeSoundHeight(musicSound, 0,  move);

                System.out.println(String.format("Octave %d selected", move));
            }
        });

        //Occurrence Time
        String time = Double.toString(Path.getSoundTimeOccurrence(musicSound.getTimeX()));
        occurrenceTimeTextField = new TextField(time);
        occurrenceTimeTextField.setText(time);
        occurrenceTimeTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                String occurrenceTime = (String) observableValue.getValue();

                if (!newValue.matches("(?<=^| )\\d+(\\.\\d+)?(?=$| )|(?<=^| )\\.\\d+(?=$| )")) {
                    occurrenceTimeTextField.setText(oldValue);
                    return;
                }

                occurrenceTimeTextField.setText(newValue);
                path.changeSoundOccurrence(musicSound, Double.parseDouble(newValue));
                System.out.println(String.format("Occurrence time set to %s", occurrenceTime));
            }
        });

        //Instrument
        var instruments = Instrument.getAllInstruments();
        instrumentChoiceBoxForCurrentlySelectedSoundToEdition = new ChoiceBox(FXCollections.observableArrayList(instruments));
        instrumentChoiceBoxForCurrentlySelectedSoundToEdition.setValue(instruments[musicSound.getInstrument()]);
        instrumentChoiceBoxForCurrentlySelectedSoundToEdition.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                String instrumentName = (String) observableValue.getValue();
                GlobalSettings.InstrumentChoice = Instrument.getInstrumentValueByChosenName(instrumentName);
                System.out.println(String.format("Instrument for chosen sound is set to %s", instrumentName));
                path.changeSoundInstrument(musicSound, instrumentName);
            }
        });

        //Tie of Sound
        List<String> tiesTypes = new LinkedList<>();
        for(var field : TieSelection.class.getDeclaredFields())
            tiesTypes.add(field.getName());

        tiesTypes.remove(tiesTypes.size() - 1); // remove artifact - $VALUES

        soundTieLabel = new Label(musicSound.getSoundConcatenation().toString());

        HBox hbox11 = new HBox(optionLabels[0], symbolNoteOption, symbolAccordOption, accordChoiceBoxOption);
        hbox11.setPadding(padding);

        HBox hbox12 = new HBox(optionLabels[1], musicSymbolDuration);
        hbox12.setPadding(padding);

        HBox hbox13 = new HBox(optionLabels[2], soundChoiceBox);
        hbox13.setPadding(padding);

        HBox hbox14 = new HBox(optionLabels[3], octaveChoiceBox);
        hbox14.setPadding(padding);

        HBox hbox15 = new HBox(optionLabels[4], occurrenceTimeTextField);
        hbox15.setPadding(padding);

        HBox hbox16 = new HBox(optionLabels[5], instrumentChoiceBoxForCurrentlySelectedSoundToEdition);
        hbox16.setPadding(padding);

        HBox hbox17 = new HBox(optionLabels[6], soundTieLabel);
        hbox17.setPadding(padding);

        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().addAll(hbox11, hbox12, hbox13, hbox14, hbox15, hbox16, hbox17);

        HBox hbox18 = createModificationSection(path, musicSound);

        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().add(hbox18);

        optionsContent.add(hbox11);
        optionsContent.add(hbox12);
        optionsContent.add(hbox13);
        optionsContent.add(hbox14);
        optionsContent.add(hbox15);
        optionsContent.add(hbox16);
        optionsContent.add(hbox17);
        optionsContent.add(hbox18);
    }

    private HBox createModificationSection(Path path, IPlayable musicSound) {
        String sound = musicSound.getValue().split("[0-9]")[0];

        ToggleGroup modificationOfSound = new ToggleGroup();
        RadioButton noneModificationOfSound = new RadioButton("None ");
        noneModificationOfSound.setUserData(SoundModification.None);
        modificationOfSound.getToggles().add(noneModificationOfSound);

        HBox hbox18 = new HBox(optionLabels[7], noneModificationOfSound);
        hbox18.setPadding(padding);

        if(sound.length() == 1)
            modificationOfSound.selectToggle(noneModificationOfSound);

        //sharp
        if(sound.charAt(0) != 'E' && sound.charAt(0) != 'B')
        {
            RadioButton sharpModificationOfSound = new RadioButton("Sharp ");
            sharpModificationOfSound.setUserData(SoundModification.Sharp);

            modificationOfSound.getToggles().add(sharpModificationOfSound);
            hbox18.getChildren().add(sharpModificationOfSound);

            if(sound.length() > 1 && sound.charAt(1) == '#')
                modificationOfSound.selectToggle(sharpModificationOfSound);
        }

        //flat
        if(sound.charAt(0) != 'C' && sound.charAt(0) != 'F')
        {
            RadioButton flatModificationOfSound = new RadioButton("Flat ");
            flatModificationOfSound.setUserData(SoundModification.Flat);

            modificationOfSound.getToggles().add(flatModificationOfSound);
            hbox18.getChildren().add(flatModificationOfSound);

            if(sound.length() > 1 && sound.charAt(1) == 'b')
                modificationOfSound.selectToggle(flatModificationOfSound);
        }

        modificationOfSound.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                SoundModification modification = (SoundModification) observableValue.getValue().getUserData();
                path.setSoundModification(musicSound, modification);
            }
        });

        return hbox18;
    }

    @Override
    public void onMusicSoundClearSelection()
    {
        for(HBox content : optionsContent)
            vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().remove(content);
    }

    @Override
    public void onMusicSoundTieCheck(IPlayable musicSound, TieSelection previousTie)
    {
        TieSelection tie = GlobalSettings.TieBetweenNotes;//musicSound.getSoundConcatenation();

        switch (previousTie) {
            case None, End -> {
                if(tie == TieSelection.Continue || tie == TieSelection.End)
                    groupForTie.selectToggle(noneTie);
            }
            case Begin, Continue -> {
                if(tie == TieSelection.None || tie == TieSelection.Begin)
                    groupForTie.selectToggle(endOfTie);
            }
        }
    }

    @Override
    public void onMusicSoundOccurrenceTimeChanged(Path path, IPlayable musicSound)
    {

    }

    @Override
    public void onMusicSoundHeightChange(Path path, IPlayable musicSound) {
        var toReplace = optionsContent.get(optionsContent.size() - 1);
        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().remove(toReplace);
        optionsContent.remove(toReplace);

        HBox newOne = createModificationSection(path, musicSound);
        vBoxPaneWithCurrentlySelectedNoteOrAccordProperties.getChildren().add(newOne);
        optionsContent.add(newOne);
    }

    @Override
    public void onMusicSoundDurationChange(Path path, IPlayable musicSound) {

    }

    @Override
    public void onMusicSoundModified(Path path, IPlayable musicSound) {

    }

    @Override
    public void onAccordNameChanged(Path path, IPlayable musicSound) {

    }
}
