package com.example.bookappyt

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.bookappyt.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {

    private val context: Context // hoạt động liên quan đến người dùng
    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList: ArrayList<ModelCategory> //bản sao của categoryList -> sử dụng cho mục đích lọc
    private var filter: FilterCategory? = null //-> triển khai bộ lọc một cách tùy chỉnh
    private lateinit var binding: RowCategoryBinding // liên kết với các phẩn tử giao diện

    // hàm khởi tạo
    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }


    //tạo view holder cho RecycleView (hiển thị các mục có thể tái sử dụng các view được tạo ra trước đó -> tối ưu)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        // tạo ra 1 đối tượng RCB từ tệp layout row_category

        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)

    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        /*Get data, Set data, Handle clicks, etc --- */

        // get data
        var model = categoryArrayList[position]
        var id = model.id
        var category = model.category
        val timestamp = model.timestamp
        val uid = model.uid

        // set data
        holder.categoryTv.text = category

        // handler click, delete category
        holder.deleteBtn.setOnClickListener {
            // confirm before delete
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this category")
                .setPositiveButton("Confirm") { a, d ->
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder)
                }.setNegativeButton("Cancel") { a, d ->
                    a.dismiss()
                }
                .show()
        }

        // handle click, start pdf list admin activity, also pas pdf id, title
        holder.itemView.setOnClickListener{
            val intent = Intent(context, PdfListAdminActivity::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)

        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
        // get id of category to delete
        val id = model.id

        // firebase db > categories > categoryId
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener { e ->
            Toast.makeText(context, "Unable to delete due to ${e.message}...", Toast.LENGTH_SHORT)
                .show()

        }

    }


    override fun getItemCount(): Int {
        return categoryArrayList.size // number of items in list
    }


    // ViewHolder class to hold/ init UI views for row_category.xml
    inner class HolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // init ui views
        var categoryTv: TextView = binding.categoryTv
        var deleteBtn: ImageButton = binding.deleteBtn

    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterCategory(filterList, this)
        }

        return filter as FilterCategory
    }


}