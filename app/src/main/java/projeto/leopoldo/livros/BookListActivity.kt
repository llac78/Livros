package projeto.leopoldo.livros

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_book_list.*
import projeto.leopoldo.livros.model.Book
import projeto.leopoldo.livros.model.MediaType
import projeto.leopoldo.livros.model.Publisher

class BookListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        val books = listOf(
            Book().apply{
                id = "1"
                title = "Dominando o Android com Kotlin"
                author = "Nelson Glauber"
                coverUrl = "https://s3.novatec.com.br/capas-ampliadas/capa-ampliada-9788575224632.jpg"
                pages = 954
                year = 2018
                publisher =
                    Publisher("1", "Novatec")
                available = true
                mediaType = MediaType.EBOOK
                rating = 5.0f

            }
        )
        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.adapter = BookAdapter(books) { book ->
            BookDetailsActivity.start(this, book)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.book_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_sign_out){
            FirebaseAuth.getInstance().signOut()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun init() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
