package projeto.leopoldo.livros.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import projeto.leopoldo.livros.firebase.FbRepository
import projeto.leopoldo.livros.model.Book

class BookFormViewModel : ViewModel() {

    private val repo = FbRepository()
    var book: Book? = null

    private var showProgress = MutableLiveData<Boolean>().apply {
        value = false
    }

    private var saveBook = MutableLiveData<Book>()
    private var savingBookOperation = Transformations.switchMap(saveBook){book ->
        showProgress.value = true
        Transformations.map(repo.saveBook(book)){ success ->
            showProgress.value = false
            success
        }
    }

    fun showProgress(): LiveData<Boolean> = showProgress

    fun savingOperation(): LiveData<Boolean> = savingBookOperation

    fun saveBook(book: Book){
        saveBook.value = book
    }
}