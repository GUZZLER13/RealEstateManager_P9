package com.example.realestatemanager.ui.update

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realestatemanager.databinding.ItemUpdatePhotoBinding
import com.example.realestatemanager.domain.models.Photo
import java.io.File

class PhotoUpdateAdapter(private val listenerPhoto: (Photo) -> Unit) :
    RecyclerView.Adapter<PhotoUpdateAdapter.ViewHolder>() {

    var data = listOf<Photo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding = ItemUpdatePhotoBinding
            .inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding, listenerPhoto)
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = data[position]
        viewHolder.bind(item)
    }


    class ViewHolder(val binding: ItemUpdatePhotoBinding, val listenerPhoto: (Photo) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Photo) {
            val context = binding.root.context
            binding.label.text = item.label
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                item.path
            )
            Glide.with(binding.root)
                .load(file)
                .centerCrop()
                .dontAnimate()// For photo display correctly
                .into(binding.imgViewPhoto)
            binding.buttonEdit.setOnClickListener {
                listenerPhoto(item)
            }
        }
    }

    override fun getItemCount() = data.size
}