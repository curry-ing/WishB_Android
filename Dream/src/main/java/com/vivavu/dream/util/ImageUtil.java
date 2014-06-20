package com.vivavu.dream.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import com.vivavu.dream.common.DreamApp;

import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 24.
 */
public class ImageUtil {
    public static File createImageFile() throws IOException{
        return createImageFile("JPEG");
    }

    public static File createImageFile(String prefix) throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = prefix + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static File createImageFileInInternalStorage() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = DreamApp.getInstance().getFilesDir();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getBitmap(File file, int targetWidth, int targetHeight) {
        return getBitmap(file.getAbsolutePath(), targetWidth, targetHeight);
    }

    public static Bitmap getBitmap(String photoPath, int targetWidth, int targetHeight) {

        targetWidth = targetWidth <= 0 ? 1: targetWidth;
        targetHeight = targetHeight <= 0 ? 1: targetHeight;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);

        // Determine how much to scale down the image
        //int scaleFactor = Math.min(photoW/targetWidth, photoH/targetHeight);
        int scaleFactor = calculateInSampleSize(bmOptions, targetWidth, targetHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        return bitmap;
    }

    /**
    * 이미지 회전 각도를 알아옴.
    * @param filepath
    * @return 각도
    */
    public synchronized static int getPhotoOrientationDegree(String filepath) {
        int degree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filepath);
        } catch (IOException e) {
            Log.d(ImageUtil.class.getSimpleName(), "Error: "+e.getMessage());
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        Log.d(ImageUtil.class.getSimpleName(), "Photo Degree: "+degree);
        return degree;
    }

    /**
     * 이미지를 특정 각도로 회전
     * @param bitmap
     * @param degrees
     * @return
     */
    public synchronized static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) {
        if ( degrees != 0 && bitmap != null ) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError e) {
                Log.d(ImageUtil.class.getSimpleName(), "Error: "+e.getMessage());
            }
        }

        return bitmap;
    }

    public synchronized static ByteArrayResource convertImageFileToByteArrayResource(final File file, int maxWidth, int maxHeight, int compressRate){
        int degree = ImageUtil.getPhotoOrientationDegree(file.getAbsolutePath());
        Bitmap bm = ImageUtil.getBitmap(file.getAbsolutePath(), maxWidth, maxHeight);
        bm = ImageUtil.getRotatedBitmap(bm, degree);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, compressRate, byteArray );
        ByteArrayResource bar = new ByteArrayResource(byteArray.toByteArray()){
            @Override
            public String getFilename() throws IllegalStateException {
                return file.getName();
            }
        };
        return bar;
    }

}
