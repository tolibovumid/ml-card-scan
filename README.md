#### If you enjoy my content, please consider supporting what I do. Thank you.

[![](https://user-images.githubusercontent.com/36783954/183887369-a0565898-0ed7-4049-877a-c688503aad90.png)](https://www.buymeacoffee.com/fozilbekimomov)

[By me a Coffee](https://www.buymeacoffee.com/fozilbekimomov)

***
[![](https://jitpack.io/v/fozilbekimomov/ml-card-scan.svg)](https://jitpack.io/#fozilbekimomov/ml-card-scan)



To get a Git project into your build:

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
  
allprojects {
   repositories {
     ..
     maven { url 'https://jitpack.io'}
     ..
  }
}

```

### Step 2. Add the dependency

Gradle:

```gradle
//NFC
implementation 'com.github.fozilbekimomov:ml-card-scan:1.0.1'


//google ml
implementation 'com.google.mlkit:text-recognition:16.0.0-beta4'

//CameraX
implementation 'androidx.camera:camera-camera2:1.1.0'
implementation 'androidx.camera:camera-lifecycle:1.1.0'
implementation 'androidx.camera:camera-view:1.1.0'

```
Maven:

```gradle

<repositories>
  <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.fozilbekimomov</groupId>
  <artifactId>ml-card-scan</artifactId>
  <version>1.0.1</version>
</dependency>

```

### Step 3. Modify your App Activity

```kotlin



class MainActivity : AppCompatActivity(), ScanOptions {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startScan.setOnClickListener {
            val intent = buildScanIntent(this)
            activityLauncher.launch(intent)
        }
    }

    val activityLauncher =
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
    }
}
```
