package projeto.leopoldo.livros

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private var googleApiClient: GoogleApiClient? = null
    private var fbAuth = FirebaseAuth.getInstance() // associar o login do Google com o Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initGoogleSignIn()

        btnSignIn.setOnClickListener {
            signIn()
        }
    }

    // quando clicar no botão de login
    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_GOOGLE_SIGN_IN) {
            // getSignInResultFromIntent para obter o resultado do processo de autenticação
            val result =  Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess){
                // método signInAccount para obter informações da conta do usuário, caso o login tenha sucesso
                val account =  result.signInAccount
                if (account != null){
                    firebaseAuthWithGoogle(account)
                } else {
                    showErrorSignIn()
                }
            } else {
                showErrorSignIn()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential =  GoogleAuthProvider.getCredential(acct.idToken, null)
        fbAuth.signInWithCredential(credential)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    finish()
                    startActivity(Intent(this, BookListActivity::class.java))
                } else {
                    showErrorSignIn()
                }
            }
    }

    private fun initGoogleSignIn() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        // o método enableAutoManage é usado para gerenciar automaticamente a conexão com o Google Play Services
        googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this){
                showErrorSignIn()
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    private fun showErrorSignIn() {
        Toast.makeText(this, R.string.error_google_sign_in, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val RC_GOOGLE_SIGN_IN = 1
    }
}
