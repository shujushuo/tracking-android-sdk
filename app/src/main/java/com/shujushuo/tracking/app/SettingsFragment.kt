package com.shujushuo.tracking.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.shujushuo.tracking.sdk.DeviceInfoManager
import com.shujushuo.tracking.sdk.TrackingSdk

class SettingsFragment : Fragment() {

    private lateinit var deviceInfoManager: DeviceInfoManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        deviceInfoManager = DeviceInfoManager()
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val brand = view.findViewById<View>(R.id.brand)
        val brand_title = brand.findViewById<TextView>(R.id.title)
        brand_title.text = "设备品牌"
        val brand_value = brand.findViewById<TextView>(R.id.value)
        brand_value.text = Build.BRAND
        brand.findViewById<Button>(R.id.copy)
            .setOnClickListener(CopyButtonClickListener(requireContext(), brand_value));


        val model = view.findViewById<View>(R.id.model)
        val model_title = model.findViewById<TextView>(R.id.title)
        model_title.text = "设备型号"
        val model_value = model.findViewById<TextView>(R.id.value)
        model_value.text = Build.MODEL
        brand.findViewById<Button>(R.id.copy)
            .setOnClickListener(CopyButtonClickListener(requireContext(), model_value));


        val version = view.findViewById<View>(R.id.version)
        val version_title = version.findViewById<TextView>(R.id.title)
        version_title.text = "系统版本"
        val version_value = version.findViewById<TextView>(R.id.value)
        version_value.text = Build.VERSION.RELEASE
        brand.findViewById<Button>(R.id.copy)
            .setOnClickListener(CopyButtonClickListener(requireContext(), version_value));


        val oaid = view.findViewById<View>(R.id.oaid)
        val oaid_title = oaid.findViewById<TextView>(R.id.title)
        oaid_title.text = "OAID"
        val oaid_value = oaid.findViewById<TextView>(R.id.value)
        oaid_value.text = TrackingSdk.deviceInfoManager.getOAID(requireContext())
        brand.findViewById<Button>(R.id.copy)
            .setOnClickListener(CopyButtonClickListener(requireContext(), oaid_value));


        val android_id = view.findViewById<View>(R.id.androidid)
        val android_id_title = android_id.findViewById<TextView>(R.id.title)
        android_id_title.text = "Android ID"
        val androidid_value = android_id.findViewById<TextView>(R.id.value)
        androidid_value.text = TrackingSdk.deviceInfoManager.getAndroidId(requireContext())
        android_id.findViewById<Button>(R.id.copy)
            .setOnClickListener(CopyButtonClickListener(requireContext(), androidid_value));

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    internal class CopyButtonClickListener(
        private val context: Context,
        private val sourceTextView: TextView
    ) :
        View.OnClickListener {
        override fun onClick(view: View) {
            // 获取剪贴板管理器
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager // 创建 ClipData
            val clip = ClipData.newPlainText("label", sourceTextView.text)
            // 设置剪贴板的主 ClipData
            clipboard.setPrimaryClip(clip)

            // 显示已复制的消息
            Toast.makeText(context.applicationContext, "已拷贝!", Toast.LENGTH_SHORT).show()
        }
    }
}