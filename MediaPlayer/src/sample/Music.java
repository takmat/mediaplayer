package sample;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Music extends Pane implements PlayList{
    private Media music;
    private String mediaPath;
    //private MediaPlayer mediaPlayer;
    SimpleObjectProperty<MediaPlayer> mediaPlayer;
    @FXML
    Label mediaTitle;
    @FXML
    Label performer;
    @FXML
    Label length;
    private Slider time,volumeAdjuster;
    private ImageView playAndPause;
    private Label nowPlayed;

    public Label getMediaTitle() {
        return mediaTitle;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    private StringProperty artist=new SimpleStringProperty(""), title=new SimpleStringProperty("");

    public Music(String mediaPath, SimpleObjectProperty<MediaPlayer> mediaPlayer) {
        loadUI();
        this.mediaPath = mediaPath;
        this.mediaPlayer = mediaPlayer;
        music = new Media(mediaPath);
        performer.textProperty().bind(artist);
        mediaTitle.textProperty().bind(title);
        music.getMetadata().addListener((MapChangeListener<String,Object>) change ->{
            if (change.wasAdded()) { System.out.println(change.getKey() + " : " + change.getValueAdded()); }
            if(change.wasAdded()){
                if ("artist".equals(change.getKey())) {
                    artist.setValue(change.getValueAdded().toString());
                } else if ("title".equals(change.getKey())) {
                    title.setValue(change.getValueAdded().toString());
                } else if ("duration".equals(change.getKey())) {
                   String duration = change.getValueAdded().toString();
                    System.out.println(duration);
                }
            }
            if(artist.get().equals("")){
                String name = mediaPath.substring(mediaPath.lastIndexOf("/")+1,mediaPath.length());
                System.out.println(name.replace("%20"," "));
                title.set(name.replace("%20"," "));
            }
        });

        this.setOnMouseClicked(event -> {
            if(event.getClickCount()==2){

                double volume = 0;
                if(mediaPlayer.isNotNull().getValue()){
                    volume=mediaPlayer.get().getVolume();
                    mediaPlayer.get().dispose();
                }

                mediaPlayer.set(new MediaPlayer(music));
                bindControlls();
                volumeAdjuster.setValue(volume*100);
                mediaPlayer.get().setVolume(volume);
                nowPlayed.setText(mediaTitle.getText());
                mediaPlayer.get().play();
                mediaPlayer.get().setOnEndOfMedia(() ->
                {
                    double volume2 = 0;
                    if(mediaPlayer.isNotNull().getValue()){
                        volume2=mediaPlayer.get().getVolume();
                        mediaPlayer.get().dispose();
                    }
                    Music nextMedia = playListOfMusic.get(playListOfMusic.indexOf(this)+1);
                    mediaPlayer.set(new MediaPlayer(nextMedia.music));
                   // nowPlayed.setText(nextMedia.getMediaPath().substring(nextMedia.getMediaPath().lastIndexOf("/")+1));
                    nowPlayed.setText(nextMedia.getMediaTitle().getText());
                    bindControlls();
                    volumeAdjuster.setValue(volume2*100);
                    mediaPlayer.get().setVolume(volume2);
                    nowPlayed.setText(mediaTitle.getText());
                    mediaPlayer.get().play();
                });
            }
        });
        if(mediaPlayer.isNotNull().getValue())
        mediaPlayer.get().dispose();
        mediaPlayer.set(new MediaPlayer(music));
        //length.setText(music.getDuration().toString());
    }
    public void passReferences(Slider time, Slider volumeAdjuster, ImageView playAndPause, Label nowPlayed){
        this.nowPlayed=nowPlayed;
        this.time = time;
        this.volumeAdjuster = volumeAdjuster;
        this.playAndPause = playAndPause;
        System.out.println(time.toString());
        System.out.println(mediaPlayer.toString());

    }
    private void loadUI(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("music.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (Exception e) {

        }
    }

    private void bindControlls(){
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
