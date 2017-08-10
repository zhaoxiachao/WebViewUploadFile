package flight.xy.com.webviewuploadfile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText mLoadEdit;
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView() {
        mLoadEdit = (EditText) findViewById(R.id.main_load);
        mBtn = (Button) findViewById(R.id.main_btn);
    }

    private void initListener() {
        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.main_btn) {
            String loadUrl = mLoadEdit.getText().toString();
            if (TextUtils.isEmpty(loadUrl)) {
                loadUrl = mLoadEdit.getHint().toString();
            }
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, WebViewActivity.class);
            intent.putExtra("load_url", loadUrl);
            startActivity(intent);
        }
    }
}
