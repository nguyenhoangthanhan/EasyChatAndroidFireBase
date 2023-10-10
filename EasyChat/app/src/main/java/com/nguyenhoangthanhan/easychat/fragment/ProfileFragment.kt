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
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nguyenhoangthanhan.easychat.SplashActivity
import com.nguyenhoangthanhan.easychat.databinding.FragmentProfileBinding
import com.nguyenhoangthanhan.easychat.model.UserModel
import com.nguyenhoangthanhan.easychat.util.AndroidUtil
import com.nguyenhoangthanhan.easychat.util.FirebaseUtil


class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment_TAG"

    private lateinit var binding: FragmentProfileBinding

    private var currentUserModel: UserModel? = null

    private var imagePickLauncher: ActivityResultLauncher<Intent>? = null

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    selectedImageUri = data.data
                    context?.let { AndroidUtil.setProfilePic(it, selectedImageUri, binding.imgProfileAvatar) }
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

        return binding.root
    }

    private fun initData() {

    }

    private fun initView() {
        getUserData()
    }

    private fun getUserData(){
        setInProgress(true)
        FirebaseUtil.getCurrentProfilePicStorageRef()?.downloadUrl?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                val uri = task.result
                AndroidUtil.setProfilePic(requireContext(), uri, binding.imgProfileAvatar)
            }
        }

        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener {
            setInProgress(false)
            if (it.isSuccessful) {
                Log.d(TAG, "#initData.isSuccessful = true")
                currentUserModel = it.result.toObject(UserModel::class.java)
                Log.d(TAG, "#initData.it.result.exists() = " + it.result.exists())
                Log.d(TAG, "#initData.it.result.toString() = " + it.result.toString())
                Log.d(
                    TAG, "#initData.it.result.toObject(UserModel::class.java) = $currentUserModel"
                            + it.result.toObject(UserModel::class.java)
                )
                if (currentUserModel != null) {
                    Log.d(
                        TAG,
                        "#initData.currentUserModel.toString() = " + currentUserModel.toString()
                    )
                    binding.edtProfileUsername.setText(currentUserModel?.username)
                    binding.edtProfilePhone.setText(currentUserModel?.phone)
                }
            } else {
                Log.d(TAG, "#initData.isSuccessful = false")
            }
        }
    }

    private fun initEvents() {
        binding.btnProfileUpdate.setOnClickListener {
            updateBtnClick()
        }
        binding.btnLogout.setOnClickListener {
            FirebaseUtil.logout()
            startActivity(Intent(requireContext(), SplashActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        binding.imgProfileAvatar.setOnClickListener {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent { intent ->
                    imagePickLauncher?.launch(intent)
                }
        }
    }

    private fun updateBtnClick() {
        val newUsername = binding.edtProfileUsername.text.toString()
        if (newUsername.isEmpty() || newUsername.length < 3) {
            binding.edtProfileUsername.error = "Username length should be at least 3 chars"
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
            binding.progressBarProfile.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE
        } else {
            binding.progressBarProfile.visibility = View.GONE
            binding.btnLogout.visibility = View.VISIBLE
        }
    }
}