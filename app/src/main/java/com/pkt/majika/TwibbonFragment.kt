package com.pkt.majika

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.pkt.majika.utils.PermissionUtils

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TwibbonFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var imagePreview: Boolean = false
    private lateinit var captureButton: Button
    private lateinit var warningMessage: TextView
    private lateinit var thisContext: Context
    private lateinit var previewView: PreviewView
    private lateinit var previewImage: ImageView
    private lateinit var twibbonImage: ImageView
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        cameraProviderFuture = ProcessCameraProvider.getInstance(thisContext)
    }

    private fun initializeCameraTwibbon(view: View){
        previewView = view.findViewById(R.id.previewView)
        previewView.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
        previewView.visibility = View.VISIBLE
        previewImage = view.findViewById(R.id.imagePreview)
        twibbonImage = view.findViewById(R.id.twibbon)
        twibbonImage.visibility = View.VISIBLE
        warningMessage = view.findViewById(R.id.warningCameraTwibbon)
        warningMessage.visibility = View.GONE
        captureButton = view.findViewById(R.id.btnCapture)
        captureButton.visibility = View.VISIBLE
    }

    private fun permissionFail_Activity(){
        warningMessage.visibility = View.VISIBLE
        warningMessage.text = resources.getString(R.string.twibbon_warning_no_permissions)
        previewView.visibility = View.GONE
        twibbonImage.visibility = View.GONE
        captureButton.visibility = View.GONE
        this.requestPermissions(arrayOf(Manifest.permission.CAMERA), PermissionUtils.getRequestCode())
    }

    private fun startCapture(){
        previewView.visibility = View.VISIBLE
        previewImage.visibility = View.GONE
        captureButton.text = resources.getString(R.string.btn_capture)
    }

    private fun captureAgain(){
        previewView.visibility = View.GONE
        previewImage.visibility = View.VISIBLE
        captureButton.text = resources.getString(R.string.btn_take_photo)
    }

    private fun captureCondition(){
        if (!imagePreview) {
            captureAgain()
            val bitmap = previewView.bitmap
            previewImage.setImageBitmap(bitmap)
            imagePreview = true
            Toast.makeText(thisContext, "Image captured", Toast.LENGTH_SHORT).show()
        } else {
            startCapture()
            imagePreview = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_twibbon, container, false)
        this.initializeCameraTwibbon(view)

        if (!imagePreview) this.startCapture()
        else this.captureAgain()

        if (!PermissionUtils.isPermissionGranted(requireActivity() as AppCompatActivity,Manifest.permission.CAMERA)) {
            this.permissionFail_Activity()
            return view
        }

        captureButton.setOnClickListener {
            this.captureCondition()
        }
        return view
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(thisContext as LifecycleOwner, cameraSelector, preview)
    }

    override fun onStart() {
        super.onStart()
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(thisContext))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        thisContext = context
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProviderFuture.get().unbindAll()
    }
}
