package de.kai_morich.simple_bluetooth_le_terminal.chat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.kai_morich.simple_bluetooth_le_terminal.Constants;
import de.kai_morich.simple_bluetooth_le_terminal.R;
import de.kai_morich.simple_bluetooth_le_terminal.SerialListener;
import de.kai_morich.simple_bluetooth_le_terminal.SerialService;
import de.kai_morich.simple_bluetooth_le_terminal.SerialSocket;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements ChatAdapter.onMyClickListener, ServiceConnection, SerialListener {

    private enum Connected {False, Pending, True}

    private String deviceAddress;
    private String newline = "\r\n";

    private TextView receiveText;

    private SerialSocket socket;
    private SerialService service;
    private boolean initialStart = true;
    private ChatFragment.Connected connected = ChatFragment.Connected.False;

    List listOfFriends;
    Button btnFindFriend;
    RecyclerView recyclerView;
    private String Tag = "check";

    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listOfFriends = new ArrayList();
        deviceAddress = Constants.currentDeviceSelectedAddress;
        socket = Constants.socket;
        service = Constants.service;


    }

  /*  @Override
    public void onDestroy() {
        if (connected != Connected.False)
//            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }*/

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    /*@Override
    public void onStop() {
        if (service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }*/

    /*  @SuppressWarnings("deprecation")
      // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
      @Override
      public void onAttach(Activity activity) {
          super.onAttach(activity);
          getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
      }
  */
   /* @Override
    public void onDetach() {
        try {
            getActivity().unbindService(this);
        } catch (Exception ignored) {
        }
        super.onDetach();
    }
*/
  /*  @Override
    public void onResume() {
        super.onResume();
        if (initialStart && service != null) {
            initialStart = false;
//            getActivity().runOnUiThread(this::connect);
        }
    }*/

    /*
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        if (initialStart) {
            initialStart = false;
//            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*

        /*
         * UI
         */
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_chat);
        btnFindFriend = view.findViewById(R.id.btn_find_friend);
        progressBar = view.findViewById(R.id.progress_bar);
        btnFindFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFindFriend.setVisibility(View.GONE);
                send("|");
                progressBar.setVisibility(View.VISIBLE);


            }
        });
        return view;
    }

    public void recyclerViewVisible() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ChatAdapter(getContext(), (ArrayList) listOfFriends, (position) -> onClickListener(position)));
    }

    @Override
    public void onClickListener(int position) {
        Toast.makeText(getActivity(), ""+Constants.Current_User_Loged_in+"has clicked " + listOfFriends.get(position).toString(), Toast.LENGTH_SHORT).show();
        Constants.Current_Chatbox_open_number = position;
        Constants.Current_Chatbox_open_name = listOfFriends.get(position).toString().replace("\n","");
        Intent in = new Intent(getActivity(), HolderActivity.class);
        Constants.isBroadcast = false;
        startActivity(in);

    }

    /*
     * Serial + UI
     */
  /*  private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            String deviceName = device.getName() != null ? device.getName() : device.getAddress();
            status("connecting...");
            connected = Connected.Pending;
            socket = new SerialSocket();
            service.connect(this, "Connected to " + deviceName);
            socket.connect(getContext(), service, device);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        if (service != null)
            service.disconnect();
        if (socket != null)
            socket.disconnect();

    }*/

    public void send(String str) {
       /* if (connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }*/
        try {
            SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // receiveText.append(spn);
            byte[] data = (str + newline).getBytes();
            socket.write(data);
            if (service != null)
                service.attach(this);

        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] data) {
//        receiveText.append(new String(data));
        String data2 = new String(data);

        String[] listOfPrivateFriends =data2.split("&".replace("\n",""));
//        Toast.makeText(getActivity(), "" + data2, Toast.LENGTH_LONG).show();
        listOfFriends.addAll(Arrays.asList(listOfPrivateFriends));
        if (listOfFriends != null) {
            recyclerViewVisible();
            progressBar.setVisibility(View.GONE);
        }
    }

   /* private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // receiveText.append(spn);
    }
*/

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
//        status("connected");
        Toast.makeText(getActivity(), "connected serial", Toast.LENGTH_SHORT).show();
        connected = Connected.True;

    }

    @Override
    public void onSerialConnectError(Exception e) {
//        status("connection failed: " + e.getMessage());
        Toast.makeText(getActivity(), "connection error" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);

    }


    @Override
    public void onSerialIoError(Exception e) {
//        status("connection lost: " + e.getMessage());
        Toast.makeText(getActivity(), "serial error", Toast.LENGTH_SHORT).show();
//        disconnect();
    }

}
