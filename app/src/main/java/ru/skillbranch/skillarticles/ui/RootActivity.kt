package ru.skillbranch.skillarticles.ui
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottom_bar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {

    private  lateinit var articleViewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupToolbar()
        Log.d("RootActivity", "toolbar 1")
        setupBottomBar()
        setupSubmenu()


        val vmFactory = ViewModelFactory("0")
        articleViewModel = ViewModelProviders.of(this, vmFactory).get(ArticleViewModel::class.java)
        articleViewModel.observeState(this) {
            renderUi(it)
            setupToolbar()
            Log.d("RootActivity", "toolbar 2")
        }

        articleViewModel.observeNotifications(this) {
            renderNotification(it)
        }
    }
    var count = 0

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if(toolbar.childCount > 2)  toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        Log.d("RootActivity", "logo width before ${logo?.width}")
        Log.d("RootActivity", "logo height before ${logo?.height}")

        (logo?.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = dpToIntPx(40)
            it.height = dpToIntPx(40)
            it.marginEnd = dpToIntPx(16)
            logo.layoutParams = it
        }
        count++

        Log.d("RootActivity", "logo width after ${logo?.width}")
        Log.d("RootActivity", "logo height after ${logo?.height}")
        Log.d("RootActivity", count.toString())

    }

    private fun renderUi(data: ArticleViewModel.ArticleState) {
        // bind submenu state
        btn_settings.isChecked = data.isShowMenu
        if(data.isShowMenu) submenu.open() else submenu.close()

        // bind article person data
        btn_like.isChecked = data.isLike
        btn_bookmark.isChecked = data.isBookmark

        // bind submenu views
        switch_mode.isChecked = data.isDarkMode
        delegate.localNightMode = if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (data.isBigText) {
            tv_text_content.textSize = 18f
            btn_text_up.isChecked = true
            btn_text_down.isChecked = false
        } else {
            tv_text_content.textSize = 14f
            btn_text_up.isChecked = false
            btn_text_down.isChecked = true
        }

        // bind content
        tv_text_content.text = if(data.isLoadingContent) "Loading..." else data.content.first() as String

        //bind toolbar
        toolbar.title = data.title ?: "Skill Articles"
        toolbar.subtitle = data.category ?: "loading..."
        if (data.categoryIcon != null) toolbar.logo = getDrawable(data.categoryIcon as Int)
//        val logo = toolbar.logo
//        logo.setBounds(1,1,1,1)
        Log.d("RootActivity", "logo bind")
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(coordinator_container, notify.message, Snackbar.LENGTH_LONG)

        when(notify) {
            is Notify.TextMessage -> { /* nothing */ }
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler?.invoke()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }

        snackbar.show()
    }

    private fun setupBottomBar() {
        btn_like.setOnClickListener { articleViewModel.handleLike() }
        btn_bookmark.setOnClickListener { articleViewModel.handleBookmark() }
        btn_share.setOnClickListener { articleViewModel.handleShare() }
        btn_settings.setOnClickListener { articleViewModel.handleToggleMenu() }
    }

    private fun setupSubmenu() {
        btn_text_up.setOnClickListener { articleViewModel.handleUpText() }
        btn_text_down.setOnClickListener { articleViewModel.handleDownText() }
        switch_mode.setOnClickListener { articleViewModel.handleNightMode() }
    }
}