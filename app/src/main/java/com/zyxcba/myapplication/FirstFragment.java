package com.zyxcba.myapplication;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.zyxcba.myapplication.databinding.FragmentFirstBinding;
import com.zyxcba.mylibrary.AnalysisAAABBB;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AnalysisAAABBB.init(getContext());
        AnalysisAAABBB.start("appidaaabbbccc", "channelaaabbbccc");

        View brand = view.findViewById(R.id.brand);
        TextView brand_title = brand.findViewById(R.id.title);
        brand_title.setText("设备品牌");
        TextView brand_value = brand.findViewById(R.id.value);
        brand_value.setText(Build.BRAND);
        brand.findViewById(R.id.copy).setOnClickListener(new CopyButtonClickListener(getContext(), brand_value));

        View model = view.findViewById(R.id.model);
        TextView model_title = model.findViewById(R.id.title);
        model_title.setText("设备型号");
        TextView model_value = model.findViewById(R.id.value);
        model_value.setText(Build.MODEL);
        model.findViewById(R.id.copy).setOnClickListener(new CopyButtonClickListener(getContext(), model_value));


        View version = view.findViewById(R.id.version);
        TextView version_title = version.findViewById(R.id.title);
        version_title.setText("系统版本");
        TextView version_value = version.findViewById(R.id.value);
        version_value.setText(Build.VERSION.RELEASE);
        version.findViewById(R.id.copy).setOnClickListener(new CopyButtonClickListener(getContext(), version_value));


        View oaid = view.findViewById(R.id.oaid);
        TextView oaid_title = oaid.findViewById(R.id.title);
        oaid_title.setText("OAID");
        TextView oaid_value = oaid.findViewById(R.id.value);
        oaid_value.setText(AnalysisAAABBB.getOAID(getContext()));
        oaid.findViewById(R.id.copy).setOnClickListener(new CopyButtonClickListener(getContext(), oaid_value));


        View android_id = view.findViewById(R.id.androidid);
        TextView android_id_title = android_id.findViewById(R.id.title);
        android_id_title.setText("Android ID");
        TextView androidid_value = android_id.findViewById(R.id.value);
        androidid_value.setText(AnalysisAAABBB.getAndroidId(getContext()));
        android_id.findViewById(R.id.copy).setOnClickListener(new CopyButtonClickListener(getContext(), androidid_value));

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    static class CopyButtonClickListener implements View.OnClickListener {
        private TextView sourceTextView;
        private Context context;

        public CopyButtonClickListener(Context context, TextView sourceTextView) {
            this.context = context;
            this.sourceTextView = sourceTextView;
        }

        @Override
        public void onClick(View view) {
            // 获取剪贴板管理器
            ClipboardManager clipboard = (ClipboardManager) this.context.getSystemService(Context.CLIPBOARD_SERVICE);                // 创建 ClipData
            ClipData clip = ClipData.newPlainText("label", sourceTextView.getText());
            // 设置剪贴板的主 ClipData
            clipboard.setPrimaryClip(clip);
            // 显示已复制的消息

            Toast.makeText(context.getApplicationContext(), "已拷贝!", Toast.LENGTH_SHORT).show();

        }
    }

}