package ceo.dog.dogapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ceo.dog.dogapp.R
import ceo.dog.dogapp.databinding.ItemBreedImageBinding
import ceo.dog.dogapp.databinding.MainFragmentBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest

class BreedDescriptionFragment : Fragment(R.layout.main_fragment) {

    private val binding by bindingDelegate(MainFragmentBinding::bind)

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collectLatest {
                binding.progress.gone()
                binding.title.text = it.selectedBreed
                binding.mainListRV.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.mainListRV.adapter = BreedImageAdapter().apply {
                    setData(it.imageList)
                }
            }
        }
    }
}

private class BreedImageAdapter : RecyclerView.Adapter<BreedImageAdapter.VH>() {

    private val items = mutableListOf<String>()

    fun setData(list: List<String>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        ItemBreedImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[holder.adapterPosition])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemBreedImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.image).load(item).into(binding.image)
        }
    }
}