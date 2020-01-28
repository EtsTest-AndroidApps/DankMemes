package com.gelostech.dankmemes.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gelostech.dankmemes.R
import com.gelostech.dankmemes.data.Status
import com.gelostech.dankmemes.data.models.Meme
import com.gelostech.dankmemes.data.models.User
import com.gelostech.dankmemes.databinding.ActivityMemeBinding
import com.gelostech.dankmemes.ui.base.BaseActivity
import com.gelostech.dankmemes.ui.callbacks.MemesCallback
import com.gelostech.dankmemes.ui.viewmodels.MemesViewModel
import com.gelostech.dankmemes.utils.AppUtils
import com.gelostech.dankmemes.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_meme.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class MemeActivity : BaseActivity() {
    private lateinit var binding: ActivityMemeBinding
    private lateinit var memeId: String
    private val memesViewModel: MemesViewModel by inject()
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusBar()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_meme)
        binding.lifecycleOwner = this
        binding.callback = memesCallback

        memeId = intent.getStringExtra(Constants.MEME_ID)!!

        initViews()
        initMemeObserver()
        memesViewModel.fetchMeme(memeId)
    }

    private fun initViews() {
        setSupportActionBar(memeToolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Initialize observer for Meme LiveData
     */
    private fun initMemeObserver() {
        memesViewModel.memeResponseLiveData.observe(this, Observer { response ->
            when (response.status) {
                Status.LOADING -> binding.loading = true

                Status.SUCCESS -> {
                    binding.loading = false
                    response.data?.meme?.subscribeBy(
                            onNext = { binding.meme = it },
                            onError = { Timber.e("Meme deleted") }
                    )?.addTo(disposables)
                }

                Status.ERROR -> {
                    binding.loading = false
                }
            }
        })
    }

    private val memesCallback = object : MemesCallback {
        override fun onMemeClicked(view: View, meme: Meme) {
            val memeId = meme.id!!

            when(view.id) {
                R.id.memeComment -> {
                    val i = Intent(this@MemeActivity, CommentActivity::class.java)
                    i.putExtra(Constants.MEME_ID, memeId)
                    startActivity(i)
                    overridePendingTransition(R.anim.enter_b, R.anim.exit_a)
                }

                R.id.memeFave -> {
                    AppUtils.animateView(view)
                    memesViewModel.faveMeme(memeId, getUid())
                }

                R.id.memeLike -> {
                    AppUtils.animateView(view)
                    memesViewModel.likeMeme(memeId, getUid())
                }

                else -> Timber.e("No action")
            }
        }

        override fun onProfileClicked(view: View, user: User) {
            // Not used here
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        AppUtils.animateEnterLeft(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
