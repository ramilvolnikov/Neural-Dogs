package com.example.retrofitaplication

import Static_classification.Classify.BREED
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.retrofitaplication.repository.Repository
import com.example.retrofitaplication.util.Constants.Companion.BREED_IDS
import com.example.retrofitaplication.view_model.MainViewModel
import com.example.retrofitaplication.view_model.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_info.*
import org.tensorflow.lite.examples.classification.R


class Info : AppCompatActivity() {
    lateinit var viewModel : MainViewModel
    val APP_PREFERENCES = "mysettings"
    lateinit var mSettings : SharedPreferences
    val SP_KEY_FIRST_START = "spKeyFirstStart"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (mSettings.getBoolean(SP_KEY_FIRST_START,true)) {
            putPairs()
            mSettings.edit().putBoolean(SP_KEY_FIRST_START,false).apply()
        }
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)
        viewModel.myResponse.observe(this, Observer{ response ->
            if (response.isSuccessful) {
                breeds_names.text = response.body()?.get(0)?.breeds?.get(0)?.name
                bred_for.text = response.body()?.get(0)?.breeds?.get(0)?.bred_for
                breed_group.text = response.body()?.get(0)?.breeds?.get(0)?.breed_group
                origin.text = response.body()?.get(0)?.breeds?.get(0)?.origin
                temperament.text = response.body()?.get(0)?.breeds?.get(0)?.temperament
                life_span.text = response.body()?.get(0)?.breeds?.get(0)?.life_span
                height.text = (response.body()?.get(0)?.breeds?.get(0)?.height?.metric + " cm")
                weight.text = (response.body()?.get(0)?.breeds?.get(0)?.weight?.metric + " kgs")

                Glide.with(this).load(response.body()?.get(0)?.url).into(internet_image)
            } else {
                Log.d("Response", response.errorBody().toString())
            }
        })
        showInfo()
    }




    fun showInfo()
    {
        val tmp = intent.getStringExtra(BREED);
        BREED_IDS = mSettings!!.getInt(tmp.toString(),0)
        if (BREED_IDS != 0)
            if (isNetworkAvailable(this)) {
                viewModel.getDogs(BREED_IDS)
                internet_image.visibility = View.VISIBLE
            }else {
                Toast.makeText(applicationContext, "Please, check your connection!", Toast.LENGTH_SHORT)
                    .show()
            }
        else{
            val showInBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$tmp"))
            startActivity(showInBrowser)
            finish()
        }

    }



    fun putPairs() {

        val editor = mSettings!!.edit()

        editor.putInt("Chihuahua", 0)
        editor.putInt("Japanese Spaniel", 140)
        editor.putInt("Maltese Dog", 161)
        editor.putInt("Pekinese", 183)
        editor.putInt("Shih-Tzu", 223)
        editor.putInt("Blenheim Spaniel", 71)
        editor.putInt("Papillon", 181)
        editor.putInt("Toy-terrier", 105)
        editor.putInt("Rhodesian-ridgeback", 209)
        editor.putInt("Afghan-hound", 2)
        editor.putInt("Basset", 30)
        editor.putInt("Beagle", 31)
        editor.putInt("Bloodhound", 45)
        editor.putInt("Bluetick", 47)
        editor.putInt("Black and tan coonhound", 43)
        editor.putInt("Walker hound", 250)
        editor.putInt("English foxhound", 14)
        editor.putInt("Redbone", 208)
        editor.putInt("Borzoi", 0)
        editor.putInt("Irish wolfhound", 137)
        editor.putInt("Italian greyhound", 138)
        editor.putInt("Whippet", 257)
        editor.putInt("Ibizan hound", 0)
        editor.putInt("Norwegian elkhound", 0)
        editor.putInt("Otterhound", 0)
        editor.putInt("Saluki", 213)
        editor.putInt("Scottish deerhound", 218)
        editor.putInt("Weimaraner", 253)
        editor.putInt("Staffordshire bullterrier", 238)
        editor.putInt("American Staffordshire terrier", 16)
        editor.putInt("Bedlington terrier", 34)
        editor.putInt("Border terrier", 51)
        editor.putInt("Kerry blue terrier", 0)
        editor.putInt("Irish terrier", 135)
        editor.putInt("Norfolk terrier", 172)
        editor.putInt("Norwich terrier", 176)
        editor.putInt("Yorkshire terrier", 264)
        editor.putInt("Wirehaired fox terrier", 259)
        editor.putInt("Lakeland terrier", 0)
        editor.putInt("Sealyham terrier", 0)
        editor.putInt("Airedale", 4)
        editor.putInt("Cairn", 65)
        editor.putInt("Australian terrier", 24)
        editor.putInt("Dandie Dinmont", 0)
        editor.putInt("Boston bull", 53)
        editor.putInt("Miniature schnauzer", 168)
        editor.putInt("Giant schnauzer", 119)
        editor.putInt("Standard schnauzer", 239)
        editor.putInt("Scotch terrier", 219)
        editor.putInt("Tibetan terrier", 246)
        editor.putInt("Silky terrier", 228)
        editor.putInt("Soft-coated wheaten terrier", 233)
        editor.putInt("West Highland white terrier", 256)
        editor.putInt("Lhasa", 156)
        editor.putInt("Flat-coated retriever", 0)
        editor.putInt("Curly-coated retriever", 0)
        editor.putInt("Golden retriever", 121)
        editor.putInt("Labrador retriever", 149)
        editor.putInt("Chesapeake Bay retriever", 76)
        editor.putInt("German short-haired pointer", 116)
        editor.putInt("Vizsla", 251)
        editor.putInt("English setter", 101)
        editor.putInt("Irish setter", 134)
        editor.putInt("Gordon setter", 123)
        editor.putInt("Brittany spaniel", 0)
        editor.putInt("Clumber", 84)
        editor.putInt("English springer", 103)
        editor.putInt("Welsh springer spaniel", 254)
        editor.putInt("Cocker spaniel", 86)
        editor.putInt("Sussex spaniel", 0)
        editor.putInt("Irish water spaniel", 0)
        editor.putInt("Kuvasz", 147)
        editor.putInt("Schipperke", 0)
        editor.putInt("Groenendael", 0)
        editor.putInt("Malinois", 36)
        editor.putInt("Briard", 58)
        editor.putInt("Kelpie", 22)
        editor.putInt("Komondor", 144)
        editor.putInt("Old English sheepdog", 178)
        editor.putInt("Shetland sheepdog", 221)
        editor.putInt("Collie", 32)
        editor.putInt("Border collie", 50)
        editor.putInt("Bouvier des Flandres", 54)
        editor.putInt("Rottweiler", 210)
        editor.putInt("German shepherd", 115)
        editor.putInt("Doberman", 94)
        editor.putInt("Miniature pinscher", 167)
        editor.putInt("Greater Swiss Mountain dog", 0)
        editor.putInt("Bernese mountain dog", 41)
        editor.putInt("Appenzeller", 19)
        editor.putInt("EntleBucher", 0)
        editor.putInt("Boxer", 55)
        editor.putInt("Bull mastiff", 64)
        editor.putInt("Tibetan mastiff", 244)
        editor.putInt("French bulldog", 113)
        editor.putInt("Great Dane", 124)
        editor.putInt("Saint Bernard", 212)
        editor.putInt("Eskimo dog", 12)
        editor.putInt("Malamute", 9)
        editor.putInt("Siberian husky", 226)
        editor.putInt("Affenpinscher", 1)
        editor.putInt("Basenji", 28)
        editor.putInt("Pug", 201)
        editor.putInt("Leonberg", 155)
        editor.putInt("Newfoundland", 171)
        editor.putInt("Great Pyrenees", 125)
        editor.putInt("Samoyed", 214)
        editor.putInt("Pomeranian", 193)
        editor.putInt("Chow", 81)
        editor.putInt("Keeshond", 142)
        editor.putInt("Brabancon griffon", 0)
        editor.putInt("Pembroke", 184)
        editor.putInt("Cardigan", 68)
        editor.putInt("Toy poodle", 197)
        editor.putInt("Miniature poodle", 196)
        editor.putInt("Standard poodle", 0)
        editor.putInt("Mexican hairless", 262)
        editor.putInt("Dingo", 0)
        editor.putInt("Dhole", 0)
        editor.putInt("African hunting dog", 3)

        editor.apply()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}