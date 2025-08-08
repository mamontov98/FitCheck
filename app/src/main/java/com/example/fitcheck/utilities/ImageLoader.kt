package com.example.fitcheck.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.fitcheck.R
import java.lang.ref.WeakReference

class ImageLoader private constructor(context: Context) {
    private val contextRef = WeakReference(context)

    //Loads a drawable into an ImageView with optional placeholder
    fun loadImage(source: Drawable,
                  imageView: ImageView,
                  placeholder: Int = R.drawable.unavailable_photo){
        contextRef.get()?.let {
                context ->
            Glide
                .with(context)
                .load(source)
                .centerCrop()
                .placeholder(placeholder)
                .into(imageView);
        }
    }

    //Loads an image from a String URL or file path into an ImageView with optional placeholder
    fun loadImage(source: String,
                  imageView: ImageView,
                  placeholder: Int = R.drawable.unavailable_photo){
        contextRef.get()?.let {
                context ->
            Glide
                .with(context)
                .load(source)
                .centerCrop()
                .placeholder(placeholder)
                .into(imageView);
        }
    }

    companion object {
        @Volatile
        private var instance: ImageLoader? = null

        //Initializes the singleton instance. Must be called once at app startup
        fun init(context: Context): ImageLoader {
            return instance ?: synchronized(this) {
                instance ?: ImageLoader(context).also { instance = it }
            }
        }

        //Returns the singleton instance. Throws if not initialized
        fun getInstance(): ImageLoader {
            return instance ?: throw IllegalStateException(
                "ImageLoader must be initialized by calling init(context) before use."
            )
        }
    }

}