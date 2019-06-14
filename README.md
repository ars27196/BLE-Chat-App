# Adnroid Chat Application based on BLE

This Android app provides Chat application which communicates with other devices using Bluetooth Low Energy (BLE) module attached with the Arduino (works as server for exchange of messages)

For an overview on Android BLE communication see 
[Android Bluetooth LE Overview](https://developer.android.com/guide/topics/connectivity/bluetooth-le).

In contrast to classic Bluetooth, there is no predefined serial profile for Bluetooth LE, 
so each vendor uses GATT services with different service and characteristic UUIDs.

This app includes UUIDs for widely used serial profiles:
- Nordic Semiconductor nRF51822  
- Texas Instruments CC254x  

All BLE communication code is taken from the kai morich project on BLE Terminal (Link Below) and chat application is built upon it.
https://github.com/kai-morich/SimpleBluetoothLeTerminal [Simple BluetoothLe Terminal]

## Motivation

Quid-e-Azam :-)
