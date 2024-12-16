package com.example.tips

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    //● Add the ability to round the total bill up or down, which should update the tip amount and
    //percent automatically.
    //● Add pre-defined options for service (e.g. poor, acceptable, good, excellent) which
    //automatically decide the tip percentage
    private lateinit var etBase: EditText
    private lateinit var tvTipPercent: TextView
    private lateinit var sbTip: SeekBar
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvTipQualityLabel: TextView
    private lateinit var etNumberOfPeople: EditText
    private lateinit var tvIndividualBudget: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etBase = findViewById(R.id.etBase)
        etNumberOfPeople = findViewById(R.id.etNumberOfPeople)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        tvTipQualityLabel = findViewById(R.id.tipQualityLabel)
        sbTip = findViewById(R.id.sbTip)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotal = findViewById(R.id.tvTotal)
        tvIndividualBudget = findViewById(R.id.tvIndividualBudget)

        etBase.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateTipAmount()
            }
        })
        etNumberOfPeople.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateTipAmount()
            }

        })
        sbTip.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvTipPercent.text = String.format(Locale.getDefault(), "%d%%", progress)
                calculateTipQuality(progress)
                calculateTipAmount()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun calculateTipQuality(progress: Int) {
        val qualityMap = mapOf(
            0..9 to Pair("Poor", "\uD83D\uDE2C"),
            10..14 to Pair("Acceptable", "\uD83D\uDE42"),
            15..19 to Pair("Good", "\uD83D\uDE00"),
            20..24 to Pair("Great", "\uD83D\uDE01")
        )
        val (text, emoji) = qualityMap.entries.find { progress in it.key }?.value ?: Pair("Amazing", "\uD83E\uDD11")
        tvTipQualityLabel.text = String.format(Locale.getDefault(), "%s %s", text, emoji)

        val color = ArgbEvaluator().evaluate(
            progress.toFloat()/30,
            getColor(R.color.Worse),
            getColor(R.color.Optimal))
        tvTipQualityLabel.setTextColor(color.toString().toInt())
    }

    private fun calculateTipAmount() {
        if (etBase.text.isEmpty()) {
            return
        }
        val baseAmount = etBase.text.toString().toDouble()
        val tipPercent = sbTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val total = tipAmount + baseAmount
        tvTipAmount.text = String.format(Locale.getDefault(), "%.2f", tipAmount)
        tvTotal.text = String.format(Locale.getDefault(), "%.2f", total)
        if (etNumberOfPeople.text.isNotEmpty()) {
            calculateIndividualBudget(total)
        }
        else {
            tvIndividualBudget.text = String.format(Locale.getDefault(), "%.2f", total)
        }
    }

    private fun calculateIndividualBudget(total: Double) {
        val numberOfPersons = etNumberOfPeople.text.toString().toInt()
        tvIndividualBudget.text = String.format(Locale.getDefault(),"%.2f", total/numberOfPersons)
    }
}