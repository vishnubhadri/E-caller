package com.vishnu.ecaller.ECaller;

import android.app.Activity;
import android.os.Bundle;

public class setcontact_help extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setcontact_help);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
