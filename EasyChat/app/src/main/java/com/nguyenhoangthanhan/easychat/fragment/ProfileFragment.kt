package com.nguyenhoangthanhan.easychat.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.messaging.FirebaseMessaging
import com.nguyenhoangthanhan.easychat.SplashActivity
import com.nguyenhoangthanhan.easychat.databinding.FragmentProfileBinding
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil


class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment_TAG"

    private var currentImage = "currentImageGetFromGallery"


    private var changeImageFlag = "change_image_flag"
    private var changeImage = false

    private var binding: FragmentProfileBinding? = null

    private var currentUserModel: UserModel? = null

    private var imagePickLauncher: ActivityResultLauncher<Intent>? = null

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            //Retrieve the value from the bundle then assign it to selectedImageUri global variable
            selectedImageUri = savedInstanceState.getString(currentImage)?.toUri()
            changeImage = savedInstanceState.getBoolean(changeImageFlag)
            Log.d(TAG, "#onCreate. savedInstanceState.getBoolean(changeImageFlag) = $changeImage")
        }

        imagePickLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    selectedImageUri = data.data
                    Log.d(TAG, "#onCreate.setSelectedImageUri = $selectedImageUri")
                    context?.let {
                        Log.d(TAG, "#onCreate.imagePickLauncher.AndroidUtil.setProfilePic")
                        AndroidUtil.setProfilePic(
                            it,
                            selectedImageUri,
                            binding?.imgProfileAvatar
                        )
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        initData()
        initView()
        initEvents()

        return binding?.root!!
    }

    private fun initData() {

    }

    private fun initView() {
        getUserData()
    }

    private fun getUserData(){
        Log.d(TAG, "#getUserData#start")
        setInProgress(true)
        FirebaseUtil.getCurrentProfilePicStorageRef()?.downloadUrl?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Log.d(TAG, "#getUserData.AndroidUtil.setProfilePic")
                val uri = task.result
                AndroidUtil.setProfilePic(activity?.applicationContext, uri, binding?.imgProfileAvatar)

                if (changeImage){
                    Log.d(TAG, "getUserData.changeImage = true")
                    Log.d(TAG, "getUserData.changeImage.selectedImageUri = $selectedImageUri")
                    context?.let {
                        Log.d(TAG, "#onCreate.savedInstanceState.AndroidUtil.setProfilePic")
                        AndroidUtil.setProfilePic(
                            it,
                            selectedImageUri,
                            binding?.imgProfileAvatar
                        )
                    }
                }
            }
        }

        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener {
            setInProgress(false)
            if (it.isSuccessful) {
                Log.d(TAG, "#initData.isSuccessful = true")
                currentUserModel = it.result.toObject(UserModel::class.java)
//                Log.d(TAG, "#initData.it.result.exists() = " + it.result.exists())
//                Log.d(TAG, "#initData.it.result.toString() = " + it.result.toString())
//                Log.d(
//                    TAG, "#initData.it.result.toObject(UserModel::class.java) = $currentUserModel"
//                            + it.result.toObject(UserModel::class.java)
//                )
                if (currentUserModel != null) {
//                    Log.d(
//                        TAG,
//                        "#initData.currentUserModel.toString() = " + currentUserModel.toString()
//                    )
                    binding?.edtProfileUsername?.setText(currentUserModel?.username)
                    binding?.edtProfilePhone?.setText(currentUserModel?.phone)
                }
            } else {
                Log.d(TAG, "#initData.isSuccessful = false")
            }
        }
        Log.d(TAG, "#getUserData#end")
    }

    private fun initEvents() {
        binding?.btnProfileUpdate?.setOnClickListener {
            updateBtnClick()
        }
        binding?.btnLogout?.setOnClickListener {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    FirebaseUtil.logout()
                    startActivity(Intent(requireContext(), SplashActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            }

        }
        binding?.imgProfileAvatar?.setOnClickListener {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent { intent ->
                    imagePickLauncher?.launch(intent)
                }
        }
    }

    private fun updateBtnClick() {
        val newUsername = binding?.edtProfileUsername?.text.toString()
        if (newUsername.isEmpty() || newUsername.length < 3) {
            binding?.edtProfileUsername?.error = "Username length should be at least 3 chars"
            return
        }
        currentUserModel?.username = newUsername
        setInProgress(true)

        if (selectedImageUri != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef()?.putFile(selectedImageUri!!)?.addOnCompleteListener{ task ->
                updateToFirestore()
            }
        }else{
            updateToFirestore()
        }

    }

    private fun updateToFirestore() {
        currentUserModel?.let {
            FirebaseUtil.currentUserDetails()?.set(it)?.addOnCompleteListener { task ->
                setInProgress(false)
                if (task.isSuccessful) {
                    AndroidUtil.showToastShort(requireContext(), "Updated successfully")
                } else {
                    AndroidUtil.showToastShort(requireContext(), "Updated failed")
                }
            }
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding?.progressBarProfile?.visibility = View.VISIBLE
            binding?.btnLogout?.visibility = View.GONE
        } else {
            binding?.progressBarProfile?.visibility = View.GONE
            binding?.btnLogout?.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(currentImage, selectedImageUri.toString());
        outState.putBoolean(changeImageFlag, true);
    }
}