package com.timer.lab2_timer

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.timer.lab2_timer.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var itemAdapter: ItemAdapter? = null
    private var toolbar: Toolbar? = null
    private var sharedPreferences: SharedPreferences? = null
    private val mainDatabase by lazy { MainDatabase.getDatabase(this).getItemDao() }
    // The Database won't be created until we call it.


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)

        Log.d("MainActivity", "onCreate method started")
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        toolbar = findViewById<View>(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)
        toolbar?.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_settings)
        toolbar?.setNavigationOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        getSharedPreferences()

        binding.addItemButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        binding.deleteAllButton.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Are you sure to delete all timers?")
            builder.setPositiveButton("YES") { p0, p1 ->
                lifecycleScope.launch {
                    var itemList = mainDatabase.getAllItems()
                    if(itemList.isEmpty()) {
                        val new_builder = AlertDialog.Builder(this@MainActivity)
                        new_builder.setMessage("Nothing to delete...")
                        new_builder.setPositiveButton("OK") {p0, p1 ->
                            p0.dismiss()
                        }
                        val dialog = new_builder.create()
                        dialog.show()
                    }
                    else {
                        mainDatabase.deleteAllItems()
                        itemList = mainDatabase.getAllItems()
                        setAdapter(itemList)
                    }
                }
                p0.dismiss()
            }
            builder.setNegativeButton("NO") { p0, p1 ->
                p0.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun getSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val isDarkTheme = sharedPreferences?.getBoolean("theme_preference", false)

        if (isDarkTheme!!)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    private fun setAdapter(itemList: List<Item>) {
        itemAdapter?.setData(itemList)
    }


    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume method started")
        lifecycleScope.launch {
            // We are getting all our items from timer.db by Dao interface
            val itemList = mainDatabase.getAllItems()
            Log.d("MainActivity", "onResume method, itemList size: ${itemList.toList().size}")

            itemAdapter = ItemAdapter()

            // Now we need to make initialization of our recyclerView src(layoutManager, adapter)
            binding.recyclerItemView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = itemAdapter
                setAdapter(itemList)

                itemAdapter?.setOnActionUpdateListener {
                    val intent = Intent(this@MainActivity, AddItemActivity::class.java)
                    // We are using putExtra to make transfer out item data
                    intent.putExtra("ItemData", it)
                    startActivity(intent)
                }

                itemAdapter?.setOnActionDeleteListener {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setMessage("Are you sure to delete timer?")
                    builder.setPositiveButton("YES") { p0, p1 ->
                        lifecycleScope.launch {
                            mainDatabase.deleteItem(it)
                            val itemList = mainDatabase.getAllItems()
                            // Set new itemList to Adapter
                            setAdapter(itemList)
                        }
                        p0.dismiss()
                    }

                    builder.setNegativeButton("NO") { p0, p1 ->
                        p0.dismiss()
                    }

                    val dialog = builder.create()
                    dialog.show()
                }

                itemAdapter?.setOnActionPlayListener {
                    if(it.duration > 0) {
                        val intent = Intent(this@MainActivity, TimerActivity::class.java)
                        intent.putExtra("ItemData", it)
                        startActivity(intent)
                    }
                    else {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setMessage("Nothing to start...")
                        builder.setPositiveButton("OK") {p0, p1 ->
                            p0.dismiss()
                        }
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            }
        }
    }
}