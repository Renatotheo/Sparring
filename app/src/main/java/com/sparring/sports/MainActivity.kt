package com.sparring.sports


import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sparring.sports.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BluetoothManager.BluetoothListener {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar BluetoothManager
        bluetoothManager = BluetoothManager(this)

        // Inicializar o View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

