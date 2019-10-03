package com.andrew.selfserviceprototype.Utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sunmi.extprinterservice.ExtPrinterService;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private ExtPrinterService printerService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceConnection serv = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                printerService = ExtPrinterService.Stub.asInterface(iBinder);
                try {
                    printerService.printerInit();
                    Log.i(TAG, "Printer Connected with Status : " + printerService.getPrinterStatus());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.e(TAG, "Printer disconnected");
            }
        };

        Intent intent = new Intent();
        intent.setPackage("com.sunmi.extprinterservice");
        intent.setAction("com.sunmi.extprinterservice.PrinterService");
        bindService(intent, serv, Context.BIND_ABOVE_CLIENT);
    }

    public ExtPrinterService getPrinterService() {
        return printerService;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Window window = getWindow();
        int flags =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                ;
        window.getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*
         * Cannot move to back activity
         * So this function should be empty
         * */
    }
}
