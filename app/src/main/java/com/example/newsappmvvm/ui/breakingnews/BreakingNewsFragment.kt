package com.example.newsappmvvm.ui.breakingnews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.R
import com.example.newsappmvvm.adapters.NewsAdapter
import com.example.newsappmvvm.data.Article
import com.example.newsappmvvm.databinding.BreakingNewsFragmentBinding
import com.example.newsappmvvm.ui.NewsViewModel
import com.example.newsappmvvm.util.Constants.Companion.SEARCH_QUERY_SIZE
import com.example.newsappmvvm.util.Resource
import kotlinx.android.synthetic.main.breaking_news_fragment.*

class BreakingNewsFragment : Fragment(R.layout.breaking_news_fragment),
    NewsAdapter.OnItemClickListener {

    private val TAG = "BreakingNewsFragment"

    lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val binding = BreakingNewsFragmentBinding.bind(view)

        val newsAdapter = NewsAdapter(this)

        binding.apply {
            breakingNewsRecyclerview.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@BreakingNewsFragment.scrollListener)
                setHasFixedSize(true)
            }
        }



        viewModel.breakingNews.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsresponse ->
                        newsAdapter.submitList(newsresponse.articles.toList())
                        val totalPages = newsresponse.totalResults / SEARCH_QUERY_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            binding.breakingNewsRecyclerview.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage && !isLoading
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isTotalMoreThanVisible = totalItemCount >= SEARCH_QUERY_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews("in")
                isScrolling = false
            }
        }
    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }
        findNavController().navigate(
            R.id.action_breakingNewsFragment_to_readArticleFragment,
            bundle
        )
    }
}