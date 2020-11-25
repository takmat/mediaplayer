package sample.entity;

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

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;
import sample.interfaces.PlayList;

public class Music extends Pane implements PlayList{
    private Media music;
    private String mediaPath;
    //private MediaPlayer mediaPlayer;
    SimpleObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    @FXML
    Label mediaTitle;
    @FXML
    Label performer;
    @FXML
    Label length;
    private int durationOfMusic=0;
    private Slider time,volumeAdjuster;
    private ImageView playAndPause;
    private Label nowPlayed,duration,volume;
    private SimpleStringProperty durationProperty = new SimpleStringProperty("");
    private MediaList mediaList;
    private Spectrum spectrum = new Spectrum();

    public Media getMusic() {
        return music;
    }

    public int getDurationOfMusic() {
        return durationOfMusic;
    }

    public void setDurationOfMusic(int durationOfMusic) {
        this.durationOfMusic = durationOfMusic;
    }

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
        mediaPlayer.set(new MediaPlayer(music));
        music.getMetadata().addListener((MapChangeListener<String,Object>) change ->{
            if (change.wasAdded()) { System.out.println(change.getKey() + " : " + change.getValueAdded()); }
            if(change.wasAdded()){
                if ("artist".equals(change.getKey())) {
                    artist.setValue(change.getValueAdded().toString());
                } else if ("title".equals(change.getKey())) {
                    title.setValue(change.getValueAdded().toString());
                }
            }
            if(artist.get().equals("")){
                String name = mediaPath.substring(mediaPath.lastIndexOf("/")+1,mediaPath.length());
                title.set(name.replace("%20"," "));
            }
        });



        this.setOnMouseClicked(event -> {
            if(event.getClickCount()==2){
                for(Music m : playListOfMusic){
                    isPlayed(m,0);
                }
                playThis();

            }
        });
        if(mediaPlayer.isNotNull().getValue())
        mediaPlayer.get().dispose();
        mediaPlayer.set(new MediaPlayer(music));

    }

    public void playThis() {
        double volume = 0;
        if(mediaPlayer.isNotNull().getValue()){
            volume=mediaPlayer.get().getVolume();
            mediaPlayer.get().dispose();
        }
        mediaList.setCurrentMusic(this);
        isPlayed(this,1);
        mediaPlayer.set(new MediaPlayer(music));
        bindControlls(this);
        volumeAdjuster.setValue(volume*100);
        mediaPlayer.get().setVolume(volume);
        nowPlayed.setText(mediaTitle.getText());
        mediaPlayer.get().play();
        spectrum.startSpectrumChart();
        mediaPlayer.get().setOnEndOfMedia(() ->{
                    isPlayed(this,0);
                    nextMedia();
                }
                );
    }

    public void passReferences(Slider time, Slider volumeAdjuster, ImageView playAndPause, Label nowPlayed,Label duration,Label volume,MediaList mediaList, Spectrum spectrum){
        this.nowPlayed=nowPlayed;
        this.time = time;
        this.volumeAdjuster = volumeAdjuster;
        this.playAndPause = playAndPause;
        this.duration=duration;
        this.volume=volume;
        this.mediaList = mediaList;
        this.spectrum = spectrum;
        bindControlls(this);
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

    public void isPlayed(Music m,int switcher){
        if(switcher==0){
            m.setStyle("-fx-background-color: #cccccc;");
        }
        else if(switcher==1){
            m.setStyle("-fx-background-color: #add8e6 ;");
        }
    }
    public void nextMedia(){
        double volume2 = 0;

        if((playListOfMusic.indexOf(this)+1)<playListOfMusic.size()){
            if(mediaPlayer.isNotNull().getValue()){
                volume2=mediaPlayer.get().getVolume();
                mediaPlayer.get().dispose();
            }
            Music nextMedia = playListOfMusic.get(playListOfMusic.indexOf(this)+1);
            isPlayed(mediaList.getCurrentMusic(),0);
            mediaList.setCurrentMusic(nextMedia);
            mediaPlayer.set(new MediaPlayer(nextMedia.music));
            // nowPlayed.setText(nextMedia.getMediaPath().substring(nextMedia.getMediaPath().lastIndexOf("/")+1));
            nowPlayed.setText(nextMedia.getMediaTitle().getText());
            bindControlls(nextMedia);
            volumeAdjuster.setValue(volume2*100);
            mediaPlayer.get().setVolume(volume2);
            //nowPlayed.setText(mediaTitle.getText());
            mediaPlayer.get().play();
            spectrum.startSpectrumChart();
            isPlayed(nextMedia,1);

        }

    }

    public void prevMedia(){
        double volume2 = 0;

        if((playListOfMusic.indexOf(this)-1)>=0){
            if(mediaPlayer.isNotNull().getValue()){
                volume2=mediaPlayer.get().getVolume();
                mediaPlayer.get().dispose();
            }
            Music nextMedia = playListOfMusic.get(playListOfMusic.indexOf(this)-1);
            isPlayed(mediaList.getCurrentMusic(),0);
            mediaList.setCurrentMusic(nextMedia);
            mediaPlayer.set(new MediaPlayer(nextMedia.music));
            // nowPlayed.setText(nextMedia.getMediaPath().substring(nextMedia.getMediaPath().lastIndexOf("/")+1));
            nowPlayed.setText(nextMedia.getMediaTitle().getText());
            bindControlls(nextMedia);
            volumeAdjuster.setValue(volume2*100);
            mediaPlayer.get().setVolume(volume2);
            //nowPlayed.setText(mediaTitle.getText());
            mediaPlayer.get().play();
            spectrum.startSpectrumChart();
            isPlayed(nextMedia,1);

        }

    }

    private void bindControlls(Music m){
        volumeAdjuster.setValue(mediaPlayer.get().getVolume()*100);
        volumeAdjuster.valueProperty().addListener(observable -> {
            mediaPlayer.get().setVolume(volumeAdjuster.getValue()/100);
            volume.setText((int)volumeAdjuster.getValue()+"%");
        });

        mediaPlayer.get().statusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(MediaPlayer.Status.READY)){
                m.durationOfMusic=(int)mediaPlayer.get().getMedia().getDuration().toSeconds();
                time.setMax(mediaPlayer.get().getMedia().getDuration().toSeconds());

                    int min = (int) (mediaPlayer.get().getMedia().getDuration().toSeconds()/60);
                    int sec = (int) (mediaPlayer.get().getMedia().getDuration().toSeconds()%60);
                    m.length.setText(formatter.format(min)+":"+formatter.format(sec));


            }
            if(newValue.equals(MediaPlayer.Status.PLAYING)){
                playAndPause.setImage(pause);
            }
            else if(newValue.equals(MediaPlayer.Status.PAUSED)){
                playAndPause.setImage(play);
            }
        });

        mediaPlayer.get().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            m.time.setValue(newValue.toSeconds());
            //int min = (int) (mediaPlayer.get().getMedia().getDuration().toSeconds()/60);
            //int sec = (int) (mediaPlayer.get().getMedia().getDuration().toSeconds()%60);
            //length.setText(min+":"+sec);
            int currentmin = (int) newValue.toSeconds()/60;
            int currentsec = (int) newValue.toSeconds()%60;
            //length.setText(min+":"+sec);
            m.durationProperty.set(formatter.format(currentmin)+":"+formatter.format(currentsec) +" / "+m.length.getText());
            duration.textProperty().bind(m.durationProperty);
        });
        time.setOnMousePressed(event -> {
            mediaPlayer.get().seek(Duration.seconds(time.getValue()));
        });
        time.setOnMouseDragged(event -> {
            mediaPlayer.get().seek(Duration.seconds(time.getValue()));
        });

    }
}