package app.android.serv.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import app.android.serv.R
import kotlinx.android.synthetic.main.issue_description.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kombo on 16/08/2018.
 */
class DetailsActivity : AppCompatActivity() {

    private var currentPhotoPath: String? = null

    private var permissionsToRequest: ArrayList<String>? = null
    private var permissionsRejected = ArrayList<String>()
    private var permissions = ArrayList<String>()

    companion object {
        const val PERMISSIONS_RESULT = 102
        const val REQUEST_TAKE_PHOTO = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.issue_description)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getPermissions()
    }

    private fun getPermissions() {
        permissions.add(Manifest.permission.CAMERA)

        permissionsToRequest = findUnaskedPermissions(permissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (permissionsToRequest!!.isNotEmpty())
                requestPermissions(permissionsToRequest!!.toArray(arrayOfNulls<String>(permissionsToRequest!!.size)), PERMISSIONS_RESULT)
    }

    private fun findUnaskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()

        wanted.forEach {
            if (!hasPermission(it))
                result.add(it)
        }

        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
        }

        return true
    }

    private fun canMakeSmores(): Boolean = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //ensure there's a camera activity to handle the intent
        if (intent.resolveActivity(packageManager) != null) {
            //create a file where the photo should go
            var photoFile: File? = null

            try {
                photoFile = createImageFile()
            } catch (io: IOException) {
                Timber.e("An error occurred while creating the file")
                io.printStackTrace()
            }

            photoFile?.let {
                val uri = FileProvider.getUriForFile(this, "app.android.serv.fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun createImageFile(): File? {
        //create an image file
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        )

        currentPhotoPath = image.absolutePath
        return image
    }

    private fun setPic() {
        //get the dimensions of the view
        val targetWidth = image.width
        val targetHeight = image.height

        //get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        //determine how much to scale the image
        val scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight)

        //decode the image file into a bitmap sized to fill the view
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        image.setImageBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Timber.e("Setting pic")
            currentPhotoPath?.let {
                setPic()
                addPhotoToGallery()
            }
        }
    }

    private fun addPhotoToGallery() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(currentPhotoPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.upload_image -> {
                takePicture()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (hasPermission(perms)) {
                    } else {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    DialogInterface.OnClickListener { _, _ ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(permissionsRejected.toArray(arrayOfNulls<String>(permissionsRejected.size)), PERMISSIONS_RESULT)
                                        }
                                    })
                            return
                        }
                    }
                }
            }
        }
    }
}