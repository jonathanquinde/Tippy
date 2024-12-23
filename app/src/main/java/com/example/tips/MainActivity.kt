package com.example.tips

import android.animation.ArgbEvaluator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
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
    private lateinit var btRoundUp: Button
    private lateinit var btRoundDown: Button

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
        btRoundUp = findViewById(R.id.btRoundUp)
        btRoundDown = findViewById(R.id.btRoundDown)

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
                calculateIndividualBudget()
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
        btRoundUp.setOnClickListener { roundUpAndDown(true) }
        btRoundDown.setOnClickListener { roundUpAndDown(false) }
    }

    private fun roundUpAndDown(up: Boolean) {
        if (etBase.text.isEmpty() || sbTip.progress == 0)
            return
        val tipAmount = tvTipAmount.text.toString().replace(',', '.').toDouble()
        val totalAmount = tvTotal.text.toString().replace(',', '.').toDouble()
        val decimals = tipAmount - tipAmount.toLong()
        if (up) {
            if (decimals == 0.0)
                return
            tvTipAmount.text = String.format(Locale.getDefault(), "%.2f", tipAmount + 1 - decimals)
            tvTotal.text = String.format(Locale.getDefault(), "%.2f", totalAmount + 1 - decimals)
        } else {
            tvTipAmount.text = String.format(Locale.getDefault(), "%.2f", tipAmount - decimals)
            tvTotal.text = String.format(Locale.getDefault(), "%.2f", totalAmount - decimals)
        }
    }

    private fun calculateIndividualBudget() {
        if (etNumberOfPeople.text.isEmpty() || etBase.text.isEmpty()) {
            tvIndividualBudget.text = getString(R.string.Empty)
            return
        }
        val base = etBase.text.toString().toDouble()
        val nPeople = etNumberOfPeople.text.toString().toInt()
        if (nPeople == 0)
            tvIndividualBudget.text = getString(R.string.Empty)
        else
            tvIndividualBudget.text = String.format(Locale.getDefault(), "%.2f", base/nPeople)
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
            tvTotal.text = getString(R.string.Empty)
            tvTipAmount.text = getString(R.string.Empty)
            tvIndividualBudget.text = getString(R.string.Empty)
            return
        }
        val baseAmount = etBase.text.toString().toDouble()
        val tipPercent = sbTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        tvTipAmount.text = String.format(Locale.getDefault(), "%.2f", tipAmount)
        tvTotal.text = String.format(Locale.getDefault(), "%.2f", baseAmount + tipAmount)
        calculateIndividualBudget()
    }
}