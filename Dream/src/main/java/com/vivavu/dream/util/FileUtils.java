package com.vivavu.dream.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.vivavu.dream.common.DreamApp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by yuja on 14. 3. 16.
 */
public class FileUtils {
    private static final String CACHE_FOLDER = "/data/data/com.vivavu.dream/cache/";

    /**
     * 이미지 파일 다운로드
     * @param fileName 파일 이름
     * @return 저장되어있는곳의 path+filename 리턴
     * @throws Exception
     */
    public static File getDownloadFromUrl(String downloadUrl, String fileName) throws Exception{

        File folder = new File(CACHE_FOLDER);
        File file = new File(folder, fileName);
        if(file.exists()){
            return file;
        }
        if(!folder.isDirectory()){//캐시 디렉토리가 없으면 만들기
            folder.mkdir();
        }


        URL url = new URL(downloadUrl);
        InputStream input = null;
        int count = 0;

        input = new BufferedInputStream(url.openStream(), 8192);
        OutputStream output = new FileOutputStream(file);
        byte data[] = new byte[1024];
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
        }

        output.flush();
        output.close();
        input.close();

        return file;//패스 리턴
    }

    public static File getDownloadFromUrl(String downloadUrl) throws Exception{
        return getDownloadFromUrl(downloadUrl, getFileNameFromUrl(downloadUrl));
    }
    public static String getFileNameFromUrl(String url){
        Uri downloadUri = Uri.parse(url);
        List<String> pathSegments = downloadUri.getPathSegments();
        String filename = pathSegments.get(pathSegments.size()-1);
        return filename;
    }

    public static boolean deleteFile(File file){
        boolean returnValue = false;
        if(file != null){
            if(file.exists() && file.isFile()){
                returnValue = file.delete();
                if(returnValue){
                    Uri contentUri = Uri.fromFile(file.getParentFile());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(contentUri);
                        DreamApp.getInstance().sendBroadcast(mediaScanIntent);
                    } else {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                        mediaScanIntent.setData(contentUri);
                        DreamApp.getInstance().sendBroadcast(new Intent(mediaScanIntent));
                    }
                }


            }
        }
        return returnValue;
    }
}
