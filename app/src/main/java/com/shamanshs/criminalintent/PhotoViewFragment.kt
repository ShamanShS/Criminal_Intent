package com.shamanshs.criminalintent

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File
import java.util.Date

private const val ARG_FILE = "file"
private const val TAG = "PhotoFragment"

class PhotoViewFragment : DialogFragment() {

    private lateinit var photoFile: File
    private lateinit var photoView: ImageView

//    private var photoWidth: Int = 0
//    private var photoHeight: Int = 0

    private var globalListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)

        photoFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_FILE, File::class.java)!!
        } else {
            arguments?.getSerializable(ARG_FILE) as File
        }


        return activity.let {

            val builder = AlertDialog.Builder(context)
            val inflater = requireActivity().layoutInflater
            val resLayout = inflater.inflate(R.layout.photo_dialog_fragment, null)

            photoView = resLayout?.findViewById<ImageView>(R.id.crime_photo_view)!!
            builder.setView(resLayout)
            updatePhotoView()

//            globalListener = ViewTreeObserver.OnGlobalLayoutListener {
//                photoWidth = photoView.width
//                photoHeight = photoView.height
//                Log.d(TAG, "$photoWidth, $photoHeight, ${photoFile.path}")
//                updatePhotoView()
//            }
//            photoView.viewTreeObserver.addOnGlobalLayoutListener(globalListener)

            builder.create()
        }
    }

    override fun onDetach() {
        photoView.viewTreeObserver.removeOnGlobalLayoutListener(globalListener)
        globalListener = null
        super.onDetach()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
//            val bitmap = BitmapFactory.decodeFile(photoFile.path)
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }

    companion object {
        fun newInstance(photoFile: File): PhotoViewFragment {
            val args = Bundle().apply {
                putSerializable(ARG_FILE, photoFile)
            }
            return PhotoViewFragment().apply {
                arguments = args
            }
        }
    }
}