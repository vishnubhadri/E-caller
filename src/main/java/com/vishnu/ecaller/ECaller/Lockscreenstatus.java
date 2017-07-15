

package com.vishnu.ecaller.ECaller;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Lockscreenstatus extends BroadcastReceiver {
    public Lockscreenstatus() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i(Utils.LogTag, "onReceive: ");
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        try {
            if (Main.Lockscreen) {
                Log.i(Utils.LogTag, "Run only when device locked");
                if (km.inKeyguardRestrictedInputMode()) {
                    Log.i(Utils.LogTag, "device locked");
                    Intent con = new Intent(context, ECallerService.class);
                    context.startService(con);
                }
            } else if (!Main.Lockscreen) {
                Log.i(Utils.LogTag, "Running over all apps");
                Intent con = new Intent(context, ECallerService.class);
                context.startService(con);
            }
        } catch (Exception e) {
            Log.i(Utils.LogTag, "Exception found :" + e.toString() + " So.Running over all apps");
            Intent con = new Intent(context, ECallerService.class);
            context.startService(con);
        }


    }

}

