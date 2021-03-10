package ceo.dog.dogapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ceo.dog.dogapp.R
import ceo.dog.dogapp.databinding.ItemBreedBinding
import ceo.dog.dogapp.databinding.MainFragmentBinding
import ceo.dog.dogapp.domain.model.Breed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

class MainFragment : Fragment(R.layout.main_fragment) {

    private val binding by bindingDelegate(MainFragmentBinding::bind)

    private val viewModel: MainViewModel by activityViewModels()

    private val adapter by lazy { BreedAdapter { viewModel.breedDescription(it.name) } }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainListRV.adapter = adapter
        binding.title.setText(R.string.all_breed_title)
        lifecycleScope.launchWhenResumed {
            viewModel.state.collectLatest {
                binding.progress.gone()
                adapter.setData(it.dogList)
            }
        }
        lifecycleScope.launchWhenResumed {
            viewModel.viewEvents.collect {
                binding.progress.gone()
                when (it) {
                    is MainViewModel.UIEvents.Loading -> binding.progress.visible()
                    is MainViewModel.UIEvents.Error -> toast(it.massage)
                    is MainViewModel.UIEvents.BreedDescriptionLoaded -> findNavController().navigate(R.id.action_mainFragment_to_breedDescriptionFragment)
                }
            }
        }
    }
}

private class BreedAdapter(private val onBreedClick: (Breed) -> Unit) : RecyclerView.Adapter<BreedAdapter.VH>() {

    private val items = mutableListOf<Breed>()

    fun setData(list: List<Breed>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        ItemBreedBinding.inflate(
            LayoutInflater
                .from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[holder.adapterPosition])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemBreedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Breed) {
            with(binding) {
                breedBtn.text = item.name
                breedBtn.setOnClickListener { onBreedClick.invoke(item) }
                subBreedList.adapter = BreedAdapter(onBreedClick).apply { setData(item.subBreed) }
            }
        }
    }
}