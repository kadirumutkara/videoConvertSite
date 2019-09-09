package com.umutkara.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "videoFormatListView")
@SessionScoped
public class AudioFormatListView {
	
	public int tbr;
	public int abr;
	public String format;
	public String formatId;
	public String formatNote;
	public String ext;
	public int preference;
	public String vcodec;
	public String acodec;
	public int width;
	public int height;
	public String filesize;
	public int fps;
	public String url;

	public int getTbr() {
		return tbr;
	}

	public void setTbr(int tbr) {
		this.tbr = tbr;
	}

	public int getAbr() {
		return abr;
	}

	public void setAbr(int abr) {
		this.abr = abr;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormatId() {
		return formatId;
	}

	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}

	public String getFormatNote() {
		return formatNote;
	}

	public void setFormatNote(String formatNote) {
		this.formatNote = formatNote;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public int getPreference() {
		return preference;
	}

	public void setPreference(int preference) {
		this.preference = preference;
	}

	public String getVcodec() {
		return vcodec;
	}

	public void setVcodec(String vcodec) {
		this.vcodec = vcodec;
	}

	public String getAcodec() {
		return acodec;
	}

	public void setAcodec(String acodec) {
		this.acodec = acodec;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
