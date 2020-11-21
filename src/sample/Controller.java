package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    Button MediaPlayButton;
    final Image play=new Image("sample/play.png");
    final Image pause=new Image("sample/pause.png");
    private ImageView playAndPause;
    @FXML
    MediaView mediaView;
    private ArrayList<File> playlist;

    @FXML
    private void initialize(){
        System.out.println("start");
        playAndPause = new ImageView(play);
        playAndPause.setFitWidth(75);
        playAndPause.setFitHeight(75);
        MediaPlayButton.setGraphic(playAndPause);

    }
    private void play(){
        playAndPause.setImage(pause);
    }
    private void pause(){
        playAndPause.setImage(play);
    }
    private void createPlaylist(){
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Képek","*.mp3","*.jpeg","*.jpg","*.bmp")
        );
        List<File> list =
                fileChooser.showOpenMultipleDialog(stage);
        if(playlist==null){
            playlist=new ArrayList<>();
        }
        for(File f : list){

        }



    }
}
