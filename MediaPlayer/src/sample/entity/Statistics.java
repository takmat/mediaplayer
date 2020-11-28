package sample.entity;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.interfaces.PlayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statistics implements PlayList {
    @FXML
    Label playlistLength;
    @FXML
    Label playlistDuration;
    @FXML
    Label playlistAVGDuration;
    @FXML
    Label sumMusics;
    @FXML
    Label sumDuration;
    @FXML
    Label tillTheVeryEnd;

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
        loadStatistics();

    }
    private void loadStatistics(){
        File f = new File("./logfile.log");
        if(f.exists()){
            try (BufferedReader br = new BufferedReader(new FileReader(f))){
                String line;
                int sum = 0;
                int min =0;
                int sec =0;
                int lengthOfMusic=0;
                int sumOfListenedMusic=0;
                int musics=0;
                Pattern playedTill = Pattern.compile("(playedTill=\\d\\d:\\d\\d)");
                Pattern length = Pattern.compile("(length=\\d\\d:\\d\\d)");
                while ((line = br.readLine()) != null){
                    Matcher m = playedTill.matcher(line);
                    if(m.find()){

                        String[] temp1 = m.group(1).split("=");
                        String[] temp2 = temp1[1].split(":");
                        min = Integer.parseInt(temp2[0])*60;
                        sec = min+Integer.parseInt(temp2[1]);
                        sum=sum+sec;
                        musics++;
                    }
                    Matcher m2 = length.matcher(line);
                    if(m2.find()){

                        String[] temp1 = m2.group(1).split("=");
                        String[] temp2 = temp1[1].split(":");
                        min = Integer.parseInt(temp2[0])*60;
                        lengthOfMusic = min+Integer.parseInt(temp2[1]);
                        if(lengthOfMusic-sec==0){
                            sumOfListenedMusic++;
                        }
                    }
                }
                int hour = sum/3600;
                min = sum/60-(hour*60);
                sec = sum%60;
                System.out.println(sum);
                System.out.println("min: "+min);
                String duration=formatter.format(hour)+":"+formatter.format(min)+":"+formatter.format(sec);
                System.out.println("összes hallgatott idő "+duration);

                tillTheVeryEnd.setText(String.valueOf(sumOfListenedMusic));
                sumDuration.setText(duration);
                sumMusics.setText(String.valueOf(musics));
            }
            catch (Exception ex){

            }
        }
    }
}
