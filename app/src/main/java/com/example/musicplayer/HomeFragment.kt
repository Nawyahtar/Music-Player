package com.example.musicplayer

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.example.musicplayer.databinding.MusicPlayerFragmentBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class HomeFragment : Fragment() ,RecyclerAdapter.onClickListerner{
    private val mutableList = mutableListOf<Song>()
    private lateinit var binding: FragmentHomeBinding
    private val recyclerAdapter = RecyclerAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        runTimePermission()
        binding.recyclerView.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
        return binding.root
    }

    private fun runTimePermission() {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    findSong()

                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {


                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {

                }

            }).check()
    }

    private fun findSong() {
        mutableList.clear()
        val resolver: ContentResolver = requireContext().contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = resolver.query(uri, null, null, null, null)
        when {
            cursor == null -> {
                Toast.makeText(requireContext(), "Null", Toast.LENGTH_SHORT).show()
            }
            !cursor.moveToFirst() -> {
                Toast.makeText(requireContext(), "No songs", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val titleColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val idColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val artistColumn: Int = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                do {
                    val thisId = cursor.getLong(idColumn)
                    val thisTitle = cursor.getString(titleColumn)
                    val thisArtist = cursor.getString(artistColumn)
                    val contentUri: Uri =
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId)
                    val song = Song(
                        title = thisTitle,
                        artist = thisArtist,
                        uri = contentUri

                    )

                    mutableList.add(song)


                } while (cursor.moveToNext())
                recyclerAdapter.submitList(mutableList)

            }
        }
        cursor?.close()
    }

    override fun onItemClick(item: Song, position: Int) {
        findNavController().navigate(MusicPlayerFragmentDirections.actionMusicPlayerFragmentToPlaySongFragment2(item.uri))
    }


}