package dansul.namethiscountrycapital;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Daniel on 3/18/14.
 */
public class ImageWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    String imageFileName;

    private Context context;
    private Bitmap bitmap;

    ImageLoadedListener imageLoadedListener;

    public ImageWorkerTask(Context context, ImageView imageView, Bitmap bitmap, ImageLoadedListener imageLoadedListener) {
        this.context = context;
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.bitmap = bitmap;

        if (imageLoadedListener == null) {
            throw new NullPointerException("ImageLoaderListener cannot be null.");
        }

        this.imageLoadedListener = imageLoadedListener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        imageFileName = params[0];
        bitmap = decodeSampleBitmap(context.getResources(), imageFileName);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();

            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                imageLoadedListener.updateUI();
            }
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int inSampleSize = 1;
        options.inSampleSize = inSampleSize;

        // Image width/height
        final int width = options.outWidth;
        final int height = options.outHeight;

        final int displayWidth = metrics.widthPixels;
        final int displayHeight = ((Double) (metrics.heightPixels * 0.60)).intValue(); // changed from 0.4

        if (width > (displayWidth *1.5) || height > (displayHeight * 1.5)) {
            inSampleSize = 2;

            final int halfWidth = width / 2;
            final int halfHeight = height / 2;

            while ((halfWidth / inSampleSize) > displayWidth || (halfHeight / inSampleSize) > displayHeight) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Scales an image if necessary.
     *
     * @param res
     * @param imageFileName
     * @return a Bitmap scaled by a factor determined by calculateInSampleSize method.
     */
    private Bitmap decodeSampleBitmap(Resources res, String imageFileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565; // only 2 bytes for 1 pixel instead of 4

        // Do this to reset the input stream otherwise it won't work
        // There is an issue when decoding the stream in the second pass
        // that makes it start at byte 1024
        InputStream is = openFileAssets(res, imageFileName);

        return BitmapFactory.decodeStream(is, null, options);
    }

    private InputStream openFileAssets(Resources res, String fileName) {
        // First check if its in assets, otherwise on the internal storage
        // otherwise return null

        InputStream is = null;

        try {
            is = res.getAssets().open("countries/" + fileName);
        } catch (FileNotFoundException e) {
            Log.d("TEST", "FileNotFoundException in opeFileAssets/ImageWorerTask:" + e);
        } catch (IOException e) {
            Log.d("TEST", "IOException in openFileAssets/ImageWorkerTask: " + e);
        }

        return is;
    }

}
