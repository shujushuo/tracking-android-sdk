package com.shujushuo.tracking.app

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.shujushuo.tracking.app.databinding.InfoCardBinding
import android.util.Log

class InfoCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: InfoCardBinding

    init {
        orientation = HORIZONTAL
        // 正确调用 inflate 方法，仅传入 LayoutInflater 和 父 ViewGroup，并设置 attachToParent 为 true
        binding = InfoCardBinding.inflate(LayoutInflater.from(context), this, true)

        // 设置复制按钮的点击监听器
        binding.copy.setOnClickListener {
            val text = binding.value.text.toString()
            if (text.isNotEmpty()) {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("label", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "已拷贝!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "没有内容可拷贝", Toast.LENGTH_SHORT).show()
            }
        }

        // 设置 EditText 为不可编辑但可选择
        binding.value.apply {
            keyListener = null
            isFocusable = false
            isFocusableInTouchMode = false
            isLongClickable = false
            isCursorVisible = false
            setTextIsSelectable(true)
        }
    }

    /**
     * 设置标题和内容
     */
    fun setInfo(title: String, value: String?) {
        binding.title.hint = title
        binding.value.setText(value)

        Log.d("InfoCardView", "SetInfo $binding.title called with title: $title, value: $value")
    }
}
