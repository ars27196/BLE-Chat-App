package de.kai_morich.simple_bluetooth_le_terminal.BLE;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.kai_morich.simple_bluetooth_le_terminal.Constants;
import de.kai_morich.simple_bluetooth_le_terminal.HomeActivity;
import de.kai_morich.simple_bluetooth_le_terminal.R;

public class TerminalFragment extends Fragment implements ServiceConnection, SerialListener {

    private enum Connected {False, Pending, True}

    private String deviceAddress;
    private String newline = "\r\n";

    private TextView receiveText;

    private SerialSocket socket;
    private SerialService service;
    private boolean initialStart = true;
    private Connected connected = Connected.False;

    public TerminalFragment() {
    }

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = Constants.currentDeviceSelectedAddress;
    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            //disconnect();
            getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if (service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation")
    // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        if (initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */
    EditText username, password;
    Button login;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_log_in, container, false);
        username = view.findViewById(R.id.edit_username);
        password = view.findViewById(R.id.edit_password);
        login = view.findViewById(R.id.btn_log_in);
        login.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {

                                         Constants.Current_User_Loged_in = username.getText().toString().intern();
                                         send(username.getText().toString() + "" + password.getText().toString(), getActivity());

                                     }
;                                 });

        /*receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        TextView sendText = view.findViewById(R.id.send_text);
        View sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(v -> send(sendText.getText().toString()));*/

        return view;
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_terminal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear) {
            receiveText.setText("");
            return true;
        } else if (id ==R.id.newline) {
            String[] newlineNames = getResources().getStringArray(R.array.newline_names);
            String[] newlineValues = getResources().getStringArray(R.array.newline_values);
            int pos = java.util.Arrays.asList(newlineValues).indexOf(newline);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Newline");
            builder.setSingleChoiceItems(newlineNames, pos, (dialog, item1) -> {
                newline = newlineValues[item1];
                dialog.dismiss();
            });
            builder.create().show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }*/

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            String deviceName = device.getName() != null ? device.getName() : device.getAddress();
            status("connecting...");
            connected = Connected.Pending;
            socket = new SerialSocket();
            service.connect(this, "Connected to " + deviceName);
            socket.connect(getContext(), service, device);
            Constants.service = service;
            Constants.socket = socket;
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

   /* private void disconnect() {
        connected = Connected.False;
        if (service != null)
            service.disconnect();
        if (socket != null)
            socket.disconnect();

    }*/

    public void send(String str, Context context) {
        if (connected != Connected.True) {
            Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // receiveText.append(spn);
            byte[] data = (str + newline).getBytes();
            socket.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
//        receiveText.append(new String(data));
        String data2 = new String(data);
        if (data2 != null) {
            data2 = data2.trim().charAt(0) + "";
            if (data2.equals("1")) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
            }
        }
    }

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // receiveText.append(spn);
    }


    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        Toast.makeText(getActivity(), "connected serial", Toast.LENGTH_SHORT).show();
        connected = Connected.True;

    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        Toast.makeText(getActivity(), "connection error" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);

    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        Toast.makeText(getActivity(), "serial error", Toast.LENGTH_SHORT).show();
        //disconnect();
    }

}
