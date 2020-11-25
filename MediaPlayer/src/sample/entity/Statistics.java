package sample.entity;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Statistics implements PlayList{
    @FXML
    Label playlistLength;
    @FXML
    Label playlistDuration;
    @FXML
    Label playlistAVGDuration;

    @FXML
    private void initialize() {
        int sum=0;
        if(playListOfMusic.size()>0) {
            for (Music m : playListOfMusic) {
                sum = sum + m.getDurationOfMusic();
            }
            int hour, min, sec;
            String duration = "";
            String avg = "";
            hour = sum / 3600;
            min = sum / 60;
            sec = sum % 60;
            duration = duration + formatter.format(hour) + ":" + formatter.format(min) + ":" + formatter.format(sec);
            playlistDuration.setText(duration);

            playlistLength.setText(String.valueOf(playListOfMusic.size()));
            int AVG = sum/playListOfMusic.size();
            avg = avg + formatter.format(AVG/3600) + ":" + formatter.format(AVG/60) + ":" + formatter.format(AVG%60);
            playlistAVGDuration.setText(avg);
        }

    }
}
