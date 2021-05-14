package com.example.newsappmvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsappmvvm.data.Article
import com.example.newsappmvvm.databinding.ArticleSingleItemBinding

class NewsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Article, NewsAdapter.ArticleViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ArticleSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentItem = getItem(position)
        return holder.bind(currentItem)
    }

    inner class ArticleViewHolder(private val binding: ArticleSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = getItem(position)
                    if(item != null)
                        listener.onItemClick(item)
                }
            }
        }

        fun bind(article: Article) {
            binding.apply {
                Glide.with(itemView).load(article.urlToImage)
                    .into(articleImageView)
                sourceTextView.text = article.source?.name
                titleTextView.text = article.title
                descTextView.text = article.description
                publishedAtTextView.text = article.publishedAt
            }
        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(article: Article)
    }
}
