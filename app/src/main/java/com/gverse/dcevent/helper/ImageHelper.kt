package com.gverse.dcevent.helper

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.gverse.dcevent.R

object ImageHelper {

    fun showImageDialog(context: Context, imageUrl: String) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_image)

        val imageView = dialog.findViewById<ImageView>(R.id.dialogImageView)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.noimage)
            .error(R.drawable.noimage)
            .into(imageView)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
