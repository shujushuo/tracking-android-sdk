package com.shujushuo.tracking.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shujushuo.tracking.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    // 使用 by viewModels() 引用 AndroidViewModel
    private val viewModel: SettingsViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 更新每个 InfoCardView 使用 ViewModel 数据
        updateDeviceInfo(
            binding.brandCard,
            "设备品牌",
            viewModel.brand
        )

        updateDeviceInfo(
            binding.modelCard,
            "设备型号",
            viewModel.model
        )

        updateDeviceInfo(
            binding.versionCard,
            "系统版本",
            viewModel.version
        )

        updateDeviceInfo(
            binding.oaidCard,
            "OAID ID",
            viewModel.oaid
        )

        updateDeviceInfo(
            binding.androididCard,
            "Android ID",
            viewModel.androidId
        )

        updateDeviceInfo(
            binding.installidCard,
            "Install ID",
            viewModel.installId
        )
    }

    private fun updateDeviceInfo(infoCard: InfoCardView, hint: String, value: String?) {
        infoCard.setInfo(hint, value)
        Log.d("SettingsFragment", "Set $hint with value: $value")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
