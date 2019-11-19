package projeto.leopoldo.livros

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.parceler.Parcels
import projeto.leopoldo.livros.databinding.ActivityBookDetailsBinding
import projeto.leopoldo.livros.model.Book

class BookDetailsActivity : AppCompatActivity() {

    private val binding: ActivityBookDetailsBinding by lazy {
        DataBindingUtil.setContentView<ActivityBookDetailsBinding>(
            this, R.layout.activity_book_details
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val book = Parcels.unwrap<Book>(intent.getParcelableExtra(EXTRA_BOOK))
        if (book != null) {
            binding.book = book
        }
    }

    companion object {
        private const val EXTRA_BOOK = "book"

        fun start(context: Context, book: Book) {
            context.startActivity(
                Intent(context, BookDetailsActivity::class.java).apply {
                    putExtra(EXTRA_BOOK, Parcels.wrap(book))
                }
            )
        }
    }
}
