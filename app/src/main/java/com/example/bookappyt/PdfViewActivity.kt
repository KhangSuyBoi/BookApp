package com.example.bookappyt

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.example.bookappyt.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.DocumentActivity
import java.io.File

class PdfViewActivity : AppCompatActivity() {
    // View binding
    private lateinit var binding: ActivityPdfViewBinding

    // book id tu intent
    private var bookId = ""

    //TAG
    private companion object{ // xac dinh ngu canh
        const val TAG = "PDF_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails(this) // lay id book cua intent va load id book tuong ung

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadBookDetails(context: Context) {
        Log.d(TAG, "loadBookDetails: Get Pdf URL from db")
        // B1: lay url theo id book
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId) // tham chieu den nut con Book id
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfUrl = snapshot.child("url").value // lay url tuong ung id book
                    Log.d(TAG, "onDataChange: PDF_URL: $pdfUrl")

                    // B2: load url cua pdf trong db
                    openHttpDocument(context, "$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Get Pdf from firebase storage using URL")
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes ->
                Log.d(TAG, "loadBookFromUrl: pdf got from url")

                // load pdf
                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false) // tat tinh nang luot ngang
                    .onPageChange{page, pageCount -> // khi trang thay doi
                        //  thay doi so trang hien tai
                        val currentPage = page + 1
                        binding.toolbarSubtitleTv.text = "$currentPage/$pageCount"
                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
                    }
                    .onError {t ->
                        Log.d(TAG, "loadBookFormUrl: ${t.message}")
                    }
                    .onPageError {_ , t ->
                        Log.d(TAG, "loadBookFormUrl: ${t.message}")
                    }.load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "loadBookFromUrl: Failed to get due to ${e.message}")
                binding.progressBar.visibility = View.GONE
            }
    }


    /**
     * Open a local document given a path
     *
     * @param context the context to start the document reader
     * @param localFilePath local path to a document
     */
    private fun openLocalDocument(context: Context, localFilePath: String) {
        val localFile = Uri.fromFile(File(localFilePath))
        presentDocument(localFile)
    }

    /**
     * Open a document given a Content Uri
     *
     * @param context the context to start the document reader
     * @param contentUri a content URI that references a document
     */
    private fun openContentUriDocument(context: Context, contentUri: Uri) {
        presentDocument(contentUri)
    }

    /**
     * Open a document from an HTTP/HTTPS url
     *
     * @param context the context to start the document reader
     * @param url an HTTP/HTTPS url to a document
     */
    private fun openHttpDocument(context: Context, url: String) {
        // Tạo một cấu hình cho việc xem tài liệu
        val config = ViewerConfig.Builder()
            .openUrlCachePath(this.getCacheDir().absolutePath) // Đặt đường dẫn thư mục để lưu tài liệu tạm thời
            .build()

        val fileLink = Uri.parse(url)

        // Mở tài liệu từ URL sử dụng cấu hình đã tạo
        presentDocument(fileLink, config)
    }


    /**
     *
     * @param context the context to start the document reader
     * @param fileResId resource id to a document in res/raw
     */
    private fun openRawResourceDocument(context: Context, @RawRes fileResId: Int) {
        val intent =
            DocumentActivity.IntentBuilder.fromActivityClass(this, DocumentActivity::class.java)
                .withFileRes(fileResId)
                .usingNewUi(true)
                .build()
        startActivity(intent)
    }

    private fun presentDocument(uri: Uri) {
        presentDocument(uri, null)
    }

    private fun presentDocument(uri: Uri, config: ViewerConfig?) {
        var config = config
        if (config == null) { // neu config null se khoi tao 1 config moi
            config = ViewerConfig.Builder().saveCopyExportPath(this.cacheDir.absolutePath).build()
        }
        val intent = // tao 1 Intent de mo tai lieu
            DocumentActivity.IntentBuilder.fromActivityClass(this, DocumentActivity::class.java)
                .withUri(uri)
                .usingConfig(config)
                .usingNewUi(true)
                .build()
        startActivity(intent)
    }
}