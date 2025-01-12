package com.shujushuo.tracking.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.shujushuo.tracking.app.databinding.FragmentTrackBinding
import com.shujushuo.tracking.sdk.CurrencyType
import com.shujushuo.tracking.sdk.SdkConfig
import com.shujushuo.tracking.sdk.TrackingSdk

class TrackFragment : Fragment() {
    private var binding: FragmentTrackBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appid = view.findViewById<TextView>(R.id.app_id)
        val baseUrl = view.findViewById<TextView>(R.id.baseUrl)
        val channelid = view.findViewById<TextView>(R.id.channelid)

        appid.text = requireContext().getSharedPreferences("sdk_config", Context.MODE_PRIVATE)
            .getString("appid", "APPID")

        baseUrl.text = requireContext().getSharedPreferences("sdk_config", Context.MODE_PRIVATE)
            .getString("baseUrl", "http://127.0.0.1:8090")

        channelid.text = requireContext().getSharedPreferences("sdk_config", Context.MODE_PRIVATE)
            .getString("channelid", "DEFAULT")

        view.findViewById<Button>(R.id.btn_initialize).setOnClickListener {
            requireContext().getSharedPreferences("sdk_config", Context.MODE_PRIVATE).edit()
                .putString("appid", appid.text.toString()).apply()
            requireContext().getSharedPreferences("sdk_config", Context.MODE_PRIVATE).edit()
                .putString("baseUrl", baseUrl.text.toString()).apply()
            requireContext().getSharedPreferences("sdk_config", Context.MODE_PRIVATE).edit()
                .putString("channelid", channelid.text.toString()).apply()
            TrackingSdk.initialize(
                requireActivity().application,
                SdkConfig(baseUrl.text.toString(), appid.text.toString(), channelid.text.toString())
            )
        }

        view.findViewById<Button>(R.id.btn_install)
            .setOnClickListener(TrackButtonClickListener(requireContext(), "install"))

        view.findViewById<Button>(R.id.btn_startup)
            .setOnClickListener(TrackButtonClickListener(requireContext(), "startup"))

        view.findViewById<Button>(R.id.btn_register)
            .setOnClickListener(TrackButtonClickListener(requireContext(), "register"))


        view.findViewById<Button>(R.id.btn_login)
            .setOnClickListener(TrackButtonClickListener(requireContext(), "login"))

        view.findViewById<Button>(R.id.btn_payment)
            .setOnClickListener(TrackButtonClickListener(requireContext(), "payment"))


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    internal class TrackButtonClickListener(
        private val context: Context,
        private val xwhat: String
    ) :

        View.OnClickListener {
        override fun onClick(view: View) {
            when (xwhat) {
                "install" -> TrackingSdk.trackInstall()
                "startup" -> TrackingSdk.trackStartup()
                "register" -> TrackingSdk.trackRegister("example_xwho")
                "login" -> TrackingSdk.trackLogin("example_xwho")
                "payment" -> TrackingSdk.trackPayment(
                    xwho = "example_xwho",
                    transactionid = "example_transactionid",
                    paymenttype = "wexinpay",
                    currencytype = CurrencyType.CNY,
                    currencyamount = 6f,
                    paymentstatus = true,
                )
            }
            // 显示已复制的消息
            Toast.makeText(context.applicationContext, "已发送", Toast.LENGTH_SHORT).show()
        }
    }
}