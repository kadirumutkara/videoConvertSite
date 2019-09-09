package com.umutkara.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.primefaces.event.SelectEvent;

import com.umutkara.mapper.VideoInfo;
import com.umutkara.youtubedl.YoutubeDL;
import com.umutkara.youtubedl.YoutubeDLException;
import com.umutkara.youtubedl.YoutubeDLRequest;
import com.umutkara.youtubedl.YoutubeDLResponse;

@ManagedBean(name = "downloadController")
@SessionScoped
public class Download implements Serializable {

	private static final long serialVersionUID = 1L;
	@ManagedProperty("#{videoDefinitionView}")
	private static VideoDefinitionView videoDefinitionStruct;

	// @ManagedProperty("#{videoFormatListView}")
	// private static VideoFormatListView videoformatListview;
	//
	//
	// public static VideoFormatListView getVideoformatListview() {
	// if(videoformatListview == null)
	// videoformatListview = new VideoFormatListView();
	// return videoformatListview;
	// }
	//
	// public static void setVideoformatListview(VideoFormatListView
	// videoformatListview) {
	// Download.videoformatListview = videoformatListview;
	// }

	public VideoDefinitionView getVideoDefinitionStruct() {
		if (videoDefinitionStruct == null)
			videoDefinitionStruct = new VideoDefinitionView();
		return videoDefinitionStruct;
	}

	public void setVideoDefinitionStruct(VideoDefinitionView videoDefinitionStruct) {
		this.videoDefinitionStruct = videoDefinitionStruct;
	}

	public static void main(String[] args) throws IOException {

	}

	public static void process() throws IOException {

		String url = videoDefinitionStruct.url;
		String formatid = videoDefinitionStruct.selectedFormatid;
		String formatType = videoDefinitionStruct.formatType;
		System.out.println(formatid);
		System.out.println(url);
		System.out.println(formatType);
		Properties prop = new Properties();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		prop.load(ec.getResourceAsStream("/WEB-INF/app.properties"));
		String download_path = "";
		String[] command = {};

		if (SystemUtils.IS_OS_WINDOWS) {
			command = new String[] { "cmd", };
			download_path = prop.getProperty("download_path_microsoft");
		} else {
			command = new String[] { "bash", };
			download_path = prop.getProperty("download_path_linux");
		}

		String generatedString = RandomStringUtils.randomAlphanumeric(10);
		String videos_ = "videos_";
		String generatedFolderName = download_path + File.separator + videos_ + generatedString;

		File f = new File(generatedFolderName);
		try {
			if (f.mkdir()) {
				System.out.println("Directory Created");
			} else {
				System.out.println("Directory is not created");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String downloaded_file = null;
		YoutubeDLRequest request = new YoutubeDLRequest(url, generatedFolderName);
		try {
			YoutubeDL.execute(request, generatedString, download_path, formatid,formatType);
		} catch (YoutubeDLException e) {
			// TODO Auto-generated catch block
			error_execute();
		}

		String[] result_files = f.list();
		downloaded_file = result_files[0];
		download_video_to_browser(downloaded_file, generatedFolderName);

		if (FileUtils.deleteQuietly(f)) {
			System.out.println(f.getAbsolutePath() + " is deleted!");
		} else {
			System.out.println("Delete operation is failed.");
		}
	}

	public static void getImagesandFormats() throws IOException {
		try {
			String url = videoDefinitionStruct.url;
			VideoInfo response = YoutubeDL.getVideoInfo(url);
			videoDefinitionStruct.setTitle(response.title);
			System.out.println(response.title +" == "+url);
			videoDefinitionStruct.setPicture_url(response.thumbnail);
			ArrayList<VideoFormatListView> videoFormatList = new ArrayList<VideoFormatListView>();
			ArrayList<AudioFormatListView> audiFormatList = new ArrayList<AudioFormatListView>();
			for (int i = 0; i < response.formats.size(); i++) {
				VideoFormatListView videoFormat = new VideoFormatListView();
				AudioFormatListView audioFormat = new AudioFormatListView();
				if (response.formats.get(i).height != 0 && response.formats.get(i).width != 0) {
					videoFormat.setFormatId(response.formats.get(i).formatId);
					videoFormat.setFilesize(formatFileSize(response.formats.get(i).filesize));
					videoFormat.setExt(response.formats.get(i).ext);
					if (response.formats.get(i).formatNote.contains("medium")) {
						videoFormat.setFormat("**BEST-VIDEO** " + Integer.toString(response.formats.get(i).height) + "*"
								+ Integer.toString(response.formats.get(i).width));
					} else {
						videoFormat.setFormat(Integer.toString(response.formats.get(i).height) + "*"
								+ Integer.toString(response.formats.get(i).width));
					}
					videoFormatList.add(videoFormat);
				} else {
					audioFormat.setFormatId(response.formats.get(i).formatId);
					audioFormat.setFilesize(formatFileSize(response.formats.get(i).filesize));
					audioFormat.setExt(response.formats.get(i).ext);
					audioFormat.setFormat(response.formats.get(i).format);
					audiFormatList.add(audioFormat);
				}
			}
			videoDefinitionStruct.setFormats(videoFormatList);
			videoDefinitionStruct.setAudioFormats(audiFormatList);
			execute();
		} catch (

		YoutubeDLException e) {
			// TODO Auto-generated catch block
			error_execute();
		}
	}

	public static void download_video_to_browser(String file_name, String folder_name) throws IOException {
		try {
			// System.out.println("4 :Browserdan download edilecek folder name :
			// " + folder_name);
			String absoluteFilePath = "";
			String correctedName = "";
			correctedName = safeChar(file_name);
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.responseReset();
			externalContext.setResponseContentType("video/mp4");
			// externalContext.setResponseContentLength(file_size);
			externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + correctedName + "\"");
			externalContext.setResponseHeader("Refresh", "3; url = index.xhtml");
			absoluteFilePath = folder_name + File.separator + file_name;
			FileInputStream inputStream = new FileInputStream(new File(absoluteFilePath));
			OutputStream outputStream = externalContext.getResponseOutputStream();

			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			inputStream.close();
			outputStream.flush();
			outputStream.close();
			context.responseComplete();
		} catch (Exception e) {
			error_execute();
		}

	}

	public static String safeChar(String input) {
		char[] allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.".toCharArray();
		char[] charArray = input.toString().toCharArray();
		StringBuilder result = new StringBuilder();
		for (char c : charArray) {
			for (char a : allowed) {
				if (c == a)
					result.append(a);
			}
		}
		return result.toString();
	}

	public static void execute() throws IOException {
		// ...
		FacesContext.getCurrentInstance().getExternalContext().redirect("/DownloadVideos/formats.xhtml");
	}

	public static void error_execute() throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect("/DownloadVideos/errors.xhtml");
	}

	public static String formatFileSize(long size) {
		String hrSize = null;

		double b = size;
		double k = size / 1024.0;
		double m = ((size / 1024.0) / 1024.0);
		double g = (((size / 1024.0) / 1024.0) / 1024.0);
		double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

		DecimalFormat dec = new DecimalFormat("0.00");

		if (t > 1) {
			hrSize = dec.format(t).concat(" TB");
		} else if (g > 1) {
			hrSize = dec.format(g).concat(" GB");
		} else if (m > 1) {
			hrSize = dec.format(m).concat(" MB");
		} else if (k > 1) {
			hrSize = dec.format(k).concat(" KB");
		} else {
			hrSize = dec.format(b).concat(" Bytes");
		}

		return hrSize;
	}

	public void onRowSelect(SelectEvent event) {
		FacesMessage msg = new FacesMessage("FormatId", ((VideoFormatListView) event.getObject()).getFormatId());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

}
