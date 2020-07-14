package com.hasz.gymrats.app.service

import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import java.util.*

object GService {

  fun uploadImage(uri: Uri, handler: (Result<String>) -> Unit) {
    val uuid = UUID.randomUUID().toString()
    val storageRef = FirebaseStorage.getInstance().reference
    val photoRef = storageRef.child("${uuid}.jpg")
    val metadata = StorageMetadata.Builder()
      .setContentType("image/jpg")
      .build()

    val task = photoRef.putFile(uri, metadata)

    task.addOnFailureListener { error ->
      handler(Result.failure(error))
    }

    task.addOnSuccessListener { _ ->
      val urlTask = photoRef.downloadUrl

      urlTask.addOnFailureListener { error ->
        handler(Result.failure(error))
      }

      urlTask.addOnSuccessListener { uri ->
        handler(Result.success(uri.toString()))
      }
    }

//          photoRef.putData(data, metadata: metadata) { metadata, error in

//            photoRef.downloadURL {
//              url, error in
//
//            }
  }
}