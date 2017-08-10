package flight.xy.com.webviewuploadfile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.widget.TextView;

public class WebViewImgSelectDialog {
    private Context mContext;
    private Dialog dialog;
    private ValueCallback<Uri[]> mFilePathCallback;

    public WebViewImgSelectDialog(Context context, ValueCallback<Uri[]> filePathCallback) {
        super();
        this.mContext = context;
        this.mFilePathCallback = filePathCallback;
    }

    public void ShowDialog(final OnBottomClick click) {
        // TODO Auto-generated method stub
        dialog = new Dialog(mContext);
        dialog.setOnCancelListener(new OnCancelListener() {
            
            @Override
            public void onCancel(DialogInterface dialog) {
                // To cancel the request, call filePathCallback.onReceiveValue(null) and return true.
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawableResource(android.R.color.white);
        
        View customView = LayoutInflater.from(mContext).inflate(R.layout.duoqu_webview_img_select_dialog, null);
        // 手机相册
        TextView photoAlbum = (TextView) customView.findViewById(R.id.duoqu_webview_img_select_dialog_photo_album);
        // 相机照相
        TextView shooting = (TextView) customView.findViewById(R.id.duoqu_webview_img_select_dialog_shooting);
        // 文件管理器
        TextView choose = (TextView) customView.findViewById(R.id.duoqu_webview_img_select_dialog_choose);
        
        photoAlbum.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                click.OnPhotoAlbumClick();
                dialog.dismiss();
            }
        });
        
        shooting.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                click.OnShootClick();
                dialog.dismiss();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                click.OnChooseClick();
                dialog.dismiss();
            }
        });
        
        
        dialog.setContentView(customView);
        dialog.show();
    }

    public interface OnBottomClick {
        void OnPhotoAlbumClick();
        void OnShootClick();
        void OnChooseClick();
    }
}

