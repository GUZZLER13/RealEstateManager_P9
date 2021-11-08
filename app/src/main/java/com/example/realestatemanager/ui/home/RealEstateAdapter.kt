package com.example.realestatemanager.ui.home

import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ItemRealestateBinding
import com.example.realestatemanager.domain.relation.RealEstateWithPhoto
import com.example.realestatemanager.utils.Constants.CODE_DOLLAR
import com.example.realestatemanager.utils.Constants.CODE_EURO
import com.example.realestatemanager.utils.Utils
import java.io.File


class RealEstateAdapter :
    RecyclerView.Adapter<RealEstateAdapter.ViewHolder>() {
    private var indexSelected = -1
    var data = listOf<RealEstateWithPhoto>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var currency: Int = CODE_DOLLAR
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemRealestateBinding
            .inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ItemRealestateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var context: Context = binding.root.context
    }


    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = data[position]
        val binding = viewHolder.binding


        when (currency) {
            CODE_DOLLAR -> {
                binding.imageCurrency.setImageDrawable(
                    AppCompatResources.getDrawable(
                        viewHolder.context,
                        R.drawable.ic_currency_dollar_black_24dp
                    )
                )
                binding.textRealEstatePrice.text = item.realEstate.price.toString()
            }
            CODE_EURO -> {
                binding.imageCurrency.setImageDrawable(
                    AppCompatResources.getDrawable(
                        viewHolder.context,
                        R.drawable.ic_currency_euro_black_24dp
                    )
                )
                binding.textRealEstatePrice.text =
                    Utils.convertDollarToEuro(item.realEstate.price).toString()
            };

        }
        binding.textRealEstateCity.text = item.realEstate.address
        binding.textRealEstateType.text = item.realEstate.type

        binding.constraintlayoutItemRealestate.setOnClickListener {
            indexSelected = position
            notifyDataSetChanged()

        }
        if (indexSelected == position) {
            binding.constraintlayoutItemRealestate.setBackgroundColor(
                ContextCompat.getColor(
                    viewHolder.context,
                    R.color.teal_700
                )
            )
        } else {
            binding.constraintlayoutItemRealestate.setBackgroundColor(
                ContextCompat.getColor(
                    viewHolder.context,
                    R.color.white
                )
            )
        }
        if (item.photos?.isNotEmpty() == true) {
            val file = File(
                viewHolder.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                item.photos[0].path
            )
            Glide.with(binding.root)
                .load(file)
                .centerCrop()
                .into(binding.imageRealEstate)
        }
    }
}








