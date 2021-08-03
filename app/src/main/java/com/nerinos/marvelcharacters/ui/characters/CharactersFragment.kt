package com.nerinos.marvelcharacters.ui.characters

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.nerinos.marvelcharacters.R
import com.nerinos.marvelcharacters.databinding.FragmentCharactersBinding
import com.nerinos.marvelcharacters.ui.characters.adapters.CharactersAdapter
import com.nerinos.marvelcharacters.ui.characters.adapters.MarvelCharacterLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CharactersFragment : Fragment(R.layout.fragment_characters) {
    private val viewModel: MarvelViewModel by viewModels()

    private var _binding:FragmentCharactersBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCharactersBinding.bind(view)

        val adapter = CharactersAdapter()

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
            recyclerView.itemAnimator = null
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MarvelCharacterLoadStateAdapter { adapter.retry() }, // paging knows how to retry
                footer = MarvelCharacterLoadStateAdapter { adapter.retry() }
            )

            buttonRetry.setOnClickListener {
                adapter.retry()
            }

        }

        viewModel.characters.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it) // important to pass lifecycle of a VIEW, not a fragment
        }

        adapter.addLoadStateListener { loadstate ->
            binding.apply {
                progressBar.isVisible = loadstate.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadstate.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadstate.source.refresh is LoadState.Error
                textViewError.isVisible = loadstate.source.refresh is LoadState.Error


                // empty view
                if (loadstate.source.refresh is LoadState.NotLoading &&
                    loadstate.append.endOfPaginationReached &&
                    adapter.itemCount < 1) {
                    // no results to begin with
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }


        setHasOptionsMenu(true)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_characters, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.scrollToPosition(0) // not smooth jump
                    viewModel.searchCharacters(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }
}