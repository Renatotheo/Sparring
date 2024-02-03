package com.sparring.sports

import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.Manifest

class DeviceListAdapter(
    private var devices: List<BluetoothDevice>,
    private val onDeviceClickListener: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextView)
        val deviceAddressTextView: TextView = itemView.findViewById(R.id.deviceAddressTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        // Verificar se a permissão BLUETOOTH_CONNECT foi concedida
        if (ContextCompat.checkSelfPermission(
                holder.itemView.context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Se a permissão foi concedida, exibir o nome do dispositivo
            holder.deviceNameTextView.text = device.name ?: "Nome Desconhecido"
        } else {
            // Se a permissão não foi concedida, exibir uma mensagem informativa
            holder.deviceNameTextView.text = "Nome não disponível (permissão necessária)"
        }

        holder.deviceAddressTextView.text = device.address

        holder.itemView.setOnClickListener {
            // Chamar o callback quando um dispositivo for clicado
            onDeviceClickListener(device)
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }


    fun updateDevices(newDevices: List<BluetoothDevice>) {
        devices = newDevices
        notifyDataSetChanged()
    }
}
