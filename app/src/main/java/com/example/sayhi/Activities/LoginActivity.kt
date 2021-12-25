package com.example.sayhi.Activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.example.sayhi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hbb20.CountryCodePicker

class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumber: String
    private lateinit var countryCode: String

    private lateinit var phoneNumberEt: EditText
    private lateinit var nextBtn: MaterialButton
    private lateinit var ccp: CountryCodePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViews()

        phoneNumberEt.addTextChangedListener {
            nextBtn.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }

        nextBtn.setOnClickListener {
            checkNumber()
        }
    }

    private fun findViews() {
        phoneNumberEt = findViewById<EditText>(R.id.phoneNumberEt)
        nextBtn = findViewById<MaterialButton>(R.id.nextBtn)
        ccp = findViewById<CountryCodePicker>(R.id.ccp)
    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + phoneNumberEt.text.toString()
        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(
                "We will be verifying the phone number $phoneNumber\n"
                        + "Is this OK or you would like to edit the number?"
            )
            setPositiveButton("OK") { _, _ ->
                showOtpActivity()
            }
            setNegativeButton("Edit") { dialog, which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        startActivity(
            Intent(this, OtpActivity::class.java)
                .putExtra(PHONE_NUMBER, phoneNumber)
        )
        finish()
    }

}