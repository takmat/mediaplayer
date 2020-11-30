package sample.entity;

import com.sun.javafx.charts.Legend;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sample.interfaces.PlayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

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
    CategoryAxis xAxis ;//= new CategoryAxis(); 
    @FXML
    NumberAxis yAxis;// = new NumberAxis();
    @FXML
    StackedBarChart<String, Number> statisticsChart;// = new StackedBarChart<>(xAxis, yAxis);
    
    public void initialize() {
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
                Pattern datePattern = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
                Pattern artistPattern = Pattern.compile("(artist=.+?[?=\\|])");
                Pattern titlePattern = Pattern.compile("(title=.+?[?=\\|])");
                
                Map<String, Integer> dateMap;
                Map<LogMusic, Integer> musicMap = new HashMap<>();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate now = LocalDate.now();
                dateMap = Arrays.asList(DayOfWeek.values()).stream().map(now::with).map( d -> d.format(dtf)).collect(Collectors.toMap(str -> str, i -> 0));
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
                    
                    Matcher m3 = datePattern.matcher(line);
                    Matcher artistMatcher = artistPattern.matcher(line);
                    Matcher titleMathcer = titlePattern.matcher(line);
                    String date = null;
                    if(m3.find()){
                        date = m3.group();
                    }
                    Matcher m2 = length.matcher(line);
                    if(m2.find()){

                        String[] temp1 = m2.group(1).split("=");
                        String[] temp2 = temp1[1].split(":");
                        min = Integer.parseInt(temp2[0])*60;
                        lengthOfMusic = min+Integer.parseInt(temp2[1]);
                        if(lengthOfMusic-sec==0){
                            sumOfListenedMusic++;
                            if(date != null){
                                Integer i = dateMap.get(date)+1;
                                dateMap.replace(date, i);
                            }
                            if(artistMatcher.find() && titleMathcer.find()){
                                LogMusic music = new LogMusic(artistMatcher.group(),titleMathcer.group());
                                if(musicMap.containsKey(music)){
                                   
                                    musicMap.replace(music, musicMap.get(music)+1);
                                }else{
                                    musicMap.put(music, 0);
                                    
                                }
                            }
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
                loadDataIntoChart(dateMap);
                loadMostListened(musicMap);
                tillTheVeryEnd.setText(String.valueOf(sumOfListenedMusic));
                sumDuration.setText(duration);
                sumMusics.setText(String.valueOf(musics));
            }
            catch (Exception ex){

            }
        }
    }
    
    private void loadDataIntoChart(Map<String,Integer> dateMap){
        for(String dateString : dateMap.keySet().stream().sorted().collect(toList())){
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.getData().add(new XYChart.Data<>(dateString, dateMap.get(dateString)));
            statisticsChart.getData().add(series1);
        }
        Legend legend = (Legend) statisticsChart.lookup(".chart-legend");
        legend.getItems().clear();
        /*yAxis.setAutoRanging(false);
        yAxis.setLowerBound(20);
        yAxis.setUpperBound(56);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickLength(0);
        yAxis.setMinorTickCount(0);
        yAxis.setMinorTickVisible(false);*/
        
    }
    private void loadMostListened(Map<LogMusic, Integer> musicMap){
        for(LogMusic m : musicMap.keySet()){
            System.out.println(m + " " + musicMap.get(m));
        }
    }
    
    private class LogMusic{
        private String artist;
        private String title;

        public LogMusic(String artist, String title) {
            this.artist = artist;
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        
        
    }
}
