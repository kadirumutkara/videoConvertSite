package com.umutkara.youtubedl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umutkara.mapper.VideoFormat;
import com.umutkara.mapper.VideoInfo;
import com.umutkara.mapper.VideoThumbnail;
import com.umutkara.utils.StreamGobbler;
import com.umutkara.utils.StreamProcessExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * <p>
 * Provide an interface for youtube-dl executable
 * </p>
 *
 * <p>
 * For more information on youtube-dl, please see
 * <a href="https://github.com/rg3/youtube-dl/blob/master/README.md">YoutubeDL
 * Documentation</a>
 * </p>
 */
public class YoutubeDL {

	/**
	 * Youtube-dl executable name
	 */
	protected static String executablePath = "youtube-dl";

	/**
	 * Append executable name to command
	 * 
	 * @param command
	 *            Command string
	 * @return Command string
	 */
	protected static String buildCommand(String command) {
		return String.format("%s %s", executablePath, command);
	}

	/**
	 * Execute youtube-dl request
	 * 
	 * @param request
	 *            request object
	 * @return response object
	 * @throws YoutubeDLException
	 */
	public static YoutubeDLResponse execute(YoutubeDLRequest request) throws YoutubeDLException {
		return execute(request, null, null, null, null, null);
	}

	public static YoutubeDLResponse execute(YoutubeDLRequest request, String generatedStringName)
			throws YoutubeDLException {
		return execute(request, null, generatedStringName, null, null, null);
	}

	public static YoutubeDLResponse execute(YoutubeDLRequest request, String generatedStringName, String downloadPath,
			String formatid, String formatType) throws YoutubeDLException {
		return execute(request, null, generatedStringName, downloadPath, formatid, formatType);
	}

	/**
	 * Execute youtube-dl request
	 * 
	 * @param request
	 *            request object
	 * @param callback
	 *            callback
	 * @return response object
	 * @throws YoutubeDLException
	 */
	public static YoutubeDLResponse execute(YoutubeDLRequest request, DownloadProgressCallback callback,
			String generatedStringName, String downloadPath, String formatid, String formatType)
			throws YoutubeDLException {
		String command = buildCommand(request.buildOptions());
		String generatedFolderNameLinux = request.getDirectory();
		Map<String, String> options = request.getOption();

		String[] bash_command;
		if (SystemUtils.IS_OS_WINDOWS) {
			bash_command = new String[] { "cmd", };

		} else {
			bash_command = new String[] { "bash", };

		}

		char ch = '"';
		String title = "/%(title)s-%(id)s.%(ext)s";

		YoutubeDLResponse youtubeDLResponse;
		Process process;
		int exitCode;
		StringBuffer outBuffer = new StringBuffer(); // stdout
		StringBuffer errBuffer = new StringBuffer(); // stderr
		PrintWriter stdin;
		long startTime = System.nanoTime();

		String[] split = command.split(" ");
		String testurl;

		ProcessBuilder processBuilder = new ProcessBuilder(bash_command);

		// Define directory if one is passed
		// if(directory != null)
		// processBuilder.directory(new File(directory));

		try {
			process = processBuilder.start();
			stdin = new PrintWriter(process.getOutputStream());
			if (!StringUtils.isEmpty(generatedStringName) && !StringUtils.isEmpty(downloadPath)) {
				if (SystemUtils.IS_OS_WINDOWS) {
					stdin.println("cd \"" + downloadPath + "\"");
				}
				if ((formatid.equals("0"))) {
					stdin.println(
							"youtube-dl -o " + ch + generatedFolderNameLinux + title + ch + " " + request.getUrl());
				} else {
					if (formatType.contains("audio")) {
						stdin.println("youtube-dl -o " + ch + generatedFolderNameLinux + title + ch + " -f " + formatid
								+ " " + request.getUrl());
						System.out.println("youtube-dl -o " + ch + generatedFolderNameLinux + title + ch + " -f "
								+ formatid + " " + request.getUrl());
					} else {
						stdin.println("youtube-dl -o " + ch + generatedFolderNameLinux + title + ch + " -f " + formatid
								+ "+bestaudio " + request.getUrl());
						System.out.println("youtube-dl -o " + ch + generatedFolderNameLinux + title + ch + " -f "
								+ formatid + "+bestaudio " + request.getUrl());
					}
				}
			} else {
				String main_director = getMainDirectory();
				stdin.println("cd \"" + main_director + "\"");
				stdin.println("youtube-dl -j " + request.getUrl());
			}
		} catch (IOException e) {
			throw new YoutubeDLException(e);
		}

		InputStream outStream = process.getInputStream();
		InputStream errStream = process.getErrorStream();

		StreamProcessExtractor stdOutProcessor = new StreamProcessExtractor(outBuffer, outStream);
		StreamGobbler stdErrProcessor = new StreamGobbler(errBuffer, errStream);

		try {
			stdin.close();
			stdOutProcessor.join();
			stdErrProcessor.join();
			exitCode = process.waitFor();
		} catch (InterruptedException e) {

			// process exited for some reason
			throw new YoutubeDLException(e);
		}

		String out = outBuffer.toString();
		String err = errBuffer.toString();
		System.out.println(err);

		if (exitCode > 0) {
			throw new YoutubeDLException(err);
		}

		int elapsedTime = (int) ((System.nanoTime() - startTime) / 1000000);

		youtubeDLResponse = new YoutubeDLResponse(command, options, downloadPath, exitCode, elapsedTime, out, err);

		return youtubeDLResponse;
	}

	/**
	 * Get youtube-dl executable version
	 * 
	 * @return version string
	 * @throws YoutubeDLException
	 */
	public static String getVersion() throws YoutubeDLException {
		YoutubeDLRequest request = new YoutubeDLRequest();
		request.setOption("version");
		return YoutubeDL.execute(request).getOut();
	}

	/**
	 * Retrieve all information available on a video
	 * 
	 * @param url
	 *            Video url
	 * @return Video info
	 * @throws YoutubeDLException
	 */
	public static VideoInfo getVideoInfo(String url) throws YoutubeDLException {

		// Build request
		YoutubeDLRequest request = new YoutubeDLRequest(url);
		// request.setOption("j");
		// request.setOption("no-playlist");
		YoutubeDLResponse response = YoutubeDL.execute(request);
		String line = null;
		Scanner scanner = new Scanner(response.getOut());
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (!line.equals("")) {
				if (line.substring(0, 1).equals("{")) {
					break;
				}
			}
		}
		scanner.close();
		// Parse result
		ObjectMapper objectMapper = new ObjectMapper();
		VideoInfo videoInfo;
		try {
			videoInfo = objectMapper.readValue(line, VideoInfo.class);
		} catch (IOException e) {
			throw new YoutubeDLException("Unable to parse video information: " + e.getMessage());
		}
		return videoInfo;
	}

	/**
	 * List formats
	 * 
	 * @param url
	 *            Video url
	 * @return list of formats
	 * @throws YoutubeDLException
	 */
	public static List<VideoFormat> getFormats(String url) throws YoutubeDLException {
		VideoInfo info = getVideoInfo(url);
		return info.formats;
	}

	/**
	 * List thumbnails
	 * 
	 * @param url
	 *            Video url
	 * @return list of thumbnail
	 * @throws YoutubeDLException
	 */
	public static List<VideoThumbnail> getThumbnails(String url) throws YoutubeDLException {
		VideoInfo info = getVideoInfo(url);
		return info.thumbnails;
	}

	/**
	 * List categories
	 * 
	 * @param url
	 *            Video url
	 * @return list of category
	 * @throws YoutubeDLException
	 */
	public static List<String> getCategories(String url) throws YoutubeDLException {
		VideoInfo info = getVideoInfo(url);
		return info.categories;
	}

	/**
	 * List tags
	 * 
	 * @param url
	 *            Video url
	 * @return list of tag
	 * @throws YoutubeDLException
	 */
	public static List<String> getTags(String url) throws YoutubeDLException {
		VideoInfo info = getVideoInfo(url);
		return info.tags;
	}

	/**
	 * Get command executable or path to the executable
	 * 
	 * @return path string
	 */
	public static String getExecutablePath() {
		return executablePath;
	}

	/**
	 * Set path to use for the command
	 * 
	 * @param path
	 *            String path to the executable
	 */
	public static void setExecutablePath(String path) {
		executablePath = path;
	}

	public static String getMainDirectory() throws FileNotFoundException, IOException {
		// String rootPath =
		// Thread.currentThread().getContextClassLoader().getResource("").getPath();
		// String appConfigPath = rootPath + "app.properties";
		// Properties appProps = new Properties();
		String directory = "";
		// appProps.load(new FileInputStream(appConfigPath));

		if (SystemUtils.IS_OS_WINDOWS) {
			directory = "C:/Users/kadir.kara/Desktop/vk/path";
		} else {
			directory = "/usr/local/tomcat/temp";
		}

		return directory;

	}
}
