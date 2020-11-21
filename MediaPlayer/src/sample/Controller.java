package sample;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaView;

public class Controller {
    @FXML
    Button MediaPlayButton;
    final Image play = new Image("sample/play.png");
    final Image pause = new Image("sample/pause.png");
    private ImageView playAndPause;
    @FXML
    MediaView mediaView;

    public Controller() {
    }

    @FXML
    private void initialize() {
        System.out.println("start");
        this.playAndPause = new ImageView(this.play);
        this.playAndPause.setFitWidth(75.0D);
        this.playAndPause.setFitHeight(75.0D);
        this.MediaPlayButton.setGraphic(this.playAndPause);
    }

    private void play() {
        this.playAndPause.setImage(this.pause);
    }

    private void pause() {
        this.playAndPause.setImage(this.play);
    }
}
