package projeto.leopoldo.livros.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import projeto.leopoldo.livros.model.Book
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FbRepository {

    // fbAuth para obter o usuário autenticado (através de currentUser) e associá-lo ao livro
    private val fbAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance() // prover acesso ao banco de dados
    private val currentUser = fbAuth.currentUser

    // permite fazer o upload de arquivos para o Cloud Storage
    private val storageRef = FirebaseStorage.getInstance().reference.child(BOOKS_KEY)

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
                        collection.document(book.id).set(book)
                    }
                    saveTask.addOnSuccessListener {
                        // caso a propriedade coverUrl do objeto comece com 'file://', o livro está apontando para uma imagem local,
                        // sendo necessário fazer o upload
                        if(book.coverUrl.startsWith("file://")){
                            uploadFile()
                        } else {
                            value = true
                        }
                    }.addOnFailureListener {
                        value = false
                    }
            }
            fun uploadFile() {
                uploadPhoto(book).continueWithTask { urlTask ->
                    File(book.coverUrl).delete()
                    book.coverUrl = urlTask.result.toString()
                    firestore.collection(BOOKS_KEY)
                        .document(book.id)
                        .update(COVER_URL_KEY, book.coverUrl)
                }.addOnCompleteListener { task ->
                    value = task.isSuccessful
                }
            }
        }
    }

    private fun uploadPhoto(book: Book): Task<Uri> { //Task<Uri> contém URL da imagem do Firebase
        compressPhoto(book.coverUrl)
        val storageRef = storageRef.child(book.id) //aponta para a pasta no books no Firebase Storage
        // ao chamar putFile, o upload é iniciado
        return storageRef.putFile(Uri.parse(book.coverUrl)).continueWithTask { uploadTask -> // continueWithTask gera URL para a imagem
                uploadTask.result?.storage?.downloadUrl
            }
    }

    // comprimir o arquivo para diminuir o tamanho antes do upload
    private fun compressPhoto(path: String) {
        val imgFile = File(path.substringAfter("file://"))
        val bos = ByteArrayOutputStream()
        val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos)
        val fos = FileOutputStream(imgFile)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
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
                        }
                    }
            }
        }
    }

    fun remove(book: Book): LiveData<Boolean>{
        return object : LiveData<Boolean>(){
            override fun onActive() {
                super.onActive()

                val db = firestore
                db.collection(BOOKS_KEY).document(book.id).delete().continueWithTask { task ->
                  if (task.isSuccessful)
                      storageRef.child(book.id).delete()
                    else
                      throw Exception(task.exception)
                }.addOnCompleteListener {
                    value = it.isSuccessful
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