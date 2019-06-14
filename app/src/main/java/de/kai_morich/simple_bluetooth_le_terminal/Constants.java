package de.kai_morich.simple_bluetooth_le_terminal;

import android.support.v4.app.FragmentManager;

import de.kai_morich.simple_bluetooth_le_terminal.BLE.SerialService;
import de.kai_morich.simple_bluetooth_le_terminal.BLE.SerialSocket;

public class Constants {

    // values have to be globally unique
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    public static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    public static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";

    public static boolean isBroadcast = false;
    public static String currentDeviceSelectedAddress = "";
    // values have to be unique within each app
    public static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;
    //global variables for Serial Socket and Serial Service
    public static SerialSocket socket;
    public static SerialService service;

    public static FragmentManager fragmentManager;

    //Chat related global variables
    public static String Current_Chatbox_open_name;
    public static int Current_Chatbox_open_number;
    public static String Current_User_Loged_in;
}
