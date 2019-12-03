package projeto.leopoldo.livros.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import projeto.leopoldo.livros.firebase.FbRepository
import projeto.leopoldo.livros.model.Book

class BookListViewModel : ViewModel() {

    private val repo = FbRepository()
    private var bookList: LiveData<List<Book>>? = null

    fun getBooks(): LiveData<List<Book>>{
        var list = bookList
        if (list == null){
            list = repo.loadBooks()
            bookList = list
        }
        return list
    }
}