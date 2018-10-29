package com.zxsoft.crawler.d.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by cox on 2016/1/27.
 */
public class FileKit extends com.jfinal.kit.FileKit {

    private static final Logger log = LoggerFactory.getLogger(FileKit.class);

    public static String readFile(String path) {
        return FileKit.readFile(path, Charset.forName("UTF-8"));
    }

    public static String readFile(File file) {
        return FileKit.readFile(file, Charset.forName("UTF-8"));
    }


    public static String readFile(String path, Charset defCharset) {
        return FileKit.readFile(new File(path), defCharset);
    }


    public static String readFile(File file, Charset defCharset) {
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            if (!file.exists()) {
                log.error("文件 {} 不存在", file.getAbsolutePath());
                return null;
            }
            // Charset fe = FileKit.getFileEncoding(file, defCharset);
            // fe = fe.equals(Charset.forName("windows-1252")) ? Charset.forName("GBK") : fe;
            log.debug("file {} charset is: {}", file.getName(), defCharset);
            isr = new InputStreamReader(new FileInputStream(file), defCharset);
            br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                // line = new String(line.getBytes(fe), defCharset);
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (isr != null) isr.close();
                if (br != null) br.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

}