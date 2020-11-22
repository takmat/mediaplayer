package sample;

import javafx.scene.image.Image;

import java.util.ArrayList;

public interface PlayList {
     ArrayList<String> playList = new ArrayList<>();
    ArrayList<Music> playListOfMusic = new ArrayList<>();
    final Image play = new Image("sample/play.png");
    final Image pause = new Image("sample/pause.png");
}
