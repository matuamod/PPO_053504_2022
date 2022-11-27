package com.timer.lab2_timer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timer.lab2_timer.databinding.ActivityAddItemBinding
import kotlinx.coroutines.launch

class AddItemActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddItemBinding
    private var item: Item? = null
    private var phaseAdapter: PhaseAdapter? = null
    private var itemActionBar: ActionBar? = null
    private val mainDatabase by lazy { MainDatabase.getDatabase(this).getItemDao() }
    private val secDatabase by lazy { MainDatabase.getDatabase(this).getPhaseDao() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Lab2_timer)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemActionBar = supportActionBar

        // Now we can get our extra data(item) from MainActivity and return it to item
        item = intent.getSerializableExtra("ItemData") as Item?

        // If we haven't got any extra data -> Add item
        if(item == null) {
            binding.addOrUpdateItem.text = "Add timer"
            binding.recyclerPhaseView.isEnabled = false
            binding.deleteAllPhases.isEnabled = false
            binding.addPhase.isEnabled = false
        }
        // Else -> Update item
        else {
            binding.addOrUpdateItem.text = "Update"
            binding.editName.setText(item?.name.toString())
            binding.editDuration.setText(item?.duration.toString())
            setItemColor()

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
                            setPhaseColorAdapter(item!!.color)
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

                var specList: List<Phase> = emptyList()
                itemActionBar?.title = "Add timer"

                if(item != null) {
                    lifecycleScope.launch {
                        Log.d("AddItemActivity", "SpecList list length is: ${specList.size}")
                        specList = secDatabase.getAllPhases().filter { it.timer_id == item?.id }
                        Log.d("AddItemActivity", "SpecList list length is: ${specList.size}")
                        setPhaseAdapter(specList)
                        setPhaseColorAdapter(item!!.color)
                    }
                    itemActionBar?.title = "Update timer"
                }

                setPhaseAdapter(specList)

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
                            setPhaseColorAdapter(item!!.color)
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


    private fun setPhaseColorAdapter(phaseColor: String) {
        phaseAdapter?.setColor(phaseColor)
    }


    private fun insertItem() {
        Log.d("AddItemActivity", "First")
        var itemName = binding.editName.text.toString()
        Log.d("AddItemActivity", "Second")
        var itemDuration = 0
        Log.d("AddItemActivity", "Third")
        var itemColor = getItemColor()
        Log.d("AddItemActivity", "Four")

        lifecycleScope.launch {
            if(item == null) {
                val new_item = Item(
                    name = itemName,
                    duration = itemDuration,
                    color = itemColor
                )

                Log.d("AddItemActivity", "Five")
                mainDatabase.insertItem(new_item)
                Log.d("AddItemActivity", "Six")
                finish()
            }
            else {
                var specList = secDatabase.getAllPhases().filter { it.timer_id == item?.id }
                val update_item = Item(
                    name = itemName,
                    duration = getItemDuration(specList),
                    color = itemColor
                )
                update_item.id = item?.id ?: null
                mainDatabase.updateItem(update_item)
                finish()
            }
        }
    }


    private fun getRadioButtonsList(): List<RadioButton> {
        val count: Int = binding.colorsRGroup.getChildCount()
        val listOfRadioButtons = ArrayList<RadioButton>()

        for(i in 0 until count) {
            val radioButtonView: View = binding.colorsRGroup.getChildAt(i)
            if (radioButtonView is RadioButton) {
                listOfRadioButtons.add(radioButtonView)
            }
        }
        return listOfRadioButtons
    }


    private fun setItemColor() {
        var listOfRadioButtons = getRadioButtonsList()
        var colorList = listOf("#40FF00", "#FF9800", "#F3417E", "#FF3D00", "#FFEE00")

        for(i in listOfRadioButtons.indices) {
            var radioButton: View = listOfRadioButtons[i]

            if(colorList[i] == item?.color) {
                binding.colorsRGroup.check(radioButton.id)
            }
        }
    }


    private fun getItemColor(): String {

        var colorList = listOf("#40FF00", "#FF9800", "#F3417E", "#FF3D00", "#FFEE00")
        var itemColor: String? = null

        val array = arrayOf(binding.greenRButton, binding.orangeRButton,
            binding.pinkRButton, binding.redRButton, binding.yellowRButton
        )

        for(i in array.indices) {
            if(array[i].isChecked) itemColor = colorList[i]
        }

        return itemColor!!
    }


    private fun getItemDuration(phaseList: List<Phase>): Int {
        var itemDuration = 0

        if(!phaseList.isEmpty()) {

            for(phase in phaseList) {
                itemDuration = itemDuration.plus(phase.duration)
            }
        }
        return itemDuration!!
    }
}