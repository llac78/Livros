package projeto.leopoldo.livros.firebase

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import projeto.leopoldo.livros.model.Book
import java.lang.Exception

class FbRepository {

    // fbAuth para obter o usuário autenticado (através de currentUser) e associá-lo ao livro
    private val fbAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance() // prover acesso ao banco de dados
    private val currentUser = fbAuth.currentUser

    fun saveBook(book: Book): LiveData<Boolean>{
        return object : LiveData<Boolean>(){
            override fun onActive() {
                super.onActive()
                if (currentUser == null){
                    throw SecurityException("Invalid user")
                }
                val db = firestore
                val collection = db.collection(BOOKS_KEY)
                val saveTask =
                    if (book.id.isBlank()){
                        book.userId = currentUser.uid
                        collection.add(book).continueWith { task ->
                            if (task.isSuccessful){
                                book.id = task.result?.id ?: ""
                            }
                        }
                    } else {
                        collection.document(book.id).set(book, SetOptions.merge())
                    }
                    saveTask.addOnSuccessListener {
                        value = true
                    }.addOnFailureListener {
                        value = false
                    }
            }
        }
    }

    fun loadBooks(): LiveData<List<Book>>{
        return object : LiveData<List<Book>>(){
            override fun onActive() {
                super.onActive()

                firestore.collection(BOOKS_KEY)
                    .whereEqualTo(USER_ID_KEY, currentUser?.uid)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException == null){
                            val books = querySnapshot?.map { document ->
                                val book = document.toObject(Book::class.java)
                                book.id = document.id
                                book
                            }
                            value = books
                        } else {
                            throw firebaseFirestoreException
                        }
                    }
            }
        }
    }

    fun loadBook(bookId: String): LiveData<Book> {
        return object : LiveData<Book>() {
            override fun onActive() {
                super.onActive()
                firestore.collection(BOOKS_KEY)
                    .document(bookId)
                    .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException == null) {
                            if (documentSnapshot != null) {
                                val book = documentSnapshot.toObject(Book::class.java)
                                book?.id = documentSnapshot.id
                                value = book
                            }
                        } else {
                            throw firebaseFirestoreException
                        }
                    }
            }
        }
    }

    companion object {
        const val BOOKS_KEY = "books"
        const val USER_ID_KEY = "userId"
        const val COVER_URL_KEY = "coverUrl"
    }
}