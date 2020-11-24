package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public interface PlayList {
     ArrayList<String> playList = new ArrayList<>();
    ArrayList<Music> playListOfMusic = new ArrayList<>();
    final Image play = new Image("sample/play.png");
    final Image pause = new Image("sample/pause.png");
    final Image prev = new Image("sample/prev.png");
    final Image next = new Image("sample/next.png");
    ImageView prevButtonImage = new ImageView(prev);
    ImageView nextButtonImage = new ImageView(next);

}
