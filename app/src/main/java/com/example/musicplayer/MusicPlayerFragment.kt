package com.example.musicplayer

import android.Manifest
import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.musicplayer.databinding.MusicPlayerFragmentBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.nio.file.Files

class MusicPlayerFragment : Fragment(){
    private lateinit var binding:MusicPlayerFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate<MusicPlayerFragmentBinding>(inflater,R.layout.music_player_fragment,container,false)
        setUpTabs()
        return binding.root
    }
    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(HomeFragment(), "Home")
        adapter.addFragment(FavouritesFragment(), "Favourites")
        adapter.addFragment(PlaylistFragment(), "Playlist")
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.apply {
            tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_home_24)
            tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_favorite_24)
            tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_queue_music_24)
        }


    }



}
