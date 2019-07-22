package com.andrew.selfserviceprototype.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DecodeBitmap {
    public static void setScaledImageView(ImageView image, final int resId, final Context context) {
        final ImageView iv = image;
        ViewTreeObserver viewTreeObserver = iv.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);
                int height = iv.getMeasuredHeight();
                int width = iv.getMeasuredWidth();
                iv.setImageBitmap(decodeSampleBitmapFromResource(context.getResources(), resId, width, height));
                // COLLISION WITH GLIDE
                return true;
            }
        });
    }

    public static ScaleDrawable setScaleDrawable(Context v, int drawable) {
        Drawable compressDrawable = v.getDrawable(drawable);
        compressDrawable.setBounds(0, 0
                , (int) (compressDrawable.getIntrinsicWidth() * 0.5)
                , (int) (compressDrawable.getIntrinsicHeight() * 0.5));
        return new ScaleDrawable(compressDrawable, 0, 50, 50);
    }

    public static Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int w, int h) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > h || width > w) {
            final int hHeight = height / 2;
            final int hWidth = width / 2;

            while ((hHeight / inSampleSize >= h) && (hWidth / inSampleSize) >= w) {
                inSampleSize += 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int w, int h) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resId, options);

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = calculateInSampleSize(options, w, h);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
