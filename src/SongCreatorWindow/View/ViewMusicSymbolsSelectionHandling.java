package SongCreatorWindow.View;

import Images.ImageManager;
import SongCreatorWindow.Model.Core.Accord;
import SongCreatorWindow.Model.Core.Instrument;
import SongCreatorWindow.Model.Core.SoundTypeSelection;
import SongCreatorWindow.Model.Core.TieSelection;
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

public class ViewMusicSymbolsSelectionHandling
{
    AnchorPane anchorPaneWithNotesAndAccordsSelection;
    VBox vBoxWithNotesAndAccordsProperties;
    AnchorPane anchorPaneWithCurrentlySelectedNoteOrAccordProperties;

    //region Duration selection
    int notesInRow = 2;
    Canvas musicSymbolsCanvas;
    Canvas interactionCanvas;

    HashMap<Character, Image> musicSymbolsNamesWithImages;
    Character[][] choiceMap;
    double nextImageInterval;
    //endregion

    //region Properties Selection
    Label[] labels;
    ToggleGroup groupForSoundType;
    ToggleGroup groupForTie;
    ChoiceBox instrumentChoiceBox;
    ChoiceBox accordChoiceBox;
    //endregion

    public ViewMusicSymbolsSelectionHandling(AnchorPane anchorPaneWithNotesAndAccordsSelection, VBox vBoxWithNotesAndAccordsProperties, AnchorPane anchorPaneWithCurrentlySelectedNoteOrAccordProperties)
    {
        this.anchorPaneWithNotesAndAccordsSelection = anchorPaneWithNotesAndAccordsSelection;
        this.vBoxWithNotesAndAccordsProperties = vBoxWithNotesAndAccordsProperties;
        this.anchorPaneWithCurrentlySelectedNoteOrAccordProperties = anchorPaneWithCurrentlySelectedNoteOrAccordProperties;

        //Duration selection configuration
        musicSymbolsCanvas = new Canvas(anchorPaneWithNotesAndAccordsSelection.getWidth(), anchorPaneWithNotesAndAccordsSelection.getHeight());
        interactionCanvas = new Canvas(0, 0);

        musicSymbolsNamesWithImages = ImageManager.getInstance().setDimensions(GlobalSettings.noteWidth, GlobalSettings.noteHeight).getAllNotesWithNames();
        choiceMap = new Character[notesInRow][(musicSymbolsNamesWithImages.size() + 1) / 2];

        anchorPaneWithNotesAndAccordsSelection.getChildren().add(musicSymbolsCanvas);
        anchorPaneWithNotesAndAccordsSelection.getChildren().add(interactionCanvas);

        //Properties selection configuration
        labels = new Label[]{
                new Label("Type: "),
                new Label("Tie inclusion: "),
                new Label("WARNING: Instrument for particular\nsound is used only when\nnone instrument for path is selected."),
                new Label("Instrument: ")
        };

        //Properties selection configuration - user selects between note and accord
        groupForSoundType = new ToggleGroup();

        RadioButton symbolNote = new RadioButton("Single Note ");
        symbolNote.setUserData(SoundTypeSelection.Note);

        RadioButton symbolAccord = new RadioButton("Accord ");
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
        instrumentChoiceBox = new ChoiceBox(FXCollections.observableArrayList(instruments));
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

        HBox hbox1 = new HBox(labels[0], symbolNote, symbolAccord, accordChoiceBox);
        hbox1.setPadding(padding);

        HBox hbox2 = new HBox(labels[1], noneTie, beginOfTie, continueOfTie, endOfTie);
        hbox2.setPadding(padding);

        HBox hbox3 = new HBox(labels[2]);
        hbox3.setPadding(padding);

        HBox hbox4 = new HBox(labels[3], instrumentChoiceBox);
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
        redrawPropertiesOfNewlyInsertedSound();
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

    private void redrawPropertiesOfNewlyInsertedSound()
    {

    }

    private void redrawOptionsOfCurrentlySelectedSound()
    {

    }
}
