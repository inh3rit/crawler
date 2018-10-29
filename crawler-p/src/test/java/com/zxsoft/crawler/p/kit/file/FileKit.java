package com.zxsoft.crawler.p.kit.file;

import com.jfinal.kit.StrKit;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by cox on 2016/1/27.
 */
public class FileKit extends com.jfinal.kit.FileKit {

    private static final Logger log = LoggerFactory.getLogger(FileKit.class);


    public static Charset getFileEncoding(File f, Charset defCharset) {

        try {
            byte[] buf = new byte[4096];
            java.io.FileInputStream fis = new java.io.FileInputStream(f);
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            detector.reset();
            return StrKit.notBlank(encoding) ? Charset.forName(encoding) : defCharset;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defCharset;
    }

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
            Charset fe = FileKit.getFileEncoding(file, defCharset);
            fe = fe.equals(Charset.forName("windows-1252")) ? Charset.forName("GBK") : fe;
            log.debug("file {} charset is: {}", file.getName(), fe);
            isr = new InputStreamReader(new FileInputStream(file), fe);
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

    public static Boolean writerFile(String fileName, String content, Charset c) {
        return FileKit.writerFile(new File(fileName), content, c);
    }

    public static Boolean writerFile(File file, String content, Charset c) {
        BufferedWriter out = null;
        try {
            if (file.isDirectory()) return false;
            if (!file.exists()) file.createNewFile();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), c));
            out.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out!=null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Boolean mkdirs(String path) {
        File f = new File(path);
        log.debug("File: {}, can write: {}", f.getAbsolutePath(), f.canWrite());
        log.debug("File: {}, is dir: {}", f.getAbsolutePath(), f.isDirectory());
        return f.isDirectory() || f.mkdirs();
    }

    public static String getFileName(String file) {
        if (StrKit.isBlank(file)) return null;
        Integer ix = file.lastIndexOf(".");
        return file.substring(0, ix<0 ? file.length() : ix);
    }

    public static String getFileSuffix(String file) {
        if (StrKit.isBlank(file)) return null;
        Integer ix = file.lastIndexOf(".");
        return ix<0 ? null : file.substring(ix+1, file.length());
    }

}