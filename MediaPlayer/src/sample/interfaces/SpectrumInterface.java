/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.interfaces;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author tlehe
 */
public interface SpectrumInterface {
    public void startSpectrumChart();
    public void passReferences(CategoryAxis xAxis, NumberAxis yAxis, BarChart<String,Number> bc,  SimpleObjectProperty<MediaPlayer> mediaPlayer);
    public void initSpectrumChart(CategoryAxis xAxis, NumberAxis yAxis, BarChart<String,Number> bc,  SimpleObjectProperty<MediaPlayer> mediaPlayer);
    public void stopSpectrumChart();
}
