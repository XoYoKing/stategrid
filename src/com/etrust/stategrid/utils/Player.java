package com.etrust.stategrid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class Player {
	public MediaPlayer mediaPlayer;

	public Player() {
		try {
			mediaPlayer = new MediaPlayer();
//			mediaPlayer.setAudioStreamType(AudioManager.m);
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
		}
	}

	public void play() {
		mediaPlayer.start();
	}

	public boolean isPlaying() {
		if (mediaPlayer == null)
			return false;
		boolean b = false;
		try {
			b = mediaPlayer.isPlaying();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	public void playUrl(String videoUrl) {
		try {
			if (mediaPlayer != null) {  
				mediaPlayer.stop();  
            }  
			mediaPlayer.reset();
			File file = new File(videoUrl);
			FileInputStream fis = new FileInputStream(file);
			mediaPlayer.setDataSource(fis.getFD());
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void release(){
		try {
			if (mediaPlayer != null) {
				mediaPlayer.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
