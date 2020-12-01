package sample.entity;

import com.sun.javafx.charts.Legend;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Pair;
import sample.interfaces.PlayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
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
    @FXML 
    PieChart favoritePieChart;
    @FXML
    Label titleLabel, artistLabel;
    @FXML
    DatePicker datePicker;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                
    
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
        LocalDate now = LocalDate.now();
        datePicker.setValue(now);
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> loadStatistics(newValue));

        loadStatistics(now);
        

    }
    private void loadStatistics(LocalDate now){
        File f = new File("./logfile.log");
        System.out.println(f.exists());
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
                
                Map<LocalDate, Integer> dateMap;
                ArrayList<LogMusic> musicList = new ArrayList<>();
                
                dateMap = Arrays.asList(DayOfWeek.values()).stream().map(now::with).collect(Collectors.toMap( d->d, i -> 0));
                //dateMap.keySet().forEach( k-> System.out.println(k));
                int maxListened=1;

                while ((line = br.readLine()) != null){
                    //System.out.println(line);
                    Matcher m = playedTill.matcher(line);
                    if(m.find()){
                        //System.out.println("lejátszás");
                        String[] temp1 = m.group(1).split("=");
                        String[] temp2 = temp1[1].split(":");
                        min = Integer.parseInt(temp2[0])*60;
                        sec = min+Integer.parseInt(temp2[1]);
                        sum=sum+sec;
                        musics++;
                    }


                    Matcher m2 = length.matcher(line);
                    Matcher artistMatcher = artistPattern.matcher(line);
                    Matcher titleMathcer = titlePattern.matcher(line);
                    if(m2.find()){
                        //System.out.println("végig");
                        String[] temp1 = m2.group(1).split("=");
                        String[] temp2 = temp1[1].split(":");
                        min = Integer.parseInt(temp2[0])*60;
                        lengthOfMusic = min+Integer.parseInt(temp2[1]);
                        if(lengthOfMusic-sec==0){
                            sumOfListenedMusic++;
                            Matcher m3 = datePattern.matcher(line);
                            LocalDate date;
                            
                            
                            if(m3.find()){
                                //System.out.println("dátum");
                                date = LocalDate.parse(m3.group(),dtf);
                                if(date != null && dateMap.containsKey(date)){
                                    if(dateMap.containsKey(date)){
                                        int i = dateMap.get(date)+1;
                                        
                                        if(i>maxListened){
                                            maxListened=i;
                                        }
                                        dateMap.replace(date, i);
                                    }
                                    if(artistMatcher.find() && titleMathcer.find()){
                                        LogMusic music = new LogMusic(artistMatcher.group().replace("|", "").replace("artist=", "").trim(),
                                                titleMathcer.group().replace("title=", "").replace("|","").trim());
                                        musicList.add(music);
                                    }
                                    else{
                                        dateMap.put(date,1);
                                    }

                                }
                            }
                        }

                    }



                    
                    
                }
                //musicList.forEach( t -> System.out.println(t.getArtist() + " " + t.getTitle()));
                int hour = sum/3600;
                min = sum/60-(hour*60);
                sec = sum%60;
                String duration=formatter.format(hour)+":"+formatter.format(min)+":"+formatter.format(sec);
                //musicMap.keySet().stream().forEach(k-> System.out.println(k + " " + musicMap.get(k)));
                Map<String, Long> counterMap =  musicList.stream()
                        .filter( k -> !"".equals(k.getArtist()))
                        .map(k -> k.getArtist())
                        .collect(Collectors.groupingBy( e -> e, Collectors.counting()));
                musicList.forEach( t -> System.out.println(t.getArtist() + " " + t.getTitle()));

                //System.out.println("összes hallgatott idő "+duration);
                favoriteArtist(counterMap, musicList);
                tillTheVeryEnd.setText(String.valueOf(sumOfListenedMusic));
                sumDuration.setText(duration);
                sumMusics.setText(String.valueOf(musics));
                loadDataIntoChart(dateMap,maxListened);
                loadPieChart(counterMap);

            }
            catch (Exception ex){

            }
        }
    }
    
    private void loadDataIntoChart(Map<LocalDate,Integer> dateMap,int maxListened){
        statisticsChart.getData().clear();
        for(LocalDate dateString : dateMap.keySet().stream().sorted().collect(toList())){
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.getData().add(new XYChart.Data<>(dateString.format(dtf), dateMap.get(dateString)));
            statisticsChart.getData().add(series1);
        }
        Legend legend = (Legend) statisticsChart.lookup(".chart-legend");
        legend.getItems().clear();
           
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(maxListened);
        yAxis.setTickUnit(1);
        yAxis.setMinorTickLength(0);
        yAxis.setMinorTickCount(0);
        
    }
    private void loadPieChart(Map<String, Long> counterMap){
        List<PieChart.Data> pieChartData = counterMap.keySet().stream()
                .sorted((k1,k2) -> counterMap.get(k2).compareTo(counterMap.get(k1)))
                .limit(3)
                .map( k-> new PieChart.Data(k + " " + counterMap.get(k) + " db", counterMap.get(k))).collect(toList());
        favoritePieChart.setData(FXCollections.observableArrayList(pieChartData));
        Legend legend = (Legend) favoritePieChart.lookup(".chart-legend");
        legend.getItems().clear();
        for(String string : counterMap.keySet()){
            System.out.println(string+" "+ counterMap.get(string));
        }
        //favoritePieChart.labelsVisibleProperty().setValue(true);
    }
    private void favoriteArtist(Map<String, Long> counterMap, ArrayList<LogMusic> musicList){
        String fav = counterMap.keySet().stream().sorted((k1,k2) -> counterMap.get(k2).compareTo(counterMap.get(k1))).limit(1).collect(Collectors.joining());
        artistLabel.setText(fav); 
        Map<String,Long> favMusicMap = musicList.stream().filter(k ->k.getArtist().equals(fav)).map( k-> k.getTitle()).collect(Collectors.groupingBy( e->e, Collectors.counting()));
        String music = favMusicMap.keySet().stream().sorted( (k1,k2) -> favMusicMap.get(k2).compareTo(favMusicMap.get(k1))).limit(1).collect(Collectors.joining());
        titleLabel.setText( music);
                
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

        @Override
        public int hashCode() {
            int hash = 3;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final LogMusic other = (LogMusic) obj;
            if (!Objects.equals(this.artist, other.artist)) {
                return false;
            }
            if (!Objects.equals(this.title, other.title)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "LogMusic{" + "artist=" + artist + ", title=" + title + '}';
        }
        
        
        
    }
}
