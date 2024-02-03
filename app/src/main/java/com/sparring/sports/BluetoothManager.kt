package com.sparring.sports


import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import android.Manifest
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast


class BluetoothManager(private val context: Context, private val listener: BluetoothListener) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothGatt: BluetoothGatt? = null
    private var scanning = false
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    private val devices = mutableListOf<BluetoothDevice>()

    interface BluetoothListener {
        fun onMessageReceived(message: String)
        fun onConnectionStateChanged(connected: Boolean)
        fun onNewDeviceDiscovered(devices: List<BluetoothDevice>)
    }

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1
        private const val REQUEST_BLUETOOTH_CONNECT_PERMISSION = 2
    }

    // Método para notificar a MainActivity sobre novos dispositivos
    private fun notifyNewDevice(device: BluetoothDevice) {
        discoveredDevices.add(device)
        listener.onNewDeviceDiscovered(discoveredDevices.toList())
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                handleBluetoothPermissionResult(grantResults)
            }
            // Adicione outros casos conforme necessário
        }
    }

    private fun handleBluetoothPermissionResult(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Permissões concedidas, você pode iniciar o scan novamente
            startScan(context as Activity)
        } else {
            // Permissões negadas, você pode tratar isso conforme necessário
            Log.e(TAG, "Bluetooth permissions denied.")
        }
    }

    fun startScan(activity: Activity) {
        Log.d(TAG, "startScan: Checking Bluetooth permissions...")

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            // Verificar e solicitar permissões em tempo de execução, se necessário
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permissão BLUETOOTH_ADMIN concedida
                Log.d(TAG, "Bluetooth permissions granted. Starting scan...")
                bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
                scanning = true
            } else {
                // Solicitar permissão BLUETOOTH_ADMIN
                Log.d(TAG, "Bluetooth permissions not granted. Requesting permissions...")
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                    REQUEST_BLUETOOTH_PERMISSION
                )
            }
        } else {
            Log.e(TAG, "BluetoothAdapter is null or Bluetooth is not enabled.")
        }
    }

    public  fun hasBluetoothPermissions(activity: Activity): Boolean {
        return (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED)
    }

     fun requestBluetoothPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN
            ),
            REQUEST_BLUETOOTH_PERMISSION
        )
    }
    fun stopScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            // Verificar se a permissão BLUETOOTH_SCAN foi concedida
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Se a permissão foi concedida, parar o scan
                bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
                scanning = false
            } else {
                // Se a permissão não foi concedida, avisar o usuário
                // (por exemplo, mostrar um Toast ou um diálogo)
                Toast.makeText(context, "Permissão de scan Bluetooth não concedida.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, "onScanResult: Device found - Name: ${result.device.name ?: "Nome Desconhecido"}, Address: ${result.device.address}")

            // Verificar se a permissão BLUETOOTH_CONNECT foi concedida
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Se a permissão foi concedida, exibir o nome do dispositivo
                Log.d(TAG, "onScanResult: Device found - Name: ${result.device.name}")
            } else {
                // Se a permissão não foi concedida, exibir uma mensagem informativa
                Log.d(TAG, "onScanResult: Nome não disponível (permissão necessária)")
            }

            // Aqui você pode processar os dispositivos encontrados durante o scan
            // Adicione a lógica para mostrar uma lista de dispositivos disponíveis
            notifyNewDevice(result.device)
        }
    }

    fun connect(device: BluetoothDevice) {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            // Verificar se a permissão BLUETOOTH_CONNECT foi concedida
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Se a permissão foi concedida, iniciar a conexão GATT
                bluetoothGatt = device.connectGatt(context, false, gattCallback)
            } else {
                // Se a permissão não foi concedida, solicitar ao usuário
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_BLUETOOTH_CONNECT_PERMISSION
                )
            }
        }
    }

    fun disconnect() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            // Verificar se a permissão BLUETOOTH foi concedida
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Se a permissão foi concedida, desconectar GATT
                bluetoothGatt?.disconnect()
                bluetoothGatt?.close()
                bluetoothGatt = null
            } else {
                // Se a permissão não foi concedida, avisar o usuário
                // (por exemplo, mostrar um Toast ou um diálogo)
                Toast.makeText(context, "Permissão Bluetooth não concedida.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                listener.onConnectionStateChanged(true)
                // Verificar se a permissão BLUETOOTH_CONNECT foi concedida
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Se a permissão foi concedida, iniciar a descoberta de serviços
                    gatt?.discoverServices()
                } else {
                    // Se a permissão não foi concedida, avisar o usuário
                    // (por exemplo, mostrar um Toast ou um diálogo)
                    Toast.makeText(context, "Permissão de conexão Bluetooth não concedida.", Toast.LENGTH_SHORT).show()
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                listener.onConnectionStateChanged(false)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.d(TAG, "onNewDeviceDiscovered: New devices discovered - Count: ${devices.size}")
            // Aqui você pode interagir com os serviços GATT
            // Adicione a lógica para enviar/receber mensagens GATT
        }

        // Adicione outros métodos para características, descritores, etc., conforme necessário
    }

    // ... Outros métodos e variáveis
}

