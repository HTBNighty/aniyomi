package eu.kanade.tachiyomi.ui.recent.animeupdates

import android.view.View
import androidx.core.view.isVisible
import coil.clear
import coil.loadAny
import eu.kanade.tachiyomi.animesource.LocalAnimeSource
import eu.kanade.tachiyomi.databinding.AnimeUpdatesItemBinding
import eu.kanade.tachiyomi.ui.anime.episode.base.BaseEpisodeHolder
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Holder that contains episode item
 * UI related actions should be called from here.
 *
 * @param view the inflated view for this holder.
 * @param adapter the adapter handling this holder.
 * @param listener a listener to react to single tap and long tap events.
 * @constructor creates a new recent episode holder.
 */
class AnimeUpdatesHolder(private val view: View, private val adapter: AnimeUpdatesAdapter) :
    BaseEpisodeHolder(view, adapter) {

    private val binding = AnimeUpdatesItemBinding.bind(view)

    init {
        binding.mangaCover.setOnClickListener {
            adapter.coverClickListener.onCoverClick(bindingAdapterPosition)
        }

        binding.download.setOnClickListener {
            onAnimeDownloadClick(it, bindingAdapterPosition)
        }
    }

    fun bind(item: AnimeUpdatesItem) {
        // Set episode title
        binding.chapterTitle.text = item.episode.name

        // Set anime title
        binding.mangaTitle.text = item.anime.title

        // Check if episode is read and set correct color
        if (item.episode.seen) {
            binding.chapterTitle.setTextColor(adapter.seenColor)
            binding.mangaTitle.setTextColor(adapter.seenColor)
        } else {
            binding.chapterTitle.setTextColor(adapter.unseenColor)
            binding.mangaTitle.setTextColor(adapter.unseenColor)
        }

        // Set episode status
        binding.download.isVisible = item.anime.source != LocalAnimeSource.ID
        Observable.interval(50, TimeUnit.MILLISECONDS)
            .flatMap {
                Observable.just(item)
            }
            // Keep only the latest emission to avoid backpressure.
            .onBackpressureLatest()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // Update the view
                binding.download.setState(item.status, it.progress)
            }

        // Set cover
        binding.mangaCover.clear()
        binding.mangaCover.loadAny(item.anime)
    }
}
