import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Handler
import android.os.Looper


class BluetoothHandler(private val context: AppCompatActivity) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_DISCOVERY = 2
    val devices = mutableListOf<BluetoothDevice>()
    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, devices)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    devices.add(it)
                    arrayAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    // Créez une instance de ActivityResultLauncher
    private val enableBluetoothLauncher = context.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Le Bluetooth a été activé
            Toast.makeText(context, "Bluetooth activé avec succès", Toast.LENGTH_SHORT).show()
        } else {
            // L'utilisateur a refusé d'activer le Bluetooth
            Toast.makeText(context, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
        }
    }

    fun enableBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth non pris en charge", Toast.LENGTH_SHORT).show()
        } else if (!bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBluetoothIntent)
        } else {
            Toast.makeText(context, "Bluetooth activé avec succès", Toast.LENGTH_SHORT).show()
        }
    }

    fun startDiscovery() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN), REQUEST_DISCOVERY)
        } else {
            val discoverDevicesIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
            context.registerReceiver(receiver, discoverDevicesIntent)
            bluetoothAdapter?.startDiscovery()

            // Ajoutez un délai pour permettre la découverte des appareils
            Handler(Looper.getMainLooper()).postDelayed({
                if (devices.isEmpty()) {
                    Toast.makeText(context, "Aucun appareil trouvé", Toast.LENGTH_SHORT).show()
                }
            }, 10000) // Délai de 10 secondes
        }
    }

    fun stopDiscovery() {
        context.unregisterReceiver(receiver)
    }
}
