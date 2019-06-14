package de.kai_morich.simple_bluetooth_le_terminal;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import de.kai_morich.simple_bluetooth_le_terminal.BLE.SerialListener;
import de.kai_morich.simple_bluetooth_le_terminal.BLE.SerialService;
import de.kai_morich.simple_bluetooth_le_terminal.BLE.SerialSocket;


/**
 * A simple {@link Fragment} subclass.
 */
public class BeaconFragment extends Fragment implements ServiceConnection, SerialListener, OnMapReadyCallback {


    FragmentManager fragmentManager;

    public BeaconFragment() {
        // Required empty public constructor
        fragmentManager = Constants.fragmentManager;
    }

    TextView tv;
    String result;
    Geocoder geocoder;
    List<Address> addressList;
    private ToggleButton toggleButton;
    private TextView location_TV;
    static String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng Pakistan = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(Pakistan)
                .title("Marker in Pakistan"));

        googleMap.setMaxZoomPreference(googleMap.getMaxZoomLevel());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(Pakistan));
    }

    private enum Connected {False, Pending, True}

    private String deviceAddress;
    private String newline = "\r\n";

    private TextView receiveText;
    private FragmentActivity myContext;
    private SerialSocket socket;
    private SerialService service;
    private boolean initialStart = true;
    private Connected connected = Connected.False;

       /* SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceAddress = Constants.currentDeviceSelectedAddress;
        socket=Constants.socket;
        service=Constants.service;
    }
    @Override
    public void onDestroy() {
        if (connected != Connected.False)
//            disconnect();
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
        myContext=(FragmentActivity) activity;
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
//            getActivity().runOnUiThread(this::connect);
        }
    }

   @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        if (initialStart && isResumed()) {
            initialStart = false;
//            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beacon, container, false);
        toggleButton = view.findViewById(R.id.tb_button_beacon);

        // googleMap=view.findViewById(R.id.map);

//        geocoder = new Geocoder(getContext(), Locale.getDefault());
        tv = view.findViewById(R.id.location_beacon);

       /* SupportMapFragment mapFragment = (SupportMapFragment) myContext.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{mPermission},
                        33);

                Toast.makeText(getActivity(), "Give Permission Access", Toast.LENGTH_LONG).show();

                // If any permission above not allowed by user, this condition will
//                execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        toggleButton.setOnClickListener(new View.OnClickListener() {
            boolean toggleStatus = false;

            @Override
            public void onClick(View v) {
                if (toggleStatus == true) {

                    toggleButton.setBackgroundResource(R.drawable.disable);
                    toggleStatus = false;

                    // location_TV.setText(" ");
                }else{
                    send("^");
                    toggleButton.setBackgroundResource(R.drawable.enable);
                    toggleStatus = true;
                    send("\\");
                    getGpsLocation();

                }

            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            }
        });

        return view;
    }


    private GPSTracker gps;
    private double latitude;
    private double longitude;

    private void getGpsLocation() {

        // create class object
        gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
//            Toast.makeText(getContext(), "Longitude" + longitude + " latitude" + latitude, Toast.LENGTH_LONG).show();
            tv.setText("Longitude: " + longitude + "\nlatitude: " + latitude);
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    send("Longitude: " + longitude + "\nlatitude: "+latitude );
                }
            },5000);
                   //
        } else {
            gps.showSettingsAlert();

        }
    }

    /*
     * Serial + UI
     */

 private void send(String str) {
     if (service != null)
         service.attach(this);
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
        Toast.makeText(getActivity(), "" + data2, Toast.LENGTH_LONG).show();

        data2 = data2.trim().charAt(0) + "";
        if (data2.equals("1")) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);

            startActivity(intent);
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
//        status("connected");
        Toast.makeText(getActivity(), "connected serial", Toast.LENGTH_SHORT).show();
        connected = Connected.True;

    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        Toast.makeText(getActivity(), "connection error beacon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
//        disconnect();
    }
}
