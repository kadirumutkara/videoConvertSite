package com.umutkara.youtubedl;

public interface DownloadProgressCallback {
    void onProgressUpdate(float progress, long etaInSeconds);
}
