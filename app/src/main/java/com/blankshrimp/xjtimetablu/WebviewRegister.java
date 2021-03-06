package com.blankshrimp.xjtimetablu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.blankshrimp.xjtimetablu.util.DataNormalizer;
import com.blankshrimp.xjtimetablu.util.NewListDAO;

import java.util.List;
import java.util.Map;

public class WebviewRegister extends AppCompatActivity {

    private AutoCompleteTextView name;
    private AutoCompleteTextView remark;
    private String captured = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.webRegisterToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        name = (AutoCompleteTextView) findViewById(R.id.nameW);
        remark = (AutoCompleteTextView) findViewById(R.id.remarkW);
        Button button = (Button) findViewById(R.id.confirm);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cancel = false;
                name.setError(null);
                View focusView = null;
                String nameCapture = name.getText().toString();
                if (TextUtils.isEmpty(nameCapture)) {
                    name.setError(getString(R.string.error_empty));
                    focusView = name;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    NewListDAO newListDAO = new NewListDAO(WebviewRegister.this);
                    DataNormalizer dataNormalizer = new DataNormalizer();
                    List<List<Map<String, String>>> input = dataNormalizer.dataNormalize(bundle.getString("html"));
                    newListDAO.register(input, nameCapture, remark.getText().toString(), dataNormalizer.getDay());
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
