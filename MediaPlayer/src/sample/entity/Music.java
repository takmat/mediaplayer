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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import sample.interfaces.MediaListInterface;
import sample.interfaces.PlayList;
import sample.interfaces.SpectrumInterface;
import sample.logging.DefaultLogger;

public class Music extends Pane implements PlayList{
    private Media music;
    private String mediaPath;
    SimpleObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    @FXML
    Label mediaTitle;
    @FXML
    Label performer;
    @FXML
    Label genre;
    @FXML
    Label year;
    @FXML
    Label album;
    @FXML
    Label length;
    @FXML
    Button nextButton;
    @FXML
    Button previousButton;
    @FXML
    HBox genreAndYearHBox;
    @FXML
    VBox musicMain;
    private int durationOfMusic=0;
    private Slider time,volumeAdjuster,playSpeed;
    private ImageView playAndPause;
    private Label nowPlayed,duration,volume;
    private SimpleStringProperty durationProperty = new SimpleStringProperty("");
    private MediaListInterface mediaList;
    private SpectrumInterface spectrum = new Spectrum();
    private DefaultLogger logger = DefaultLogger.getInstance();

    
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

    private StringProperty artist=new SimpleStringProperty(""), title=new SimpleStringProperty(""),
            gen=new SimpleStringProperty(""),YEAR=new SimpleStringProperty(""),ALBUM=new SimpleStringProperty("");

    public Music(String mediaPath, SimpleObjectProperty<MediaPlayer> mediaPlayer) {
        loadUI();
        this.mediaPath = mediaPath;
        this.mediaPlayer = mediaPlayer;
        musicMain.getChildren().remove(genreAndYearHBox);
        music = new Media(mediaPath);
        performer.textProperty().bind(artist);
        mediaTitle.textProperty().bind(title);
        genre.textProperty().bind(gen);
        year.textProperty().bind(YEAR);
        album.textProperty().bind(ALBUM);
        mediaPlayer.set(new MediaPlayer(music));
        music.getMetadata().addListener((MapChangeListener<String,Object>) change ->{
            if (change.wasAdded()) { System.out.println(change.getKey() + " : " + change.getValueAdded()); }
            if(change.wasAdded()){
                if ("artist".equals(change.getKey())) {
                    artist.setValue(change.getValueAdded().toString());
                } else if ("title".equals(change.getKey())) {
                    title.setValue(change.getValueAdded().toString());
                }
                 else if ("album".equals(change.getKey())) {
                    ALBUM.setValue(change.getValueAdded().toString());
                }
                else if ("genre".equals(change.getKey())) {
                    gen.setValue(change.getValueAdded().toString());
                    if(!gen.get().equals("")){
                        if(!musicMain.getChildren().contains(genreAndYearHBox))
                        musicMain.getChildren().add(genreAndYearHBox);

                    }
                }
                else if ("year".equals(change.getKey())) {
                    YEAR.setValue(change.getValueAdded().toString());
                    if(!YEAR.get().equals("") ){
                        if(!musicMain.getChildren().contains(genreAndYearHBox))
                            musicMain.getChildren().add(genreAndYearHBox);
                    }
                }

            }
            if(artist.get().equals("")){
                String[] nameArray = title.get().split("-");
                if(nameArray.length == 2){
                    artist.set(nameArray[0].trim());
                    title.set(nameArray[1].replace(".mp3", "").trim());
                }else{
                    String name = mediaPath.substring(mediaPath.lastIndexOf("/")+1,mediaPath.length());
                    title.set(name.replace("%20"," "));
                }
            }
        });
        SimpleObjectProperty<Music> draggingTab = new SimpleObjectProperty<>();


        this.setOnDragOver( (DragEvent event) -> {
            Dragboard d = event.getDragboard();
            if(d.hasString() ){
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
        this.setOnDragDropped((DragEvent event) -> {
            System.out.println("droped");
            Dragboard d = event.getDragboard();
            boolean success = false;
            if(d.hasString()){
                Pane parent = (Pane) this.getParent();
                Object source = event.getGestureSource();
                int sourceIndex = parent.getChildren().indexOf(source);
                int targetIndex = parent.getChildren().indexOf(this);
                System.out.println("source " + sourceIndex);
                System.out.println("target " + targetIndex);
                List<Node> nodes = new ArrayList<>(parent.getChildren());
                nodes.remove((Music) source);
                nodes.add(targetIndex, (Music)source);
                playListOfMusic.remove((Music)source);
                playListOfMusic.add(targetIndex, (Music)source);
                parent.getChildren().clear();
                parent.getChildren().addAll(nodes);
                setButtonsToMusicList();
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        this.setOnDragDetected((MouseEvent event ) -> {
            System.out.println("drag");
            Dragboard dragboard = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString("Music");
            dragboard.setContent(clipboardContent);
            draggingTab.set(this);
            event.consume();
            
        }); 
        
        this.setOnMouseClicked(event -> {
            if(event.getClickCount()==2){
                if(mediaList.getCurrentMusic() != null){
                    System.out.println(mediaList.getCurrentMusic().toString());
                    //logger.logInfo(mediaList.getCurrentMusic());
                }
                for(Music m : playListOfMusic){
                    isPlayed(m,0);
                }
                playThis();
                setButtonsToMusicList();
            }
        });
        if(mediaPlayer.isNotNull().getValue())
        mediaPlayer.get().dispose();
        mediaPlayer.set(new MediaPlayer(music));

    }
    public void setButtonsToMusicList(){
        if(playListOfMusic.size() <= 1){
            nextButton.setDisable(true);
            previousButton.setDisable(true);
        }else if(playListOfMusic.indexOf(mediaList.getCurrentMusic()) == 0){
            nextButton.setDisable(false);
            previousButton.setDisable(true);
            
        }else if (playListOfMusic.indexOf(mediaList.getCurrentMusic()) == playListOfMusic.size() - 1){
            nextButton.setDisable(true);
            previousButton.setDisable(false);
        }else{
            nextButton.setDisable(false);
            previousButton.setDisable(false);
        }
    }
    public void playThis() {
        double volume = 0;
        if(mediaList.getCurrentMusic() == null || !mediaList.getCurrentMusic().equals(this)){
            if(mediaList.getCurrentMusic() != null){
                logger.logInfo(mediaList.getCurrentMusic());
            }
            if(mediaPlayer.isNotNull().getValue()){
                volume=mediaPlayer.get().getVolume();
                mediaPlayer.get().dispose();
            }
            if(volume==0)
                volume=mediaList.getCurrentVolume();

            mediaList.setCurrentMusic(this);
            isPlayed(this,1);
            mediaPlayer.set(new MediaPlayer(music));
            bindControlls(this);
            volumeAdjuster.setValue(volume*100);
            mediaPlayer.get().setVolume(volume);
            nowPlayed.setText(mediaTitle.getText());
            mediaList.setCurrentVolume(mediaPlayer.get().getVolume());
        }
        mediaPlayer.get().setOnEndOfMedia(() ->{
                isPlayed(this,0);
                nextMedia();
            });
        spectrum.startSpectrumChart();
        mediaPlayer.get().play();
    }

    public void passReferences(Slider time, Slider volumeAdjuster, 
            ImageView playAndPause, Label nowPlayed,Label duration,Label volume,MediaListInterface mediaList, 
            SpectrumInterface spectrum, Button nextMedia, Button previousMedia,Slider playSpeed){
        this.nowPlayed=nowPlayed;
        this.time = time;
        this.volumeAdjuster = volumeAdjuster;
        this.playAndPause = playAndPause;
        this.duration=duration;
        this.volume=volume;
        this.mediaList = mediaList;
        this.spectrum = spectrum;
        this.nextButton = nextMedia;
        this.previousButton = previousMedia;
        this.playSpeed = playSpeed;
        bindControlls(this);
    }
    private void loadUI(){

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/fxml/music.fxml"));
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
        if(mediaPlayer.isNotNull().getValue()){
            logger.logInfo(this);
        }
        if((playListOfMusic.indexOf(this)+1)<playListOfMusic.size()){
            if(mediaPlayer.isNotNull().getValue()){
                volume2=mediaPlayer.get().getVolume();
                mediaPlayer.get().dispose();
            }
            Music nextMedia = playListOfMusic.get(playListOfMusic.indexOf(this)+1);
            
            isPlayed(mediaList.getCurrentMusic(),0);
            mediaList.setCurrentMusic(nextMedia);
            mediaPlayer.set(new MediaPlayer(nextMedia.music));
            nextMedia.setButtonsToMusicList();
            // nowPlayed.setText(nextMedia.getMediaPath().substring(nextMedia.getMediaPath().lastIndexOf("/")+1));
            nowPlayed.setText(nextMedia.getMediaTitle().getText());
            bindControlls(nextMedia);
            volumeAdjuster.setValue(volume2*100);
            mediaPlayer.get().setVolume(volume2);
            //nowPlayed.setText(mediaTitle.getText());
            
            //mediaPlayer.get().play();
            nextMedia.playThis();
            //spectrum.startSpectrumChart();
            //isPlayed(nextMedia,1);

        }else{
            mediaList.setCurrentMusic(null);
        }

    }

    public void prevMedia(){
        double volume2 = 0;
        if(mediaPlayer.isNotNull().getValue()){
            logger.logInfo(this);
        }
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
            nextMedia.setButtonsToMusicList();
            nextMedia.playThis();
            //spectrum.startSpectrumChart();
            //isPlayed(nextMedia,1);

        }else{
            mediaList.setCurrentMusic(null);
        }

    }

    private void bindControlls(Music m){
        volumeAdjuster.setValue(mediaPlayer.get().getVolume()*100);
        volumeAdjuster.valueProperty().addListener(observable -> {
            mediaPlayer.get().setVolume(volumeAdjuster.getValue()/100);
            volume.setText((int)volumeAdjuster.getValue()+"%");
            mediaList.setCurrentVolume(volumeAdjuster.getValue());
        });
        System.out.println(mediaPlayer.get().getRate());
        playSpeed.valueProperty().addListener((observable,oldValue,newValue) -> {


            mediaPlayer.get().setRate(newValue.doubleValue());

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
            else if(newValue.equals(MediaPlayer.Status.PAUSED) || newValue.equals(MediaPlayer.Status.STOPPED)){
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

    public String getLogOutString() {
        if(mediaPlayer.get().getCurrentTime().toMillis() > 0){
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            return "playedTill=" + df.format(mediaPlayer.get().getCurrentTime().toMillis()) + " | artist=" + artist.get() + " | title=" + title.get() + " | gen=" + gen.get() + " | year=" + YEAR.get() + " | album=" + ALBUM.get() + " | length=" + length.getText() ;
        }
        return null;
    }

    public void pauseThis() {
        mediaPlayer.get().pause();
        spectrum.stopSpectrumChart();
    }

    public void stopThis() {
        mediaPlayer.get().stop();
        spectrum.stopSpectrumChart();
    }

    
    
}
