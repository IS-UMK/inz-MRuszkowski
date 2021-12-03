package SongCreatorWindow.View;

import Images.ImageManager;
import SongCreatorWindow.Model.Core.Duration;
import SongCreatorWindow.Model.GlobalSettings;
import javafx.collections.FXCollections;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;

public class ViewMusicSymbolsSelectionHandling
{
    AnchorPane anchorPaneWithNotesAndAccordsSelection;
    AnchorPane anchorPaneWithNotesAndAccordsProperties;
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
    ToggleGroup group;
    RadioButton symbolNote;
    RadioButton symbolAccord;
    ChoiceBox choiceBox;
    //endregion

    public ViewMusicSymbolsSelectionHandling(AnchorPane anchorPaneWithNotesAndAccordsSelection, AnchorPane anchorPaneWithNotesAndAccordsProperties, AnchorPane anchorPaneWithCurrentlySelectedNoteOrAccordProperties)
    {
        this.anchorPaneWithNotesAndAccordsSelection = anchorPaneWithNotesAndAccordsSelection;
        this.anchorPaneWithNotesAndAccordsProperties = anchorPaneWithNotesAndAccordsProperties;
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
                new Label("Type"),
                new Label("Note Duration"),
                new Label("Insertion time"),
                new Label("Instrument")
        };

        group = new ToggleGroup();

        symbolNote = new RadioButton("Single Note");
        symbolAccord = new RadioButton("Accord");

        choiceBox = new ChoiceBox(FXCollections.observableArrayList(Duration.class.getFields()));

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
