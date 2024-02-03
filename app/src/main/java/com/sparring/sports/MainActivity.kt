package com.sparring.sports



import android.bluetooth.BluetoothDevice
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sparring.sports.databinding.ActivityMainBinding
import android.Manifest
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), BluetoothManager.BluetoothListener {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var binding: ActivityMainBinding
    private var scanning = false
    private lateinit var deviceRecyclerView: RecyclerView
    private lateinit var deviceListAdapter: DeviceListAdapter
    private val REQUEST_BLUETOOTH_PERMISSION = 1



    override fun onStart() {
        super.onStart()

        if (!bluetoothManager.hasBluetoothPermissions(this)) {
            bluetoothManager.requestBluetoothPermissions(this)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar BluetoothManager
        bluetoothManager = BluetoothManager(this,this)

        // Solicitar todas as permissões necessárias explicitamente
        requestBluetoothPermissions()

        // Inicializar o View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Encontre o RecyclerView
        deviceRecyclerView = binding.deviceRecyclerView

        // Configurar o RecyclerView
        deviceListAdapter = DeviceListAdapter(emptyList()) { device ->
            // Callback chamado quando um dispositivo é clicado
            bluetoothManager.connect(device)
        }
        deviceRecyclerView.layoutManager = LinearLayoutManager(this)
        deviceRecyclerView.adapter = deviceListAdapter

        // Botões para os fragments
        binding.forcaButton.setOnClickListener {
            replaceFragment(ForcaFragment())
        }

        binding.velocidadeButton.setOnClickListener {
            replaceFragment(VelocidadeFragment())
        }

        binding.reacaoButton.setOnClickListener {
            replaceFragment(ReacaoFragment())
        }

        binding.resistenciaButton.setOnClickListener {
            replaceFragment(ResistenciaFragment())
        }

        binding.connectButton.setOnClickListener {
            if (!scanning) {
                startBluetoothScan()
            } else {
                stopBluetoothScan()
            }
        }

    }
    override fun onNewDeviceDiscovered(devices: List<BluetoothDevice>) {
        // Atualizar o adaptador com os novos dispositivos
        deviceListAdapter.updateDevices(devices)
    }


    private fun requestBluetoothPermissions() {
        val bluetoothPermissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
            // Adicione outras permissões conforme necessário
        )

        ActivityCompat.requestPermissions(
            this,
            bluetoothPermissions,
            REQUEST_BLUETOOTH_PERMISSION
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        bluetoothManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startBluetoothScan() {
        if (bluetoothManager.hasBluetoothPermissions(this)) {
            bluetoothManager.startScan(this)
            scanning = true
            binding.connectButton.text = "Parar Scan"
        } else {
            Log.e(TAG, "Bluetooth permissions not granted.")
        }
    }

    private fun stopBluetoothScan() {
        bluetoothManager.stopScan()
        scanning = false
        binding.connectButton.text = "Conectar Bluetooth"
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onMessageReceived(message: String) {
        // Implementar a lógica para lidar com mensagens recebidas
    }

    override fun onConnectionStateChanged(connected: Boolean) {
        // Implementar a lógica para lidar com alterações no estado de conexão
    }

    // Métodos adicionais conforme necessário
}

