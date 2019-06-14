package de.kai_morich.simple_bluetooth_le_terminal.chat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kai_morich.simple_bluetooth_le_terminal.Constants;
import de.kai_morich.simple_bluetooth_le_terminal.GPSTracker;
import de.kai_morich.simple_bluetooth_le_terminal.R;
import de.kai_morich.simple_bluetooth_le_terminal.SerialListener;
import de.kai_morich.simple_bluetooth_le_terminal.SerialService;
import de.kai_morich.simple_bluetooth_le_terminal.SerialSocket;
import de.kai_morich.simple_bluetooth_le_terminal.database.ChatDatabase;
import de.kai_morich.simple_bluetooth_le_terminal.database.ChatMessage;

import static android.media.CamcorderProfile.get;

public class HolderActivity extends AppCompatActivity implements ServiceConnection, SerialListener {

    private String deviceAddress;
    private String newline = "\r\n";
    private String chatOpenedOf,userLogedIn;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private SerialSocket socket;
    private SerialService service;
    private boolean initialStart = true;
    private List<ChatMessage> messageList;
    private EditText newMessage;
    private Button send, startChat, location;
    private ChatDatabase database;
    private List<ChatMessage> chatMessageList;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);

        deviceAddress = Constants.currentDeviceSelectedAddress;
        socket = Constants.socket;
        service = Constants.service;

        chatOpenedOf = Constants.Current_Chatbox_open_name;
        userLogedIn=Constants.Current_User_Loged_in;
        messageList = new ArrayList<>();

        newMessage = findViewById(R.id.edittext_chatbox);
        send = findViewById(R.id.button_chatbox_send);
        location = findViewById(R.id.button_chatbox_location);
        startChat = findViewById(R.id.startChat);
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        toolbar=findViewById(R.id.toolbar_holder);
        toolbar.setTitle(Constants.Current_Chatbox_open_name);
        database=ChatDatabase.getInstance(getApplicationContext());
        //get location
        getGpsLocation();

        if (Constants.isBroadcast)
            startChat.setText("Start Broadcast");
        else
            startChat.setText("Start Chat");


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localMessage=("Longitude: " + longitude + "\nlatitude: " + latitude);
                ChatMessage chatMessage = new ChatMessage(localMessage,getCurrentTime(), Constants.Current_User_Loged_in, "BroadCast");
                messageList.add(chatMessage);
                send(localMessage);
                updateRecyclerView();
            }
        });
        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.isBroadcast)
                    send("/");
                else
                    send(chatOpenedOf);
                updateDatabaseHistory();
                startChat.setVisibility(View.GONE);
            }
        });


        /* ChatViewModel viewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        viewModel.getTask().observe(HolderActivity.this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessages) {

                updateRecyclerView();
            }
        });*/

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String localMessage = newMessage.getText().toString();
                if(!localMessage.equals("")) {
                    ChatMessage chatMessage = new ChatMessage(localMessage, getCurrentTime(), userLogedIn, chatOpenedOf);
                    messageList.add(chatMessage);
                    database.chatDao().insertTask(chatMessage);
                    send(localMessage);
                    newMessage.setText("");
                    updateRecyclerView();
                }
            }
        });

    }

    private String getCurrentTime() {
        GregorianCalendar calendar = new GregorianCalendar();
        String h =calendar.get(Calendar.HOUR) + ":";
        String m =calendar.get(Calendar.MINUTE) + ":";
        String s =calendar.get(Calendar.SECOND) + ":";
        return h+""+m+""+s;
    }

    private void updateDatabaseHistory() {
        //accessing all previous messages from the data base
        chatMessageList=database.chatDao().loadChatHistory();
        for(ChatMessage message:chatMessageList){
            if(message.getSender().equals(chatOpenedOf)&&message.getReceiver().equals(userLogedIn)){
                messageList.add(message);
            }
            if(message.getSender().equals(userLogedIn)&&message.getReceiver().equals(chatOpenedOf)){
                messageList.add(message);

            }
        }
        updateRecyclerView();
    }

    private void updateRecyclerView() {

        mMessageAdapter = new MessageListAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        else
            startService(new Intent(this, SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change


    }

    private GPSTracker gps;
    private double latitude;
    private double longitude;

    private void getGpsLocation() {

        // create class object
        gps = new GPSTracker(this);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
//            Toast.makeText(this, "Longitude" + longitude + " latitude" + latitude, Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();

        }
    }

    private void send(String str) {
        try {
            SpannableStringBuilder spn = new SpannableStringBuilder(str);
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
        String data2 = new String(data).trim();
        if(!data2.equals("")) {
            ChatMessage chatMessage = new ChatMessage(data2, getCurrentTime(), chatOpenedOf, userLogedIn);
            messageList.add(chatMessage);
        database.chatDao().insertTask(chatMessage);
            updateRecyclerView();
        }
//        Toast.makeText(this, data2, Toast.LENGTH_LONG).show();

    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        if (initialStart) {
            initialStart = false;
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }


    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        Toast.makeText(this, "connected serial", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        Toast.makeText(this, "connection error" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);

    }

    @Override
    public void onSerialIoError(Exception e) {
        Toast.makeText(this, "serial error", Toast.LENGTH_SHORT).show();
//        disconnect();
    }
}
