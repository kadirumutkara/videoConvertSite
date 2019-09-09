package com.umutkara.common;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.umutkara.mapper.VideoFormat;
import com.umutkara.mapper.VideoThumbnail;

@ManagedBean(name = "videoDefinitionView")
@SessionScoped
public class VideoDefinitionView {

	public String title;
	public String picture_url;
	public String url;
	public String selectedFormatid;
	public String selectedAudioid;
	public String formatType;
	public ArrayList<VideoFormatListView> formats;
	public ArrayList<VideoThumbnail> thumbnails;
	public ArrayList<AudioFormatListView> audioFormats;

	public ArrayList<AudioFormatListView> getAudioFormats() {
		return audioFormats;
	}

	public void setAudioFormats(ArrayList<AudioFormatListView> audioFormats) {
		this.audioFormats = audioFormats;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPicture_url() {
		return picture_url;
	}

	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<VideoThumbnail> getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(ArrayList<VideoThumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}

	public ArrayList<VideoFormatListView> getFormats() {
		return formats;
	}

	public void setFormats(ArrayList<VideoFormatListView> formats) {
		this.formats = formats;
	}

	public String getSelectedFormatid() {
		return selectedFormatid;
	}

	public void setSelectedFormatid(String selectedFormatid) {
		this.selectedFormatid = selectedFormatid;
	}

	public String getSelectedAudioid() {
		return selectedAudioid;
	}

	public void setSelectedAudioid(String selectedAudioid) {
		this.selectedAudioid = selectedAudioid;
	}

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}
	

}
