package com.timer.lab2_timer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timer.lab2_timer.databinding.ActivityAddItemBinding
import kotlinx.coroutines.launch

class AddItemActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddItemBinding
    private var item: Item? = null
    private var phaseAdapter: PhaseAdapter? = null
    private val mainDatabase by lazy { MainDatabase.getDatabase(this).getItemDao() }
    val secDatabase by lazy { MainDatabase.getDatabase(this).getPhaseDao() }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Now we can get our extra data(item) from MainActivity and return it to item
        item = intent.getSerializableExtra("ItemData") as Item?

        // If we haven't got any extra data -> Add item
        if(item == null) {
            binding.addOrUpdateItem.text = "Add timer"
            binding.recyclerPhaseView.isEnabled = false
            binding.deleteAllPhases.isEnabled = false
            binding.addPhase.isEnabled = false

            lifecycleScope.launch {

            }
        }
        // Else -> Update item
        else {
            binding.addOrUpdateItem.text = "Update"
            binding.editName.setText(item?.name.toString())
            binding.editDuration.setText(item?.duration.toString())
            binding.editColor.setText(item?.color.toString())

            binding.addPhase.setOnClickListener {
                val intent = Intent(this, AddPhaseActivity::class.java)
                intent.putExtra("ItemData", item)
                startActivity(intent)
            }

            binding.deleteAllPhases.setOnClickListener {
                val builder = AlertDialog.Builder(this@AddItemActivity)
                builder.setMessage("Are you sure to delete all phases?")
                builder.setPositiveButton("YES") { p0, p1 ->
                    lifecycleScope.launch {
                        var phaseList = secDatabase.getAllPhases()
                        if(phaseList.isEmpty()) {
                            val new_builder = AlertDialog.Builder(this@AddItemActivity)
                            new_builder.setMessage("Nothing to delete...")
                            new_builder.setPositiveButton("OK") {p0, p1 ->
                                p0.dismiss()
                            }
                            val dialog = new_builder.create()
                            dialog.show()
                        }
                        else {
                            secDatabase.deleteAllPhases()
                            phaseList = secDatabase.getAllPhases()
                            setPhaseAdapter(phaseList)
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

        binding.addOrUpdateItem.setOnClickListener { insertItem() }
    }


    override fun onResume() {
        super.onResume()
        Log.d("AddItemActivity", "onResume method started")
        lifecycleScope.launch {
            // We are getting all our items from timer.db by Dao interface
            val phaseList = secDatabase.getAllPhases()
            Log.d("AddItemActivity", "onResume method, phaseList size: ${phaseList.toList().size}")

            phaseAdapter = PhaseAdapter()

            // Now we need to make initialization of our recyclerView src(layoutManager, adapter)
            binding.recyclerPhaseView.apply {
                layoutManager = LinearLayoutManager(this@AddItemActivity)
                adapter = phaseAdapter
                setPhaseAdapter(phaseList)

                phaseAdapter?.setOnActionUpdateListener {
                    val intent = Intent(this@AddItemActivity, AddPhaseActivity::class.java)
                    // We are using putExtra to make transfer out phase data
                    intent.putExtra("PhaseData", it)
                    intent.putExtra("ItemData", item)
                    startActivity(intent)
                }

                phaseAdapter?.setOnActionDeleteListener {
                    val builder = AlertDialog.Builder(this@AddItemActivity)
                    builder.setMessage("Are you sure to delete phase?")
                    builder.setPositiveButton("YES") { p0, p1 ->
                        lifecycleScope.launch {
                            secDatabase.deletePhase(it)
                            val phaseList = secDatabase.getAllPhases()
                            // Set new itemList to Adapter
                            setPhaseAdapter(phaseList)
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
        }
    }


    private fun setPhaseAdapter(phaseList: List<Phase>) {
        phaseAdapter?.setData(phaseList)
    }


    private fun insertItem() {
        val itemName = binding.editName.text.toString()
        val itemDuration = binding.editDuration.text.toString().toInt()
        val itemColor = binding.editColor.text.toString()

        lifecycleScope.launch {
            if(item == null) {
                val new_item = Item(
                    name = itemName,
                    duration = itemDuration,
                    color = itemColor
                )
                mainDatabase.insertItem(new_item)
                finish()
            }
            else {
                val update_item = Item(
                    name = itemName,
                    duration = itemDuration,
                    color = itemColor
                )
                update_item.id = item?.id ?: null
                mainDatabase.updateItem(update_item)
                finish()
            }
        }
    }
}