package com.lois.tytool.exif.adpcm;

import java.io.InputStream;

public interface StreamableDecoder {
    void init(InputStream is) throws AudioException;
    byte[] decode() throws AudioException;
}