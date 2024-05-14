package com.example.myapplication

import BluetoothHandler
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class BluetoothActivity : AppCompatActivity() {
    private lateinit var bluetoothHandler: BluetoothHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        bluetoothHandler = BluetoothHandler(this)
        bluetoothHandler.enableBluetooth()

        val listView: ListView = findViewById(R.id.list_view)
        listView.adapter = bluetoothHandler.arrayAdapter

        val scanButton: Button = findViewById(R.id.scan_button)
        scanButton.setOnClickListener {
            bluetoothHandler.startDiscovery()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothHandler.stopDiscovery()
    }
}
