package com.pkt.majika

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import retrofit2.Response
import com.google.zxing.BarcodeFormat
import com.pkt.majika.utils.PermissionUtils
import com.pkt.majika.utils.Config
import com.pkt.majika.utils.retrofit.EndpointPayment
import com.pkt.majika.utils.retrofit.DataPayment
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.pkt.majika.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentFragment : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    private val cartItemViewModel: CartItemViewModels by viewModels {
        CartItemModelFact((this.application as App).repository)
    }

    private fun codeScannerManager(scanner: CodeScannerView){
        codeScanner = CodeScanner(this, scanner)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_payment)

        fun initializeTotalPrice(){
            findViewById<TextView>(R.id.total_price).text = intent.extras!!.getString("totalPrice")
        }

        fun viewMessage(text1: String, text2: String){
            findViewById<TextView>(R.id.scan_result).text = text1
            findViewById<TextView>(R.id.scan_result2).text = text2
        }

        fun intentActivity(intentReq: Intent){
            intentReq.putExtra("origin", "start")
            startActivity(intentReq)
            finish()
        }

        fun paymentResponseManager(paymentResponse: Response<DataPayment>){
            val result = paymentResponse.body()!!.status
            Log.d("PostData", result)

            this@PaymentFragment.runOnUiThread {
                if (result == "SUCCESS") {
                    viewMessage("Done", "Payment Success")
                    findViewById<ImageView>(R.id.scan_result_icon).setImageResource(R.drawable.baseline_check_circle_24)
                } else {
                    viewMessage("Fail", "Need to Pay")
                    findViewById<ImageView>(R.id.scan_result_icon).setImageResource(R.drawable.baseline_close_24)
                }
            }

            if (result == "SUCCESS") {
                Handler(Looper.getMainLooper()).postDelayed({
                    cartItemViewModel.clearAll()
                    intentActivity(Intent(this@PaymentFragment, MainActivity::class.java))
                }, 5000)
            } else {
                codeScanner.startPreview()
            }
        }

        fun nullPointerException(){
            this@PaymentFragment.runOnUiThread {
                findViewById<TextView>(R.id.scan_result).text = "Fail"
                findViewById<TextView>(R.id.scan_result2).text = "Need to Pay"
                findViewById<ImageView>(R.id.scan_result_icon).setImageResource(R.drawable.baseline_close_24)
                codeScanner.startPreview()
            }
        }

        fun handleException(){
            val intReq = Intent(this@PaymentFragment, MainActivity::class.java)
            intReq.putExtra("origin", "payment")
            startActivity(intReq)
            finish()
        }

        fun showErrorCameraMessage(throwView: Throwable){
            Toast.makeText(
                this, "Camera initialization error: ${throwView.message}",
                Toast.LENGTH_LONG
            ).show()
        }

        if(PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA)){

            initializeTotalPrice()

            val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

            codeScannerManager(scannerView)

            // Callbacks
            codeScanner.decodeCallback = DecodeCallback {
                runOnUiThread {
                    val endpointPaymentAPI = Config.getInstance().create(EndpointPayment::class.java)
                    GlobalScope.launch {
                        try {

                            val paymentResponse = endpointPaymentAPI.postPayment(it.text)
                            println(it.text)
                            paymentResponseManager(paymentResponse)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            when (e) {
                                is java.lang.NullPointerException -> {
                                    nullPointerException()
                                }
                                is java.net.SocketTimeoutException -> {
                                    withContext(Dispatchers.Main) {Toast.makeText(this@PaymentFragment,
                                                    "Connection timed out", Toast.LENGTH_SHORT).show()}
                                    handleException()
                                }
                                else -> {
                                    handleException()
                                }
                            }
                        }
                    }
                }
            }

            //ErrorCallback.SUPPRESS
            codeScanner.errorCallback = ErrorCallback {
                showErrorCameraMessage(it)
            }

            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }
        }else{
            this.requestPermissions(arrayOf(Manifest.permission.CAMERA), PermissionUtils.getRequestCode())
            this.finish()
            return
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }
}