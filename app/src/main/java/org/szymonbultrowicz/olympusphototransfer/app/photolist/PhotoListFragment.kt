package org.szymonbultrowicz.olympusphototransfer.app.photolist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.szymonbultrowicz.olympusphototransfer.R
import org.szymonbultrowicz.olympusphototransfer.app.CameraClientConfigFactory
import org.szymonbultrowicz.olympusphototransfer.lib.client.CameraClient
import org.szymonbultrowicz.olympusphototransfer.lib.client.PhotoInfo
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.time.ZoneOffset
import java.util.logging.Logger

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PhotoListFragment.OnListFragmentInteractionListener] interface.
 */
class PhotoListFragment : Fragment() {

    private val logger = Logger.getLogger(PhotoListFragment::class.java.name)

    private var columnCount = 2

    private var listener: OnListFragmentInteractionListener? = null

    private var myAdapter: MyItemRecyclerViewAdapter? = null

    private var camera: CameraClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        camera = CameraClient(CameraClientConfigFactory.fromPreferences(
            PreferenceManager.getDefaultSharedPreferences(context)
        ))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_list, container, false)

        val fragment = this
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                myAdapter = MyItemRecyclerViewAdapter(
                    emptyList(),
                    fragment,
                    listener
                )
                adapter = myAdapter
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            refreshFiles()
        }
    }

    private suspend fun refreshFiles() {
        try {
            val files = fetchCameraFiles(
                createCameraClient()
            )
            /// TODO: improve sort
            val sortedFiles = files.sortedByDescending { it.dateTaken.atZone(ZoneOffset.UTC).toEpochSecond() }
            myAdapter?.updateData(sortedFiles)
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException,
                is ConnectException -> {
                    Toast.makeText(context, "Failed to connect to the camera", Toast.LENGTH_SHORT)
                        .show()
                    logger.warning("Failed to connec to the camera $e")
                }
                else -> {
                    Toast.makeText(context, "Unknown error: $e", Toast.LENGTH_SHORT)
                        .show()
                    logger.severe("Unknown error $e")
                }
            }
        }
    }

    private fun createCameraClient(): CameraClient {
        return CameraClient(CameraClientConfigFactory.fromPreferences(
            PreferenceManager.getDefaultSharedPreferences(context)
        ))
    }

    private suspend fun fetchCameraFiles(camera: CameraClient?): List<PhotoInfo> = withContext(Dispatchers.IO) {
        if (camera == null) {
            logger.warning("Camera object empty in onResume step")
            return@withContext emptyList<PhotoInfo>()
        }

        logger.info("Fetching camera files")

        return@withContext camera.listPhotos()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: PhotoInfo?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PhotoListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
