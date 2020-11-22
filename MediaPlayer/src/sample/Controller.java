package sample;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Controller implements PlayList {
    @FXML
    Button MediaPlayButton;

    private ImageView playAndPause;
    @FXML
    private MediaView mediaView;
    SimpleObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    @FXML
    Button getFiles;
    private Media music;
    @FXML
    FlowPane listOfMedia;
    @FXML
    Slider time;
    @FXML
    Slider volumeAdjuster;
    @FXML
    Label nowPlaying;
    @FXML
    Label duration;
    @FXML
    Label volumeNumber;



    public Controller() {
    }

    @FXML
    private void initialize() {
        System.out.println("start");

        MediaPlayButton.setPrefSize(71,71);

        Bounds buttonBounds = MediaPlayButton.getBoundsInParent();
        System.out.println(buttonBounds.getMaxX());
        playAndPause = new ImageView(this.play);
        playAndPause.setFitWidth(71.0);
        playAndPause.setFitHeight(71.0);
        Circle c = new Circle(MediaPlayButton.getPrefWidth()/2,MediaPlayButton.getPrefHeight()/2,playAndPause.getFitWidth()/2);
        MediaPlayButton.setClip(c);
        MediaPlayButton.setGraphic(this.playAndPause);
        //MediaPlayButton
        MediaPlayButton.disableProperty().bind(mediaPlayer.isNull());




    }

    @FXML
    private void playAndPauseMedia() {
        if(mediaPlayer.get().getStatus().equals(MediaPlayer.Status.PLAYING)){
                mediaPlayer.get().pause();
        }
        else {


            mediaPlayer.get().getMedia().getMetadata().addListener((MapChangeListener<String, Object>) change -> {
                String artist = "", title = "";
                if ("artist".equals(change.getKey())) {
                    artist = change.getValueAdded().toString();
                } else if ("title".equals(change.getKey())) {
                    title = change.getValueAdded().toString();
                }
                nowPlaying.setText(artist + " " + title);
            });
            mediaPlayer.get().play();



        }

    }
    @FXML
    private void getFiles(){
        FileChooser fc = new FileChooser();
        Stage stage = new Stage();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Médiafájlok","*.mp3") //,"*.jpeg","*.jpg","*.bmp"
        );
        List<File> list =
                fc.showOpenMultipleDialog(stage);


        for(File f : list){
            if(playList.contains(f.toURI().toString())){

            }
            else{
                playList.add(f.toURI().toString());
                Music music = new Music(f.toURI().toString(),mediaPlayer);
                playListOfMusic.add(music);
                music.passReferences(time,volumeAdjuster,playAndPause,nowPlaying,duration,volumeNumber);
                addContextMenuToMusic(music);
                listOfMedia.getChildren().add(music);
            }

        }
        if(mediaPlayer.isNotNull().getValue())
            mediaPlayer.get().dispose();

        NumberFormat formatter = NumberFormat.getIntegerInstance();
        formatter.setMinimumIntegerDigits(2);



        //bindMediaPlayer();

        //music = new Media(playList.get(0));
        //mediaPlayer.set(new MediaPlayer(music));
        //mediaView=new MediaView(mediaPlayer);

        mediaPlayer.get().setVolume(0.5);

    }
    private void addContextMenuToMusic(Music m){
        m.setOnContextMenuRequested( event -> {
            MusicContextMenu musicContextMenu = new MusicContextMenu(m);
            musicContextMenu.show(m,event.getScreenX(),event.getScreenY());
        });
    }
    private class MusicContextMenu extends ContextMenu {
        public MusicContextMenu(Music music){
            MenuItem editpane = new MenuItem("Törlés");
            editpane.setOnAction(event ->
            {
                playList.remove(music.getMediaPath());
                listOfMedia.getChildren().remove(music);
            });

            getItems().add(editpane);
        }
    }

    private void bindMediaPlayer(){
        volumeAdjuster.setValue(mediaPlayer.get().getVolume()*100);
        volumeAdjuster.valueProperty().addListener(observable -> {
            mediaPlayer.get().setVolume(volumeAdjuster.getValue()/100);
        });

        mediaPlayer.get().statusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(MediaPlayer.Status.READY)){
                time.setMax(mediaPlayer.get().getMedia().getDuration().toSeconds());
            }
            if(newValue.equals(MediaPlayer.Status.PLAYING)){
                playAndPause.setImage(pause);
            }
            else if(newValue.equals(MediaPlayer.Status.PAUSED)){
                playAndPause.setImage(play);
            }
        });

        mediaPlayer.get().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            time.setValue(newValue.toSeconds());
        });
        time.setOnMousePressed(event -> {
            mediaPlayer.get().seek(Duration.seconds(time.getValue()));
        });
        time.setOnMouseDragged(event -> {
            mediaPlayer.get().seek(Duration.seconds(time.getValue()));
        });
    }



}
