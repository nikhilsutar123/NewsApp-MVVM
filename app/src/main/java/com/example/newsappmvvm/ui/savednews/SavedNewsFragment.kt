package com.example.newsappmvvm.ui.savednews

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.R
import com.example.newsappmvvm.adapters.NewsAdapter
import com.example.newsappmvvm.data.Article
import com.example.newsappmvvm.databinding.SavedNewsFragmentBinding
import com.example.newsappmvvm.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment(R.layout.saved_news_fragment), NewsAdapter.OnItemClickListener {
    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val binding = SavedNewsFragmentBinding.bind(view)

        val newsAdapter = NewsAdapter(this)

        binding.apply {
            savedNewsRecyclerview.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)
            }
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.submitList(articles)
        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.currentList[position]
                viewModel.deleteArticle(article)

                Snackbar.make(view,"Article deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO"){
                        viewModel.saveArticle(article)
                    }
                }.show()
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.savedNewsRecyclerview)
        }
    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article",article)
        }
        findNavController().navigate(R.id.action_savedNewsFragment_to_readArticleFragment,bundle)    }
}