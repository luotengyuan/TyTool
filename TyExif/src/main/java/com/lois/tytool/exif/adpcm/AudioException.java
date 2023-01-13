package com.lois.tytool.exif.adpcm;

public class AudioException extends Exception {
    public AudioException(Throwable cause) {
        super(cause);
    }
    public AudioException(String message) {
        super(message);
    }
}