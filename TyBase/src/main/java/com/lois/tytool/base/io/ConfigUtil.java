package com.lois.tytool.base.io;

import com.lois.tytool.base.debug.TyLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件读取工具
 *
 * @author Luo.T.Y
 * @time 2017-10-11
 */
public class ConfigUtil {
    private static final String TAG = ConfigUtil.class.getSimpleName();
    //默认节点，默认注释符号
    private static String GLOBLE_CONFIG = "__globle";
    private String[] comments = {"#", ";"};
    private Map<String, Map<String, String>> configMap;
    private static ConfigUtil instance;

    private static boolean loaded = false;

    private ConfigUtil() {
        configMap = new HashMap<String, Map<String, String>>();
    }

    public static void init(String fileName) {
        init(fileName, null);
    }

    public static void init(String fileName, String[] comments) {
        instance = new ConfigUtil();
        if (comments != null) {
            instance.setComments(comments);
        }

        loaded = instance.loadConfig(fileName);
        if (loaded) {
            com.lois.tytool.base.debug.TyLog.v(TAG, "load config success.");
        } else {
            TyLog.e(TAG, "load config failed.");
        }
    }

    public static boolean isLoaded() {
        return loaded;
    }

    private void setComments(String[] comments) {
        this.comments = comments;
    }

    private boolean loadConfig(String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return loadConfig(new FileInputStream(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadConfig(InputStream is) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new UnicodeReader(is, "UTF-8"));
            String line;
            Map<String, String> inMap;

            inMap = new HashMap<String, String>();
            configMap.put(GLOBLE_CONFIG, inMap);

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    if (isComment(line)) {
                        continue;
                    } else if (isSection(line)) {
                        String section = line.substring(1, line.indexOf("]"));
                        if (configMap.containsKey(section)) {
                            inMap = configMap.get(section);
                        } else {
                            inMap = new HashMap<String, String>();
                            configMap.put(section, inMap);
                        }
                    } else {
                        int index = line.indexOf("=");
                        String key = null;
                        String val = null;
                        if (index != -1) {
                            key = line.substring(0, index);
                            val = line.substring(index + 1);
                            inMap.put(key == null ? key : key.trim(), val == null ? val : val.trim());
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean isSection(String line) {
        return line.startsWith("[") && line.indexOf("]") == line.length() - 1;
    }

    private boolean isComment(String line) {
        for (String comment : comments) {
            if (line.startsWith("#")) {
                return true;
            }
        }
        return false;
    }

    public static ConfigUtil getInstance() {
        if (instance == null) {
            throw new RuntimeException("not init.");
        }
        return instance;
    }

    public String getConfigIgnoreCase(String section, String key) {
        return getConfig(section, key, true);
    }

    public String getConfig(String section, String key) {
        return getConfig(section, key, false);
    }

    private String getConfig(String section, String key, boolean ignorecase) {
        String val = null;
        if (section == null) {
            section = GLOBLE_CONFIG;
        }
        if (ignorecase) {
            section = section.toLowerCase();
        }
        if (configMap != null && configMap.containsKey(section)) {
            Map<String, String> inMap = configMap.get(section);
            if (inMap != null) {
                val = inMap.get(key);
            }
        }
        return val;
    }

    public boolean hasSection(String section){
        if (section == null || configMap == null){
            return false;
        }
        return configMap.containsKey(section);
    }

    /**
     version: 1.1 / 2007-01-25
     - changed BOM recognition ordering (longer boms first)

     Original pseudocode   : Thomas Weidenfeller
     Implementation tweaked: Aki Nieminen

     http://www.unicode.org/unicode/faq/utf_bom.html
     BOMs:
     00 00 FE FF    = UTF-32, big-endian
     FF FE 00 00    = UTF-32, little-endian
     EF BB BF       = UTF-8,
     FE FF          = UTF-16, big-endian
     FF FE          = UTF-16, little-endian

     Win2k Notepad:
     Unicode format = UTF-16LE
     ***/

    /**
     * Generic unicode textreader, which will use BOM mark
     * to identify the encoding to be used. If BOM is not found
     * then use a given default or system encoding.
     */
    public class UnicodeReader extends Reader {
        PushbackInputStream internalIn;
        InputStreamReader internalIn2 = null;
        String defaultEnc;

        private static final int BOM_SIZE = 4;

        /**
         * @param in         inputstream to be read
         * @param defaultEnc default encoding if stream does not have
         *                   BOM marker. Give NULL to use system-level default.
         */
        UnicodeReader(InputStream in, String defaultEnc) {
            internalIn = new PushbackInputStream(in, BOM_SIZE);
            this.defaultEnc = defaultEnc;
        }

        public String getDefaultEncoding() {
            return defaultEnc;
        }

        /**
         * Get stream encoding or NULL if stream is uninitialized.
         * Call init() or read() method to initialize it.
         */
        public String getEncoding() {
            if (internalIn2 == null) {
                return null;
            }
            return internalIn2.getEncoding();
        }

        /**
         * Read-ahead four bytes and check for BOM marks. Extra bytes are
         * unread back to the stream, only BOM bytes are skipped.
         */
        protected void init() throws IOException {
            if (internalIn2 != null) {
                return;
            }

            String encoding;
            byte bom[] = new byte[BOM_SIZE];
            int n, unread;
            n = internalIn.read(bom, 0, bom.length);

            if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) &&
                    (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
                encoding = "UTF-32BE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) &&
                    (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
                encoding = "UTF-32LE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) &&
                    (bom[2] == (byte) 0xBF)) {
                encoding = "UTF-8";
                unread = n - 3;
            } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
                encoding = "UTF-16BE";
                unread = n - 2;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
                encoding = "UTF-16LE";
                unread = n - 2;
            } else {
                // Unicode BOM mark not found, unread all bytes
                encoding = defaultEnc;
                unread = n;
            }

            if (unread > 0) {
                internalIn.unread(bom, (n - unread), unread);
            }

            // Use given encoding
            if (encoding == null) {
                internalIn2 = new InputStreamReader(internalIn);
            } else {
                internalIn2 = new InputStreamReader(internalIn, encoding);
            }
        }

        @Override
        public void close() throws IOException {
            init();
            internalIn2.close();
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            init();
            return internalIn2.read(cbuf, off, len);
        }

    }
}
