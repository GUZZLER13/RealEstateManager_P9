package com.example.realestatemanager.ui.home

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
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
import com.example.realestatemanager.ui.details.DetailsActivity
import com.example.realestatemanager.ui.details.DetailsFragment
import com.example.realestatemanager.utils.Constants.CODE_DOLLAR
import com.example.realestatemanager.utils.Constants.CODE_EURO
import com.example.realestatemanager.utils.Utils
import java.io.File


class RealEstateAdapter :
    RecyclerView.Adapter<RealEstateAdapter.ViewHolder>() {
    var context: Context? = null
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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRealestateBinding
            .inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ItemRealestateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var context: Context = binding.root.context
        val mainActivity: MainActivity = context as MainActivity

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

            if (viewHolder.context.resources.getBoolean(R.bool.large_layout)) {
                val bundle = Bundle()
                bundle.putLong("idRealEstate", item.realEstate.idRealEstate)
                viewHolder.mainActivity.supportFragmentManager.beginTransaction()
                    .add(R.id.frame_layout_2, DetailsFragment::class.java, bundle)
                    .commit()
            } else {
                val intent = Intent(viewHolder.context, DetailsActivity::class.java)
                intent.putExtra("idRealEstate", item.realEstate.idRealEstate)
                viewHolder.context.startActivity(intent)
            }
        }

        //ASPECT POUR THEME SOMBRE
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                if (indexSelected == position) {
                    binding.constraintlayoutItemRealestate.setBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.context,
                            R.color.teal_700
                        )
                    )
                    viewHolder.binding.textRealEstateCity.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.textRealEstateType.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.textRealEstatePrice.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.imageCurrency.setColorFilter(Color.parseColor("#FFFAFA"))
                } else {


                    binding.constraintlayoutItemRealestate.setBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.context,
                            R.color.grey
                        )
                    )
                    viewHolder.binding.textRealEstateCity.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.textRealEstateType.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.textRealEstatePrice.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.imageCurrency.setColorFilter(Color.parseColor("#FF3700B3"))
                }
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                if (indexSelected == position) {
                    binding.constraintlayoutItemRealestate.setBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.context,
                            R.color.teal_700
                        )
                    )
                    viewHolder.binding.textRealEstateCity.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.textRealEstateType.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.textRealEstatePrice.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.imageCurrency.setColorFilter(Color.parseColor("#FFFAFA"))
                } else {


                    binding.constraintlayoutItemRealestate.setBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.context,
                            R.color.white
                        )
                    )
                    viewHolder.binding.textRealEstateCity.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.textRealEstateType.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.textRealEstatePrice.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.imageCurrency.setColorFilter(Color.parseColor("#FF3700B3"))
                }
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                if (indexSelected == position) {
                    binding.constraintlayoutItemRealestate.setBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.context,
                            R.color.teal_700
                        )
                    )
                    viewHolder.binding.textRealEstateCity.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.textRealEstateType.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.textRealEstatePrice.setTextColor(Color.parseColor("#FFFAFA"))
                    viewHolder.binding.imageCurrency.setColorFilter(Color.parseColor("#FFFAFA"))
                } else {


                    binding.constraintlayoutItemRealestate.setBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.context,
                            R.color.white
                        )
                    )
                    viewHolder.binding.textRealEstateCity.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.textRealEstateType.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.textRealEstatePrice.setTextColor(Color.parseColor("#FF3700B3"))
                    viewHolder.binding.imageCurrency.setColorFilter(Color.parseColor("#FF3700B3"))
                }
            }
        }
        if (item.photos?.isNotEmpty() == true) {
            val file = File(
                viewHolder.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                item.photos[0].path
            )
            Glide.with(binding.root)
                .load(file)
                .centerCrop()
                .dontAnimate()
                .into(binding.imageRealEstate)
        }


        if (item.realEstate.propertyStatus) {
            binding.textImageRealEstate.setBackgroundResource(R.color.red)
            binding.textImageRealEstate.text = "SOLD"
        } else {
            binding.textImageRealEstate.setBackgroundResource(R.color.green)
            binding.textImageRealEstate.text = "FOR SALE"

        }
    }
}








