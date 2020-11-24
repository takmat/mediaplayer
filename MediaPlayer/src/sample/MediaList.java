package sample;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.text.DecimalFormat;

public class MediaList implements PlayList {
    private Music currentMusic;

    private Slider time,volumeAdjuster;
    private ImageView playAndPause;
    private Label nowPlayed,duration,volume;
    private SimpleStringProperty durationProperty = new SimpleStringProperty("");
    MediaPlayer mediaPlayer ;

    public MediaList(Slider time, Slider volumeAdjuster, Label nowPlayed, Label duration, Label volume, MediaPlayer mediaPlayer) {
        this.time = time;
        this.volumeAdjuster = volumeAdjuster;
        this.nowPlayed = nowPlayed;
        this.duration = duration;
        this.volume = volume;
        this.mediaPlayer = mediaPlayer;
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
    private void bindControlls(Music m){
        DecimalFormat formatter = new DecimalFormat("00");
        volumeAdjuster.setValue(mediaPlayer.getVolume()*100);
        volumeAdjuster.valueProperty().addListener(observable -> {
            mediaPlayer.setVolume(volumeAdjuster.getValue()/100);
            volume.setText((int)volumeAdjuster.getValue()+"%");
        });

        mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(MediaPlayer.Status.READY)){
                time.setMax(mediaPlayer.getMedia().getDuration().toSeconds());

                int min = (int) (mediaPlayer.getMedia().getDuration().toSeconds()/60);
                int sec = (int) (mediaPlayer.getMedia().getDuration().toSeconds()%60);
                m.length.setText(formatter.format(min)+":"+formatter.format(sec));


            }
            if(newValue.equals(MediaPlayer.Status.PLAYING)){
                playAndPause.setImage(pause);
            }
            else if(newValue.equals(MediaPlayer.Status.PAUSED)){
                playAndPause.setImage(play);
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
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
            mediaPlayer.seek(Duration.seconds(time.getValue()));
        });
        time.setOnMouseDragged(event -> {
            mediaPlayer.seek(Duration.seconds(time.getValue()));
        });

    }
}
