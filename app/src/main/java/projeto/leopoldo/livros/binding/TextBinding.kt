package projeto.leopoldo.livros.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import projeto.leopoldo.livros.R
import projeto.leopoldo.livros.model.MediaType

object TextBinding {

    // este método converte um MediaType para algo que será utilizado em um TextView
    @JvmStatic
    @BindingAdapter("android:text") // componente  da TextView que será afetado
    fun setMediaTypeText(textView: TextView, mediaType: MediaType?){
        if(mediaType == null){
            textView.text = null
            return
        }

        val context = textView.context
        textView.text = when(mediaType){
            MediaType.EBOOK -> context.getString(R.string.text_book_media_ebook)
            MediaType.PAPER -> context.getString(R.string.text_book_media_paper)
        }
    }


}