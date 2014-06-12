package com.vivavu.dream.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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

    public static Bitmap getImageFromUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            BufferedInputStream bis = new
                    BufferedInputStream(conn.getInputStream());
            Bitmap bm = BitmapFactory.decodeStream(bis);
            bis.close();
            return bm;
        } catch (IOException e) {
            Log.e("dream", e.toString());
        }
        return null;
    }

    private void galleryAddPic(Context context, String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static void setPic(ImageView view, String photoPath) {
        // Get the dimensions of the View
        int targetW = view.getWidth();
        int targetH = view.getHeight();

        view.setImageBitmap(getBitmap(photoPath, targetW, targetH ));
    }

    public static Bitmap decodeStream(InputStream inputStream){
        /*// Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/maxW, photoH/maxH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        return bitmap;*/
        return null;
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

    // 이미지 리사이징
    Drawable resize(Resources resources, String path){
        /// 비트맵 저장할 변수를 하나 생성한다.
        Bitmap bmp ;
        /// 이미지 불러올때 사용할 옵션을 만든다.
        BitmapFactory.Options options2 =  new BitmapFactory.Options( );
        /// 이미지 가져올때 샘플링할 옵션 값이 클수록 작게 가져온다.
        options2.inSampleSize = 4 ;
        /// 해당 옵션을 사용하여 이미지를 가져온다.
        /// 아래 줄에서 실제 이미지를 메모리에 전부 로드하지는 않는다.
        bmp = BitmapFactory.decodeFile( path, options2 ) ;
        /// 가져온 이미지가 Bitmap(비트맵)이라서 Drawable로 바꿔줘야한다.
        /// Drawable형태의 Bitmap로 바꾼다.
        /// 실제 메모리에 이미지를 로드한다.
        BitmapDrawable dbmp = new BitmapDrawable( resources, bmp );
        /// Drawable 형태로 형변환 해서 반환한다.
        return (Drawable)dbmp ;
    }



	/*
	 * setCameraImageBackground, setAlbumImageBackground
	 * 위 두개는 이미지뷰의 사이즈에 꽉 차게 세팅한다.
	 *
	 * setCameraImageDrawable, setAlbumImageDrawble
	 * 위 두개는 이미지의 비율에 맞춰 세팅한다.
	 * 단 ImageView에 android:scaleType="fitStart" 옵션을 설정하는걸 잊지말자
	 */
    // 카메라로 찍은 사진을 이미지뷰에 세팅
    public void setCameraImageBackground( Bitmap bm, ImageView iv, String tempPictuePath ) {

        BitmapDrawable bmd = new BitmapDrawable( bm ) ;
        // tempPictuePath는 사진 저장 경로를 바꾼 다음 카메라로 사진을 찍었을 때 파일의 경로이다.

        File copyFile = new File( tempPictuePath ) ;
        try {
            copyFile.createNewFile( ) ;
            OutputStream out = null;
            out = new FileOutputStream(copyFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 70, out) ;
            out.close( ) ;

            if( copyFile.exists( ) && copyFile.length( ) > 0 ) /// 성공적으로파일이 저장되어 존재함
            {
                ImageUtil resize = new ImageUtil( ) ;
                Drawable dr = resize.resize( iv.getResources(), tempPictuePath ) ;
                //iv.setImageBitmap( iv.getResources(), dr ) ;
            } else {

            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    // 카메라로 찍은 사진을 이미지뷰에 세팅
    public void setCameraImageDrawable( Bitmap bm, ImageView iv, String tempPictuePath ){

        BitmapDrawable bmd = new BitmapDrawable( bm ) ;
        // tempPictuePath는 사진 저장 경로를 바꾼 다음 카메라로 사진을 찍었을 때 파일의 경로이다.
        File copyFile = new File( tempPictuePath ) ;
        try {
            copyFile.createNewFile( ) ;
            OutputStream out = null;
            out = new FileOutputStream(copyFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 70, out) ;
            out.close( ) ;
            if( copyFile.exists( ) && copyFile.length( ) > 0 ) /// 성공적으로파일이 저장되어 존재함
            {
                ImageUtil resize = new ImageUtil( ) ;
                Drawable dr = resize.resize(iv.getResources(), tempPictuePath ) ;
                iv.setImageDrawable( dr ) ;
                //iv.setBackgroundDrawable( dr ) ;
            } else {

            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }



    // 앨범에서 가져온 사진을 이미지뷰에 세팅
    public void setAlbumImageBackground( String path, ImageView iv )
    {
        BitmapFactory.Options options = new BitmapFactory.Options( ) ; 	// 비트맵 이미지의 옵션 받을 변수 생성
        options.inJustDecodeBounds = true;  							// 비트맴을 바로 로드하지 말고 옵션만 받아오라고 설정
        BitmapFactory.decodeFile( path, options ) ; 					// 매트맵을 읽는데 윗줄에 의해 옵션만 받아오고 비트맵 다 읽지는 않음
        /// 이미지의 높이를 얻음
        int fscale = options.outHeight ;
        if( options.outWidth > options.outHeight )	// 이미지의 높이보다 넓이가 클 경우
        {
            fscale = options.outWidth ;				// 이미지의 넓이를 스케일에 저장
        }
        Bitmap bmp ;	// 실제 이미지를 저장할 변수
        /// 이미지의 넓이가 800보다 크면
        if( 800 < fscale )
        {
            /// 이미지의 사이즈를 800으로 나눈 만큼 리사이징 할거다
            fscale = fscale / 800 ;
            /// 새 비트맵 옵션 생성
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = fscale ;	/// 리사이징할 비율 설정
            bmp = BitmapFactory.decodeFile( path, options2 ) ;	/// 실제로 비트맵을 읽어온다.
        } else {	/// 사이즈가 적당 하면 그냥 읽는다.
            bmp = BitmapFactory.decodeFile( path ) ;
        }

        /// 읽은 배트맵을 형변환해서 새로 생성
        BitmapDrawable dbmp = new BitmapDrawable( bmp );
        Drawable dr = (Drawable)dbmp ;	/// 그걸 다시 형변환
        iv.setBackgroundDrawable( dr ) ; /// 뷰 객체의 백그라운드로 설정
    }

    public void setAlbumImageDrawble( String path, ImageView iv ){
        BitmapFactory.Options options = new BitmapFactory.Options( ) ; 	// 비트맵 이미지의 옵션 받을 변수 생성
        options.inJustDecodeBounds = true;  							// 비트맴을 바로 로드하지 말고 옵션만 받아오라고 설정
        BitmapFactory.decodeFile( path, options ) ; 					// 매트맵을 읽는데 윗줄에 의해 옵션만 받아오고 비트맵 다 읽지는 않음
        /// 이미지의 높이를 얻음
        int fscale = options.outHeight ;
        if( options.outWidth > options.outHeight )	// 이미지의 높이보다 넓이가 클 경우
        {
            fscale = options.outWidth ;				// 이미지의 넓이를 스케일에 저장
        }
        Bitmap bmp ;	// 실제 이미지를 저장할 변수
        /// 이미지의 넓이가 800보다 크면
        if( 800 < fscale )
        {
            /// 이미지의 사이즈를 800으로 나눈 만큼 리사이징 할거다
            fscale = fscale / 800 ;
            /// 새 비트맵 옵션 생성
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = fscale ;	/// 리사이징할 비율 설정
            bmp = BitmapFactory.decodeFile( path, options2 ) ;	/// 실제로 비트맵을 읽어온다.
        } else {	/// 사이즈가 적당 하면 그냥 읽는다.
            bmp = BitmapFactory.decodeFile( path ) ;
        }
        /// 읽은 배트맵을 형변환해서 새로 생성
        BitmapDrawable dbmp = new BitmapDrawable( bmp );
        Drawable dr = (Drawable)dbmp ;	/// 그걸 다시 형변환
        iv.setImageDrawable( dr ) ; /// 뷰 객체의 백그라운드로 설정
        //iv.setBackgroundDrawable( dr ) ; /// 뷰 객체의 백그라운드로 설정

    }
}
