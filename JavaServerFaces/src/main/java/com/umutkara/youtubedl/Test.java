package com.umutkara.youtubedl;

import java.util.ArrayList;

import com.umutkara.mapper.VideoInfo;
import com.umutkara.mapper.VideoThumbnail;



public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws YoutubeDLException {
		
		String videoUrl = "https://www.youtube.com/watch?v=BaW_jenozKc";

		// Destination directory
		String directory ="C:/Users/kadir.kara/Desktop/vk/path";
		

		// Build request
	//	YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
		

	
		VideoInfo response = YoutubeDL.getVideoInfo(videoUrl);
		
		// Response
		String stdOut = response.title;
		ArrayList<VideoThumbnail> stdOutlist = response.thumbnails;
	}

}
