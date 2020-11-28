package sample.interfaces;

import java.awt.peer.PanelPeer;
import sample.entity.Music;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.css.Styleable;
import javafx.scene.Node;

public interface PlayList{
    ArrayList<String> playList = new ArrayList<>();
    ArrayList<Music> playListOfMusic = new ArrayList<>();
    Image play = new Image("sample/pictures/play.png");
    Image pause = new Image("sample/pictures/pause.png");
    Image prev = new Image("sample/pictures/prev.png");
    Image next = new Image("sample/pictures/next.png");
    Image stop = new Image("sample/pictures/stop.png");
    ImageView prevButtonImage = new ImageView(prev);
    ImageView nextButtonImage = new ImageView(next);
    ImageView stopButtonImage = new ImageView(stop);
    DecimalFormat formatter = new DecimalFormat("00");
}
