package com.matuamod.converter

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.matuamod.converter.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val dataModel: DataModel by viewModels()

    private var resNumberStr: String? = ""
    private var outputNumberStr: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openFragment(KeyboardFragment.newInstance(), R.id.keyboard_holder)

         configNumber()

        if(Build.VERSION.SDK_INT >= 21) {
            entryTextField.showSoftInputOnFocus = false
        }
        else if(Build.VERSION.SDK_INT >= 11) {
            entryTextField.setRawInputType(InputType.TYPE_CLASS_TEXT)
            entryTextField.setTextIsSelectable(true)
        }
        else {
            entryTextField.setRawInputType(InputType.TYPE_NULL)
            entryTextField.isFocusable = true
        }

        Log.d("LogMAct", "onCreate method started")
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Log.d("onSaveInstance", resNumberStr.toString())
        outState.run {
            putString("fromRadio_1", radioFromUnitButton_1.text.toString())
            putBoolean("isEnabled_fromRadio_1", radioFromUnitButton_1.isEnabled)
            putString("fromRadio_2", radioFromUnitButton_2.text.toString())
            putBoolean("isEnabled_fromRadio_2", radioFromUnitButton_2.isEnabled)
            putString("fromRadio_3", radioFromUnitButton_3.text.toString())
            putBoolean("isEnabled_fromRadio_3", radioFromUnitButton_3.isEnabled)
            putString("toRadio_1", radioToUnitButton_1.text.toString())
            putBoolean("isEnabled_toRadio_1", radioToUnitButton_1.isEnabled)
            putString("toRadio_2", radioToUnitButton_2.text.toString())
            putBoolean("isEnabled_toRadio_2", radioToUnitButton_2.isEnabled)
            putString("toRadio_3", radioToUnitButton_3.text.toString())
            putBoolean("isEnabled_toRadio_3", radioToUnitButton_3.isEnabled)
            putBoolean("isEnabled_ViceVersa", vice_versa_button.isEnabled)
            putString("resNumberStr", resNumberStr)
            putString("outputNumberStr", outputTextField.text.toString())
        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        radioFromUnitButton_1.text = savedInstanceState.getString("fromRadio_1")
        radioFromUnitButton_1.isEnabled = savedInstanceState.getBoolean("isEnabled_fromRadio_1")
        radioFromUnitButton_2.text = savedInstanceState.getString("fromRadio_2")
        radioFromUnitButton_2.isEnabled = savedInstanceState.getBoolean("isEnabled_fromRadio_2")
        radioFromUnitButton_3.text = savedInstanceState.getString("fromRadio_3")
        radioFromUnitButton_3.isEnabled = savedInstanceState.getBoolean("isEnabled_fromRadio_3")
        radioToUnitButton_1.text = savedInstanceState.getString("toRadio_1")
        radioToUnitButton_1.isEnabled = savedInstanceState.getBoolean("isEnabled_toRadio_1")
        radioToUnitButton_2.text = savedInstanceState.getString("toRadio_2")
        radioToUnitButton_2.isEnabled = savedInstanceState.getBoolean("isEnabled_toRadio_2")
        radioToUnitButton_3.text = savedInstanceState.getString("toRadio_3")
        radioToUnitButton_3.isEnabled = savedInstanceState.getBoolean("isEnabled_toRadio_3")
        vice_versa_button.isEnabled = savedInstanceState.getBoolean("isEnabled_ViceVersa")
        resNumberStr = savedInstanceState.getString("resNumberStr")
        outputNumberStr = savedInstanceState.getString("outputNumberStr")
        configNumber(true)
    }


    override fun onStart() {
        super.onStart()
        Log.d("LogMAct", "onStart method started")
    }


    override fun onResume() {
        super.onResume()
        Log.d("LogMAct", "onResume method started")
    }


    override fun onPause() {
        super.onPause()
        Log.d("LogMAct", "onPause method started")
    }


    override fun onRestart() {
        super.onRestart()
        Log.d("LogMAct", "onRestart method started")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("LogMAct", "onDestroy method started")
    }


    override fun onStop() {
        super.onStop()
        Log.d("LogMAct", "onStop method started")
    }


    private fun removeLastChar(str: String?): String? {
        return str?.replaceFirst(".$".toRegex(), "")
    }


    private fun getCropCount(): Int {
        var cropCounter: Int = 0

        for(element in resNumberStr.toString()) {
            if(element == '.') {
                cropCounter++
            }
        }
        return cropCounter
    }


    private fun configNumber(isRestore:Boolean = false) {
        if (isRestore) {
            if(resNumberStr?.isNotEmpty()!! && outputNumberStr?.isNotEmpty()!!) {
                binding.entryTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                binding.outputTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
            }
            else if(resNumberStr?.isNotEmpty()!!) {
                binding.entryTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
            }

            binding.entryTextField.setText(resNumberStr)
            binding.outputTextField.setText(outputNumberStr)
            return
        }

        dataModel.digit.observe(this) {
            if(it != "enter" && it != "delete") {
                if(it != "." || (getCropCount() < 1 && resNumberStr!!.isNotEmpty())) {
                    resNumberStr += it
                    binding.entryTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                    binding.entryTextField.setText(resNumberStr)
                }
            }
            else if(it == "delete") {

                if(resNumberStr!!.isNotEmpty()) {
                    resNumberStr = removeLastChar(resNumberStr)
                    binding.entryTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)
                    binding.entryTextField.setText(resNumberStr)

                    if(resNumberStr!!.isEmpty()) {
                        binding.entryTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                    }
                }
                else {
                    binding.entryTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                }
            }
            else {

                if(resNumberStr!!.isNotEmpty()) {
                    binding.outputTextField.setText(convertData())

                    if(resNumberStr!!.isEmpty()) {
                        binding.outputTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                        binding.outputTextField.setText("")
                    }
                }
                else {
                    binding.outputTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
                    binding.outputTextField.setText("")
                }
            }
        }
    }


    private fun openFragment(fragment: Fragment, idHolder: Int) {
        supportFragmentManager.beginTransaction().replace(idHolder, fragment).commit()
    }


    private fun chooseDistance() {
        makeUnitsNotActive()

        val chosenDistance = Toast.makeText(this, "Distance category", Toast.LENGTH_SHORT)
        chosenDistance.show()
    }


    private fun chooseWeight() {
        makeUnitsNotActive()

        val chosenWeight = Toast.makeText(this, "Weight category", Toast.LENGTH_SHORT)
        chosenWeight.show()
    }


    private fun chooseCurrency() {
        makeUnitsNotActive()

        val chosenCurrency = Toast.makeText(this, "Currency category", Toast.LENGTH_SHORT)
        chosenCurrency.show()
    }


    private fun makeUnitsNotActive() {
        radioGroup_2.clearCheck()
        radioGroup_3.clearCheck()
    }


    private fun chooseUnits(list: List<String>) {

        radioFromUnitButton_1.text = list[0]
        radioFromUnitButton_1.isEnabled = true

        radioFromUnitButton_2.text = list[1]
        radioFromUnitButton_2.isEnabled = true

        radioFromUnitButton_3.text = list[2]
        radioFromUnitButton_3.isEnabled = true

        radioToUnitButton_1.text = list[0]
        radioToUnitButton_1.isEnabled = true

        radioToUnitButton_2.text = list[1]
        radioToUnitButton_2.isEnabled = true

        radioToUnitButton_3.text = list[2]
        radioToUnitButton_3.isEnabled = true

        entryTextField.isEnabled = true

        vice_versa_button.isEnabled = true
    }


    private fun checkedRadioButtonName(radioButton_1: RadioButton,
                                            radioButton_2: RadioButton,
                                                radioButton_3: RadioButton): String {

        lateinit var buttonName: String

        if(radioButton_1.isChecked) {
            buttonName = radioButton_1.text.toString()
        }
        else if(radioButton_2.isChecked) {
            buttonName = radioButton_2.text.toString()
        }
        else if(radioButton_3.isChecked) {
            buttonName = radioButton_3.text.toString()
        }
        return buttonName
    }


    private fun changeCheckedStatus(buttonName: String, radioButton_1: RadioButton,
                                                            radioButton_2: RadioButton,
                                                                radioButton_3: RadioButton) {

        if(radioButton_1.text.toString() == buttonName) {
            radioButton_1.isChecked = true
        }
        else if(radioButton_2.text.toString() == buttonName) {
            radioButton_2.isChecked = true
        }
        else if(radioButton_3.text.toString() == buttonName) {
            radioButton_3.isChecked = true
        }
    }


    fun onViceVersaClick(view: View) {
        lateinit var fromButtonName: String
        lateinit var toButtonName: String
        var bufferTextField: Editable?

        if(areUnitsChecked()) {
            if(vice_versa_button.isEnabled) {
                fromButtonName = checkedRadioButtonName(radioFromUnitButton_1,
                                                            radioFromUnitButton_2,
                                                                radioFromUnitButton_3)

                toButtonName = checkedRadioButtonName(radioToUnitButton_1,
                                                            radioToUnitButton_2,
                                                                radioToUnitButton_3)

                if(entryTextField.text.toString().trim().isNotEmpty()
                    && outputTextField.text.toString().trim().isNotEmpty()) {
                    bufferTextField = outputTextField.text
                    outputTextField.text = entryTextField.text
                    entryTextField.text = bufferTextField
                    resNumberStr = entryTextField.text.toString()
//                    outputNumberStr = outputTextField.text.toString()
                }

                makeUnitsNotActive()
                changeCheckedStatus(toButtonName, radioFromUnitButton_1,
                                                        radioFromUnitButton_2,
                                                            radioFromUnitButton_3)

                changeCheckedStatus(fromButtonName, radioToUnitButton_1,
                                                        radioToUnitButton_2,
                                                            radioToUnitButton_3)
            }
        }
    }


    fun chosenDistance(view: View) {
        chooseDistance()

        val distUnitsList = listOf<String>("kilometer", "mile", "nautical")

        chooseUnits(distUnitsList)
    }


    fun chosenWeight(view: View) {
        chooseWeight()

        val weightUnitsList = listOf<String>("kilogram", "pound", "ounce")

        chooseUnits(weightUnitsList)
    }


    fun chosenCurrency(view: View) {
        chooseCurrency()

        val currencyUnitsList = listOf<String>("dollar", "euro", "byn")

        chooseUnits(currencyUnitsList)
    }


    private fun areUnitsChecked(): Boolean {
        if((radioFromUnitButton_1.isChecked || radioFromUnitButton_2.isChecked || radioFromUnitButton_3.isChecked)
            &&(radioToUnitButton_1.isChecked || radioToUnitButton_2.isChecked || radioToUnitButton_3.isChecked)) {

            return true
        }
        return false
    }


    private fun convertDataByUnits(fromDistance: BigDecimal ,fromButtonName: String,
    toButtonName: String, ratioList: List<Double>): BigDecimal {
        var toDistance: BigDecimal = BigDecimal.ZERO

        if(radioFromUnitButton_1.text.toString() == fromButtonName) {

            if(radioToUnitButton_2.text.toString() == toButtonName) {
                toDistance = fromDistance * BigDecimal.valueOf(ratioList[0])
            }
            else if (radioToUnitButton_3.text.toString() == toButtonName){
                toDistance = fromDistance * BigDecimal.valueOf(ratioList[1])
            }
        }
        else if(radioFromUnitButton_2.text.toString() == fromButtonName) {

            if(radioToUnitButton_1.text.toString() == toButtonName) {
                toDistance = fromDistance * BigDecimal.valueOf(ratioList[2])
            }
            else if (radioToUnitButton_3.text.toString() == toButtonName){
                toDistance = fromDistance * BigDecimal.valueOf(ratioList[3])
            }
        }
        else {

            if(radioToUnitButton_1.text.toString() == toButtonName) {
                toDistance = fromDistance * BigDecimal.valueOf(ratioList[4])
            }
            else if (radioToUnitButton_2.text.toString() == toButtonName){
                toDistance = fromDistance * BigDecimal.valueOf(ratioList[5])
            }
        }
        return toDistance
    }


    private fun convertData(): String {
        var toValue: BigDecimal = BigDecimal.ZERO
        var toValueStr: String = ""

        if(areUnitsChecked()) {

            outputTextField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30F)

            if(entryTextField.text.toString().trim().isNotEmpty()) {
                var fromValue: BigDecimal = entryTextField.text.toString().toBigDecimal()

                val fromButtonName = checkedRadioButtonName(radioFromUnitButton_1,
                                                                radioFromUnitButton_2,
                                                                    radioFromUnitButton_3)

                val toButtonName = checkedRadioButtonName(radioToUnitButton_1,
                                                                radioToUnitButton_2,
                                                                    radioToUnitButton_3)

                if(fromButtonName == toButtonName) {
                    toValue = fromValue
                }
                else {
                    if (radioDistanceButton.isChecked) {
                        val distRatioList = listOf<Double>(0.621, 0.54, 1.609, 0.869, 1.852, 1.151)
                        toValue = convertDataByUnits(fromValue, fromButtonName, toButtonName, distRatioList)
                    }
                    else if(radioWeightButton.isChecked) {
                        val weightRatioList = listOf<Double>(2.205, 35.274, 0.454, 16.0, 0.0283, 0.0625)
                        toValue = convertDataByUnits(fromValue, fromButtonName, toButtonName, weightRatioList)
                    }
                    else {
                        val currencyRatioList = listOf<Double>(1.03, 2.52, 0.98, 2.47, 0.4, 0.41)
                        toValue = convertDataByUnits(fromValue, fromButtonName, toButtonName, currencyRatioList)
                    }
                }
                toValueStr = toValue.toString()
            }
        }
        return toValueStr
    }


    fun makeCopyFrom(view: View) {
        val defaultButtonStr = resources.getResourceEntryName(view.getId())
        var clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        val notCopiedMsg = Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT)
        val copiedMsg = Toast.makeText(this, "Copied successfully", Toast.LENGTH_SHORT)

        if(defaultButtonStr == "buttonCopy_1") {
            var textToCopy: String = entryTextField.text.toString()

            if (textToCopy.isEmpty()) {
                notCopiedMsg.show()
            } else {
                val clipData = ClipData.newPlainText("text", textToCopy)
                clipboardManager.setPrimaryClip(clipData)

                copiedMsg.show()
            }
        }
        else if(defaultButtonStr == "buttonCopy_2") {
            var textToCopy: String = outputTextField.text.toString()

            if (textToCopy.isEmpty()) {
                notCopiedMsg.show()
            } else {
                val clipData = ClipData.newPlainText("text", textToCopy)
                clipboardManager.setPrimaryClip(clipData)

                copiedMsg.show()
            }
        }
    }
}