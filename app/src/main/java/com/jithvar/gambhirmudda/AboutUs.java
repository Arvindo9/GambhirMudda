package com.jithvar.gambhirmudda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.jithvar.gambhirmudda.constant.Config.COMPANY_URL;

/**
 * Created by Arvindo Mondal on 4/8/17.
 * Company name Jithvar
 * Email arvindo@jithvar.com
 */
public class AboutUs extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        findViewById(R.id.jithar).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jithar:
                Intent i  = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(COMPANY_URL));
                startActivity(i);
                break;
        }
    }
}
