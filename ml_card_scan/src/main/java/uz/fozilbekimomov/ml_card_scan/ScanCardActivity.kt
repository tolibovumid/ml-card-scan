package uz.fozilbekimomov.ml_card_scan

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import uz.fozilbekimomov.ml_card_scan.analyzer.TextReaderAnalyzer
import uz.fozilbekimomov.ml_card_scan.listeners.ScanOptions
import uz.fozilbekimomov.ml_card_scan.models.CardResult
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanCardActivity : AppCompatActivity() {

    private var camera: Camera? = null
    private val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    private var hasOptions = false

    private lateinit var flashCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_card)


        if (scanOptions != null) {

            hasOptions = true

            val view = scanOptions?.getCustomDecoration(this)

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )

            view?.layoutParams = params

            findViewById<FrameLayout>(R.id.custom_decor).addView(view)

            findViewById<TextView>(R.id.custom_title).text = scanOptions?.getScreenTitle()

        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        flashCheckBox = findViewById(R.id.flash_switch)

        flashCheckBox.setOnCheckedChangeListener { _, b ->
            camera?.cameraControl?.enableTorch(b)
        }

        findViewById<ImageButton>(R.id.arrow_back).setOnClickListener {
            onBackPressed()
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    private val imageAnalyzer by lazy {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setImageQueueDepth(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    TextReaderAnalyzer(::onDateFound, ::onNumberFound, ::onCardScanned)
                )
            }
    }

    var isFinished = false

    private fun onCardScanned(cardResult: CardResult) {

        if (!isFinished) {


            val cardNumberView: TextView = findViewById(R.id.custom_card_number)
            val cardDateView: TextView = findViewById(R.id.custom_card_date)

            cardNumberView.text = cardResult.cardNumber
            cardDateView.text = cardResult.cardDate

            val intent = Intent()
            intent.putExtra(CARD_RESULT, cardResult)
            setResult(Activity.RESULT_OK, intent)
            val handler = Handler(Looper.getMainLooper())

            isFinished = false

            if (hasOptions) {
                handler.postDelayed({ finish() }, 500)
            }else{
                handler.postDelayed({ finish() }, scanOptions!!.getDelayAfterScan())
            }

        }


    }


    private fun onNumberFound(number: String) {

    }

    private fun onDateFound(date: String) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startCamera() {


        val surfaceView = findViewById<PreviewView>(R.id.cameraPreviewView)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)


        cameraProviderFuture.addListener(
            {
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(surfaceView.surfaceProvider)
                    }


                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                val cameraProvider = cameraProviderFuture.get()

                cameraProvider.unbindAll()

                this.camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            },
            ContextCompat.getMainExecutor(this)
        )


    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        val CARD_RESULT = "scan_card_activity"

        private var scanOptions: ScanOptions? = null

        fun Context.buildScanIntent(options: ScanOptions? = null): Intent {
            scanOptions = options
            val intent = Intent(this, ScanCardActivity::class.java)
            return intent
        }

    }

}