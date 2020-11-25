/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

/**
 *
 * @author tlehe
 */
public class Spectrum {
    
    @FXML
    CategoryAxis xAxis;
    @FXML
    NumberAxis yAxis;
    @FXML
    BarChart<String,Number>  bc;
    SimpleObjectProperty<MediaPlayer> mediaPlayer;
    private XYChart.Series<String,Number> series1 = new XYChart.Series<> ();
    private XYChart.Series<String,Number> series2 = new XYChart.Series<> ();
    
    public void startSpectrumChart(){
        XYChart.Data[] series1Data = initXYChart(series1);
        XYChart.Data[] series2Data = initXYChart(series2);
        bc.getData().add(series1);
        bc.getData().add(series2);
        mediaPlayer.get().setAudioSpectrumListener((double d, double d1, float[] magnitudes , float[] phases) -> {
            for(int i=0;i<magnitudes.length;i++){
                series1Data[i].setYValue((magnitudes[i]+60)); //Top Series
                series2Data[i].setYValue(-(magnitudes[i]+60));//Bottom series
            }

        });
    }

    public void passReferences(CategoryAxis xAxis, NumberAxis yAxis, BarChart<String,Number> bc,  SimpleObjectProperty<MediaPlayer> mediaPlayer) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.bc = bc;
        this.mediaPlayer = mediaPlayer;
        bc.setLegendVisible(false);
        bc.setAnimated(false);
        bc.setBarGap(0);
        bc.setCategoryGap(0);
        bc.setVerticalGridLinesVisible(false);
        bc.setHorizontalGridLinesVisible(false);
        bc.setHorizontalZeroLineVisible(false);
        bc.setVerticalZeroLineVisible(false);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis,null,"dB"));
        xAxis.setTickLabelsVisible(false);
        yAxis.setTickLabelsVisible(false);
        xAxis.setOpacity(0);
        yAxis.setOpacity(0);
        
    }
    private XYChart.Data[] initXYChart(XYChart.Series<String,Number> series){
        XYChart.Data[] seriesData = new XYChart.Data[128];
        for (int i=0; i<seriesData.length; i++) {
            seriesData[i] = new XYChart.Data<>( Integer.toString(i+1),50);
            series.getData().add(seriesData[i]);
        }
        return seriesData;
    }
    public void stopSpectrumChart(){
        bc.getData().clear();
    }
    
}
