package com.example.newsappmvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsappmvvm.data.ArticleDatabase
import com.example.newsappmvvm.repository.NewsRepository
import com.example.newsappmvvm.ui.NewsViewModel
import com.example.newsappmvvm.ui.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel =
            ViewModelProvider(this, newsViewModelProviderFactory).get(NewsViewModel::class.java)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navcontroller = navHostFragment.findNavController()
        bottom_navigation_view.setupWithNavController(navcontroller)
    }
}
