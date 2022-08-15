package uz.fozilbekimomov.mlkitcardscannerandroid

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
/*import uz.fozilbekimomov.ml_card_scan.ScanCardActivity
import uz.fozilbekimomov.ml_card_scan.ScanCardActivity.Companion.buildScanIntent
import uz.fozilbekimomov.ml_card_scan.listeners.ScanOptions
import uz.fozilbekimomov.ml_card_scan.models.CardResult*/
import uz.fozilbekimomov.mlkitcardscannerandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()/*, ScanOptions */{
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
      /*  binding.startScan.setOnClickListener {
            val intent = buildScanIntent(this)
            activityLauncher.launch(intent)
        }*/
    }

   /* val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                intent?.let {
                    if (it.hasExtra(ScanCardActivity.CARD_RESULT)) {
                        val cardResult =
                            it.getParcelableExtra<CardResult>(ScanCardActivity.CARD_RESULT)
                        cardResult?.let {
                            binding.cardDate.text = cardResult.cardDate
                            binding.cardNumber.text = cardResult.cardNumber
                        }
                    }
                }
            }
        }


    override fun getCustomDecoration(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.decoration_view, null, false)
    }

    override fun getScreenTitle(): String {
        return "Поместите карту в рамку"
    }

    override fun getDelayAfterScan(): Long {
        return 1000
    }*/
}