package sample.entity;


import sample.entity.Music;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.text.DecimalFormat;
import sample.interfaces.PlayList;

public class MediaList implements PlayList {
    private Music currentMusic;

    private Slider time,volumeAdjuster;
    private ImageView playAndPause;
    private Label nowPlayed,duration,volume;
    private SimpleStringProperty durationProperty = new SimpleStringProperty("");
    SimpleObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();

    public MediaList(Slider time, Slider volumeAdjuster, Label nowPlayed, Label duration, Label volume, MediaPlayer mediaPlayer) {
        this.time = time;
        this.volumeAdjuster = volumeAdjuster;
        this.nowPlayed = nowPlayed;
        this.duration = duration;
        this.volume = volume;
        this.mediaPlayer.set(mediaPlayer);
    }

    public void setCurrentMusic(Music currentMusic) {
        this.currentMusic = currentMusic;
    }

    public Music getCurrentMusic() {
        return currentMusic;
    }

    public void nextMedia(){
        currentMusic.nextMedia();
        System.out.println(currentMusic.toString());
        //currentMusic=playListOfMusic.get(playListOfMusic.indexOf(currentMusic)+1);
    }
    public void prevMedia(){
        currentMusic.prevMedia();
        System.out.println(currentMusic.toString());
        //currentMusic=playListOfMusic.get(playListOfMusic.indexOf(currentMusic)-1);
    }
    public void startPlay(){
        playListOfMusic.get(0).playThis();
    }
    public void getDurations(){

        for(Music m : playListOfMusic){
            MediaPlayer mediaPlayer1 = new MediaPlayer(m.getMusic());
            volumeAdjuster.setValue(10);

            mediaPlayer1.setOnReady(() -> {
                m.setDurationOfMusic((int) mediaPlayer1.getMedia().getDuration().toSeconds());
                System.out.println(m.getDurationOfMusic());
                int min = m.getDurationOfMusic() / 60;
                int sec = m.getDurationOfMusic() % 60;
                m.length.setText(formatter.format(min) + ":" + formatter.format(sec));
                mediaPlayer1.dispose();
            });
        }
    }
    private void bindControlls(Music m){
        DecimalFormat formatter = new DecimalFormat("00");
        volumeAdjuster.setValue(mediaPlayer.get().getVolume()*100);
        volumeAdjuster.valueProperty().addListener(observable -> {
            mediaPlayer.get().setVolume(volumeAdjuster.getValue()/100);
            volume.setText((int)volumeAdjuster.getValue()+"%");
        });

        mediaPlayer.get().statusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(MediaPlayer.Status.READY)){
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
            time.setValue(newValue.toSeconds());
            //int min = (int) (mediaPlayer.get().getMedia().getDuration().toSeconds()/60);
            //int sec = (int) (mediaPlayer.get().getMedia().getDuration().toSeconds()%60);
            //length.setText(min+":"+sec);
            int currentmin = (int) newValue.toSeconds()/60;
            int currentsec = (int) newValue.toSeconds()%60;
            //length.setText(min+":"+sec);
            durationProperty.set(formatter.format(currentmin)+":"+formatter.format(currentsec) +" / "+m.length.getText());
            duration.textProperty().bind(durationProperty);
        });
        time.setOnMousePressed(event -> {
            mediaPlayer.get().seek(Duration.seconds(time.getValue()));
        });
        time.setOnMouseDragged(event -> {
            mediaPlayer.get().seek(Duration.seconds(time.getValue()));
        });

    }
}
