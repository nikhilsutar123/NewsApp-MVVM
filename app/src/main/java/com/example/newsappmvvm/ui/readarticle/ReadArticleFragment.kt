package com.example.newsappmvvm.ui.readarticle

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.R
import com.example.newsappmvvm.databinding.ReadArticleFragmentBinding
import com.example.newsappmvvm.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ReadArticleFragment : Fragment(R.layout.read_article_fragment) {

    lateinit var viewModel: NewsViewModel
    val args: ReadArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val binding = ReadArticleFragmentBinding.bind(view)
        val article = args.article

        binding.apply {
            webview.apply {
                webViewClient = WebViewClient()
                loadUrl(article.url!!)
            }

            fabFavorite.setOnClickListener {
                viewModel.saveArticle(article)
                Snackbar.make(view,"Article saved",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}