/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.interfaces;

import sample.entity.Music;

/**
 *
 * @author tlehe
 */
public interface MediaListInterface extends PlayList{
    public void setCurrentMusic(Music currentMusic);
    public Music getCurrentMusic() ;
    public void nextMedia();
    public void prevMedia();
    public void startPlay();
    public void getDurations();
    void setCurrentVolume(double currentVolume);
    double getCurrentVolume();
}
