package com.vinay.sqlitekotlindemo

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_update_record.*
import java.lang.StringBuilder
import java.util.*

class AddUpdateRecordActivity : AppCompatActivity() {

    //permission constants

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101

    //image picker constatnt
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    //arrays of permissions


    private lateinit var cameraPermission: Array<String> // camera and storage
    private lateinit var storagePermission: Array<String> // only storage


    //variable that will contain data to save in database
    private var imageUri: Uri? = null
    private var name: String? = ""
    private var phone: String? = ""
    private var email: String? = ""
    private var dob: String? = ""
    private var bio: String? = ""


    //actionBar

    private var actionBar: ActionBar? = null

    lateinit var dbHelper: MyDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_record)

        //init actionbar
        actionBar = supportActionBar
        //title of actionBar
        actionBar!!.title = "Add Records"
        //back button in actionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        //init dbHelper class
        dbHelper = MyDbHelper(this)

        //init permission arrays
        cameraPermission = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //click imageview to pick image

        profileImageView.setOnClickListener {

            //show image pick dialog
            imagePickDialog()

        }
        //click save button to save records
        cbSave.setOnClickListener {

            inputData()

        }
    }

    private fun inputData() {

        //get data
        name = "" + etName.text.toString().trim()
        phone = "" + etPhone.text.toString().trim()
        email = "" + etEmail.text.toString().trim()
        dob = "" + etDob.text.toString().trim()
        bio = "" + etBio.text.toString().trim()

        //save data to db

        val timestamp = System.currentTimeMillis()
        val id = dbHelper.insertRecord(
            "" + name,
            "" + imageUri,
            "" + bio,
            "" + phone,
            "" + email,
            "" + dob,
            "" + timestamp,
            "" + timestamp
        )
        Toast.makeText(this, "Record added against id $id", Toast.LENGTH_LONG).show()
    }

    private fun imagePickDialog() {

        //options to display in dialog

        val options = arrayOf("Camera", "Gallery")
        //dialog

        val builder = AlertDialog.Builder(this)
        //title
        builder.setTitle("Pick Image From")
        //set items/options
        builder.setItems(options) { dialog, which ->
            //handle item clicks
            if (which == 0) {
                //Camera click

                if (!checkCameraPermissions()) {
                    //permission not granted
                    requestCameraPermission()
                } else {

                    //permission already granted
                    pickFromCamera()

                }

            } else {
                //Gallery Click
                if (!checkStoragePermission()) {


                    //permission not granted
                    requestStoragePermission()

                } else {
                    //permission already granted

                    pickFromGallery()
                }
            }
        }
        //show dialog
        builder.show()
    }

    private fun pickFromGallery() {

        //pick image from gallery using intent

        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"  // only image to be picked
        startActivityForResult(
            galleryIntent,
            IMAGE_PICK_GALLERY_CODE
        )


    }

    private fun requestStoragePermission() {

        ActivityCompat.requestPermissions(
            this,
            storagePermission,
            STORAGE_REQUEST_CODE
        )
    }

    private fun checkStoragePermission(): Boolean {

        //check if storage permission is enable or not

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")

        //put image uri
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        //intent of open camera

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            cameraIntent,
            IMAGE_PICK_CAMERA_CODE
        )
    }

    private fun requestCameraPermission() {

        ActivityCompat.requestPermissions(
            this,
            cameraPermission,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions(): Boolean {

        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val results1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        return result && results1
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity
        return super.onSupportNavigateUp()
    }

    //handle permission result

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {

                if (grantResults.isNotEmpty()) {
                    //if allowed returns true otherwise false
                    var cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    var storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {

                        pickFromCamera()
                    } else {
                        Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {

                if (grantResults.isNotEmpty()) {

                    // if allowed returns true otherwise false
                    var storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // image pick from camera and gallery will be received here
        if (resultCode == Activity.RESULT_OK) {
            // image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //picked  from gallery
                //crop image
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //picked  from camera
                //crop image
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //cropped image received
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    imageUri = resultUri

                    //set Image
                    profileImageView.setImageURI(resultUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    //error
                    val error = result.error
                    Toast.makeText(this,""+error,Toast.LENGTH_LONG).show()

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)


    }
}
