package com.pardawala.aliasgar.library.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pardawala.aliasgar.library.R
import com.pardawala.aliasgar.library.db.BookDao
import com.pardawala.aliasgar.library.db.BookDatabase
import kotlinx.android.synthetic.main.activity_main.*
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.first

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var useremail: String
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    @InternalCoroutinesApi
    lateinit var db : BookDao
    private var dataStore : DataStore<Preferences> = createDataStore(name = "last")
    var loggedInState = false
    private val AUTH_CODE = 121

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = BookDatabase.getInstance(this).bookDao()
        auth = FirebaseAuth.getInstance()
//        Log.d("LogIn", auth.currentUser.displayName.toString())
        loggedInState = auth.currentUser != null

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        if (!loggedInState) login()



        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        setupActionBarWithNavController(navController)


        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.addFragment -> {
                    try {
                        val action =
                            BookListFragmentDirections.actionBookListFragmentToAddFragment()
                        navController.navigate(action)
                    } catch (e: Exception) {
                        val action = FavouritesFragmentDirections.actionFavouritesFragmentToAddFragment()
                        navController.navigate(action)

                    }
                }
                R.id.favouritesFragment -> {
                    try {
                        val action =
                            BookListFragmentDirections.actionBookListFragmentToFavouritesFragment()
                        navController.navigate(action)
                    }catch (e:Exception) {
                        val action =
                            AddFragmentDirections.actionAddFragmentToFavouritesFragment()
                        navController.navigate(action)
                    }
                }
                R.id.bookListFragment -> {
                    try {
                        val action =
                            FavouritesFragmentDirections.actionFavouritesFragmentToBookListFragment()
                        navController.navigate(action)
                    }catch (e: Exception) {
                        val action =
                            AddFragmentDirections.actionAddFragmentToBookListFragment()
                        navController.navigate(action)
                    }
                }
            }
            true
        }
    }

    suspend fun save(key : String, value : String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    suspend fun read(key : String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    fun login() {
        if(loggedInState) {
            try {
                mGoogleSignInClient.signOut()
                auth.signOut()
                loggedInState = false
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show()
            } catch (e:Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        else {
            mGoogleSignInClient.signInIntent.also {
                startActivityForResult(it, AUTH_CODE)
            }
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        try {
            auth.signInWithCredential(credentials)
            loggedInState = true
        }
        catch (e:Exception){
            Log.d("QR", e.message.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AUTH_CODE) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
            account?.let {
                useremail = account.email.toString()
                googleAuthForFirebase(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun hasInternerConnection() : Boolean {
        val connectivityManager = applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return  when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}