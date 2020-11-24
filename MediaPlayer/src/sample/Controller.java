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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
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
    @FXML
    Button nextMedia;
    @FXML
    Button previousMedia;
    private Music currentMusic;
    private MediaList mediaList;



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

        prevButtonImage.setFitWidth(71.0);
        prevButtonImage.setFitHeight(71.0);
        nextButtonImage.setFitWidth(71.0);
        nextButtonImage.setFitHeight(71.0);
        nextMedia.setGraphic(nextButtonImage);
        previousMedia.setGraphic(prevButtonImage);
        Rectangle r = new Rectangle(35.5,0,35.5,71);//nextMedia.setGraphic(nextButtonImage);
        Rectangle r2 = new Rectangle(0,0,35.5,71);
        Circle c2 = new Circle(MediaPlayButton.getPrefWidth()/2,MediaPlayButton.getPrefHeight()/2,playAndPause.getFitWidth()/2);
        Shape s1 = Shape.union(r,c2);
        Shape s2 = Shape.union(r2,c2);
        //prevButtonImage.setClip(s1);
        //previousMedia.setShape(s1);
        nextMedia.setClip(s2);
        previousMedia.setClip(s1);


        Circle c = new Circle(MediaPlayButton.getPrefWidth()/2,MediaPlayButton.getPrefHeight()/2,playAndPause.getFitWidth()/2);
        MediaPlayButton.setClip(c);
        MediaPlayButton.setGraphic(this.playAndPause);
        //MediaPlayButton
        MediaPlayButton.disableProperty().bind(mediaPlayer.isNull());
        System.out.println(previousMedia.getWidth());



        nextMedia.setOnAction(event -> {
            nextMedia();
        });

        previousMedia.setOnAction(event -> {
            prevMedia();
        });
//time,volumeAdjuster,playAndPause,nowPlaying,duration,volumeNumber,currentMusic
        mediaList = new MediaList(time,volumeAdjuster,nowPlaying,duration,volumeNumber,mediaPlayer.get());

    }
    private void nextMedia(){
        if(playListOfMusic.indexOf(mediaList.getCurrentMusic())<playListOfMusic.size()-1){
            previousMedia.setDisable(false);
            mediaList.nextMedia();
            if(playListOfMusic.indexOf(mediaList.getCurrentMusic())==playListOfMusic.size()-1){
                nextMedia.setDisable(true);
            }
        }

    }
    private void prevMedia(){
        if(playListOfMusic.indexOf(mediaList.getCurrentMusic())>0){
            nextMedia.setDisable(false);
            mediaList.prevMedia();

            if(playListOfMusic.indexOf(mediaList.getCurrentMusic())==0){
                previousMedia.setDisable(true);
            }
        }

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


        if(list!=null){
        for(File f : list){
            if(playList.contains(f.toURI().toString())){

            }
            else{
                playList.add(f.toURI().toString());
                Music music = new Music(f.toURI().toString(),mediaPlayer);
                playListOfMusic.add(music);
                music.passReferences(time,volumeAdjuster,playAndPause,nowPlaying,duration,volumeNumber,mediaList);
                addContextMenuToMusic(music);
                listOfMedia.getChildren().add(music);
            }

        }
        if(mediaPlayer.isNotNull().getValue())
            mediaPlayer.get().dispose();

        NumberFormat formatter = NumberFormat.getIntegerInstance();
        formatter.setMinimumIntegerDigits(2);



        //bindMediaPlayer();

       mediaList.startPlay();
        //mediaView=new MediaView(mediaPlayer);

        mediaPlayer.get().setVolume(0.5);
        }

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
    public void loadStatistics(){
        Parent root =null;
        FXMLLoader loader =new FXMLLoader(getClass().getResource("statistics.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Statistics controller = loader.getController();
        loader.setRoot(Statistics.class);
        loader.setController(Statistics.class);
        Stage statistics = new Stage(StageStyle.DECORATED);
        statistics.setTitle("Lejátszási statisztikák");
        statistics.setScene(new Scene(root));
        statistics.show();

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
