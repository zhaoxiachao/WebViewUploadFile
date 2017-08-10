package flight.xy.com.webviewuploadfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by helen on 2017/8/10.
 */

public class WebViewActivity extends Activity {

    private final static String TAG = "[WebViewActivity]";
    private WebView mWebView;
    private ValueCallback<Uri[]> mUploadMessage = null;
    // 手机相册
    private int TAKE_PHOTO_ALBUM = 1;
    // 相机照相
    private int TAKE_PHOTO_REQUEST_CODE = 2;
    // 文件管理器
    private int FILECHOOSER_RESULTCODE = 3;
    private Uri mImageUri;
    private LinkedList<String> mFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(mWebView, layoutParams);

        init();
        String loadUrl = getIntent().getStringExtra("load_url");
        mWebView.loadUrl(loadUrl);
    }

    private void init() {
        initWebView();
        initSettings();
    }

    private void initWebView() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                // show dialog
                WebViewImgSelectDialog dialog = new WebViewImgSelectDialog(WebViewActivity.this, filePathCallback);
                dialog.ShowDialog(new WebViewImgSelectDialogClick(filePathCallback, fileChooserParams));
                return true;
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void initSettings() {
        WebSettings settings = mWebView.getSettings();
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
    }


    private class WebViewImgSelectDialogClick implements WebViewImgSelectDialog.OnBottomClick {
        ValueCallback<Uri[]> mFilePathCallback = null;
        WebChromeClient.FileChooserParams mFileChooserParams = null;

        public WebViewImgSelectDialogClick(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            // TODO Auto-generated constructor stub
            this.mFilePathCallback = filePathCallback;
            this.mFileChooserParams = fileChooserParams;
            if (mFileName == null) {
                mFileName = new LinkedList<String>();
            }
        }

        @Override
        public void OnShootClick() {
            openCamera(mFilePathCallback, mFileChooserParams);
        }

        @Override
        public void OnChooseClick() {
            openLocalFile(mFilePathCallback, mFileChooserParams);
        }

        @Override
        public void OnPhotoAlbumClick() {
            // TODO Auto-generated method stub
            openPhotoAlbumImage(mFilePathCallback, mFileChooserParams);
        }
    }

    /**
     * 手机相册
     *
     * @param filePathCallback
     * @param fileChooserParams
     */
    private void openPhotoAlbumImage(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = filePathCallback;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "选择应用"), TAKE_PHOTO_ALBUM);
    }

    /**
     * 相机照相
     */
    private void openCamera(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = filePathCallback;
        String fileName = "camera_" + System.currentTimeMillis() + ".jpg";
        mFileName.add(fileName);
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        mImageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
    }

    /**
     * 文件管理器
     */
    @SuppressLint("NewApi")
    private void openLocalFile(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = filePathCallback;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "选择应用"), FILECHOOSER_RESULTCODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri result = null;
            if (resultCode != RESULT_OK) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE || requestCode == TAKE_PHOTO_ALBUM) {
                if (null == mUploadMessage) {
                    return;
                }
                result = data == null || resultCode != RESULT_OK ? null : data.getData();
            } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
                if (mUploadMessage == null) {
                    return;
                }
                if (mImageUri == null) {
                    return;
                }

                result = mImageUri;
                // Images captured by the URI, and through compression
                // processing
                Bitmap bitmap = null;
                try {
                    bitmap = getBitmapFormUri(this, result);
                } catch (Exception e) {
                }
                // resave Image .
                if (bitmap != null) {
                    saveMyBitmap(bitmap, mFileName.getLast());
                }
            }

            if (result == null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
            /* RM-943 zhaoxiachao 20161117 start */
            if (result.toString().contains("content://") && requestCode != TAKE_PHOTO_REQUEST_CODE) {
                mUploadMessage.onReceiveValue(new Uri[]{result});
                mUploadMessage = null;
                return;
            }
            /* RM-943 zhaoxiachao 20161117 end */
            String path = FileUtils.getPath(this, result);
            if (TextUtils.isEmpty(path)) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
//            FileProvider.getUriForFile(mContext, "android.task.mm3.fileprovider", new File(path));
            // Uri[] uri = new Uri[]{Uri.fromFile(new File(path))};
            Uri[] uri = new Uri[]{Uri.parse("file://" + path)};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mUploadMessage.onReceiveValue(new Uri[]{uri[0]});
            } else {
                mUploadMessage.onReceiveValue(uri);
            }
            mUploadMessage = null;
        } catch (Throwable ex) {
        }
    }


    public static Bitmap getBitmapFormUri(Context ctx, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ctx.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1)) {
            return null;
        }
        float hh = 1024f;
        float ww = 768f;
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ctx.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return bitmap;
    }

    public void saveMyBitmap(Bitmap bitmap, String fileName) {
        File f = new File(Environment.getExternalStorageDirectory(), fileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
        }
        if (fOut != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
            }
            try {
                fOut.close();
            } catch (IOException e) {
            }
        }
    }

    private void delMyBitmap() {
        // TODO Auto-generated method stub
        try {
            if (mFileName == null) {
                return;
            }
            File f = null;
            for (String fileName : mFileName) {
                f = new File(Environment.getExternalStorageDirectory(), fileName);
                if (f != null && f.isFile() && f.exists()) {
                    f.delete();
                }
            }
        } catch (Throwable e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delMyBitmap();
    }
}
