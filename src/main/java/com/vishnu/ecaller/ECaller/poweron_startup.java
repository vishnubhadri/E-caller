package com.vishnu.ecaller.ECaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class poweron_startup extends BroadcastReceiver {
    public poweron_startup() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
try {
    if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
        Intent serviceIntent = new Intent(context, ECallerService.class);
        context.startService(serviceIntent);
    }
}catch (Exception e)
{
    Log.i(Utils.LogTag, "onReceive: error "+e.toString());
}


    }
}
