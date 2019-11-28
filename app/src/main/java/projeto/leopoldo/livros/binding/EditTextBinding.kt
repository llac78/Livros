package projeto.leopoldo.livros.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter

object EditTextBinding {
    @JvmStatic
    @BindingAdapter("android:text") // converter um Int para um texto que será exibido no EditText
    fun setTextFromInt(editText: EditText, value: Int) {
        if (getTextAsInt(editText) != value) {
            editText.setText(value.toString())
        }
    }
    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text") // converter o que é digitado no EditText para um Int
    fun getTextAsInt(editText: EditText): Int {
        return try {
            Integer.parseInt(editText.text.toString())
        } catch (e: Exception) {
            0
        }
    }
}
