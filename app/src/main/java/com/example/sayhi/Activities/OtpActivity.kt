package com.example.sayhi.Activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.sayhi.Activities.LoginActivity
import com.example.sayhi.Activities.MainActivity
import com.example.sayhi.Activities.SignUpActivity
import com.example.sayhi.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phoneNumber"

class OtpActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var verifyTv: TextView
    private lateinit var waitingTv: TextView
    private lateinit var counterTv: TextView
    private lateinit var sentcodeEt: EditText
    private lateinit var resendBtn: MaterialButton
    private lateinit var verificationBtn: MaterialButton
    private lateinit var progressDialog: ProgressDialog
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mCounterDown: CountDownTimer? = null
    private var timeLeft: Long = -1
    var phoneNumber: String? = null
    var mVerificationId: String? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        findIds()
        initViews()
        startVerify()
    }

    private fun startVerify() {
        startPhoneNumberVerification(phoneNumber!!)
        showTimer(60000)
        progressDialog = createProgressDialog("Sending a verification code", false)
        progressDialog.show()
    }

    private fun initViews() {

        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        verifyTv.text = "Verify $phoneNumber"
        setSpannableString()

        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)

        // init fire base verify Phone number callback
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                val smsMessageSent = credential.smsCode
                if (!smsMessageSent.isNullOrBlank())
                    sentcodeEt.setText(smsMessageSent)
                Toast.makeText(this@OtpActivity, "onVerificationCompleted", Toast.LENGTH_SHORT).show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.e("Exception:", "FirebaseAuthInvalidCredentialsException", e)
                    Log.e("Exception:", "FirebaseAuthInvalidCredentialsException " + e.message)

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.e("Exception:", "FirebaseTooManyRequestsException", e)
                }
                Toast.makeText(this@OtpActivity, "onVerificationFailed", Toast.LENGTH_SHORT).show()
                // Show a message and update the UI
                notifyUserAndRetry("Your Phone Number might be wrong or connection error.Retry again!")

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                //for low level version which doesn't do auto verification save the verification code and the token

                progressDialog.dismiss()
                counterTv.isVisible = false
                // Save verification ID and resending token so we can use them later
                Toast.makeText(this@OtpActivity, "onCodeSent", Toast.LENGTH_SHORT).show()

                mVerificationId = verificationId
                mResendToken = token

            }
        }
    }

    private fun setSpannableString() {
        val span =
            SpannableString(
                "Waiting to automatically " +
                        "detect an SMS sent to $phoneNumber. Wrong Number ?"
            )
        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }

            override fun onClick(widget: View) {
                showLoginActivity()
                Toast.makeText(this@OtpActivity, "showLoginActivity", Toast.LENGTH_SHORT).show()
            }

        }

        span.setSpan(clickableSpan, span.length - 14, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span

    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                showLoginActivity()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeLeft", timeLeft)
        outState.putString(PHONE_NUMBER, phoneNumber)
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        Toast.makeText(this@OtpActivity, "startPhoneNumberVerification", Toast.LENGTH_SHORT).show()
//        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
//            .setPhoneNumber(phoneNumber)       // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(this)                 // Activity (for callback binding)
//            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber!!,          // Phone number to verify
            60,              // Timeout duration
            TimeUnit.SECONDS,       // Unit of timeout
            this,             // Activity (for callback binding)
            callbacks
        )
    }

    private fun showLoginActivity() {
        Toast.makeText(this@OtpActivity, "showLoginActivity", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(
                this,
                LoginActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Toast.makeText(this@OtpActivity, "signInWithPhoneAuthCredential", Toast.LENGTH_SHORT).show()
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    if (::progressDialog.isInitialized) {
                        progressDialog.dismiss()
                    }
                    //First Time Login
                    if (task.result?.additionalUserInfo?.isNewUser == true) {
                        showSignUpActivity()
                    } else {
                        showHomeActivity()
                    }
                } else {

                    if (::progressDialog.isInitialized) {
                        progressDialog.dismiss()
                    }

                    notifyUserAndRetry("Your Phone Number Verification is failed. Retry again!")
                }
            }
    }

    private fun showSignUpActivity() {
        Toast.makeText(this@OtpActivity, "showSignUpActivity", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showHomeActivity() {
        Toast.makeText(this@OtpActivity, "showHomeActivity", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCounterDown != null) {
            mCounterDown!!.cancel()
        }
    }

    override fun onClick(v: View) {
        when (v) {
            verificationBtn -> {
                Toast.makeText(this@OtpActivity, "verificationBtn", Toast.LENGTH_SHORT).show()

                // try to enter the code by yourself to handle the case
                // if user enter another sim card used in another phone ...
                var code = sentcodeEt.text.toString()
                if (code.isNotEmpty() && !mVerificationId.isNullOrEmpty()) {

                    progressDialog = createProgressDialog("Please wait...", false)
                    progressDialog.show()
                    val credential =
                        PhoneAuthProvider.getCredential(mVerificationId!!, code.toString())
                    signInWithPhoneAuthCredential(credential)
                }
            }

            resendBtn -> {
                Toast.makeText(this@OtpActivity, "resendBtn", Toast.LENGTH_SHORT).show()

                if (mResendToken != null) {
                    resendVerificationCode(phoneNumber.toString(), mResendToken)
                    showTimer(60000)
                    progressDialog = createProgressDialog("Sending a verification code", false)
                    progressDialog.show()
                } else {
                    Toast.makeText(
                        this,
                        "Sorry, You Can't request new code now, Please wait ...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun showTimer(milliesInFuture: Long) {
        resendBtn.isEnabled = false
        mCounterDown = object : CountDownTimer(milliesInFuture, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                counterTv.isVisible = true
                counterTv.text = "Seconds remaining: " + millisUntilFinished / 1000


                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                resendBtn.isEnabled = true
                resendBtn.setBackgroundColor(Color.parseColor("#ff007a"))
                counterTv.isVisible = false
            }
        }.start()
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        mResendToken: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            mResendToken
        ) // ForceResendingToken from callbacks
    }

    private fun findIds() {
        verifyTv = findViewById(R.id.verifyTv)
        verificationBtn = findViewById(R.id.verificationBtn)
        waitingTv = findViewById(R.id.waitingTv)
        counterTv = findViewById(R.id.counterTv)
        resendBtn = findViewById(R.id.resendBtn)
        sentcodeEt = findViewById(R.id.sentcodeEt)
    }

}

fun Context.createProgressDialog(message: String, isCancellable: Boolean): ProgressDialog {
    return ProgressDialog(this).apply {
        setCancelable(false)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }

}