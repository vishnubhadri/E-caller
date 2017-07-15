package com.vishnu.ecaller.ECaller;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Utils {
    public static final int NOT_ID = 25;
    public static final int REQUEST_CODE_PICK_CONTACTS = 1;
    static final int READ_BLOCK_SIZE = 100;
    public static String LogTag = "ECaller";
    public static String EXTRA_MSG = "extra_msg";
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1011;
    public static final int PERMISSIONS_REQUEST_CALL = 1015;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            return Settings.canDrawOverlays(context);
        }
    }


}
