package com.example.matheusmaxwellmeireles.borrowme




import android.app.AlertDialog
import android.app.LauncherActivity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baoyz.swipemenulistview.SwipeMenuItem
//import javax.swing.text.StyleConstants.setIcon
import android.graphics.drawable.ColorDrawable
import android.support.v7.view.menu.ListMenuItemView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuListView
import java.text.FieldPosition


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HistoryFragment : Fragment() {

    var list: SwipeMenuListView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        list = view.findViewById(R.id.list)
        val listBorrows = MyApplication.database!!.BorrowDAO().getAllBorrow()
        val listItems = MyApplication.database!!.ItemDAO().getAllItems()
        val listPersons = MyApplication.database!!.PersonDAO().getAllPerson()
        var arrayAdapter = MyAdapter(context!!, listBorrows, listItems, listPersons)
        val edtSearch = view.findViewById<EditText>(R.id.edtSearch)
        registerForContextMenu(list)
        list?.adapter = arrayAdapter

        list?.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(context, ActivityDetailItem::class.java)
            intent.putExtra("idBorrow", listBorrows.get(position).id)
            startActivity(intent)
        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            val listBorrowSearch = ArrayList<Entities.Borrow>()
            override fun afterTextChanged(s: Editable?) {
                for (i in listItems){
                    if(i.nome!!.startsWith(s.toString())){
                        for (e in listBorrows){
                            if(e.idItem == i.idItem){
                                listBorrowSearch.add(e)
                            }
                        }
                    }
                }
                if(listBorrowSearch!=null){
                    var arrayAdapter2 = MyAdapter(context!!, listBorrowSearch.toList(), listItems, listPersons)
                    listBorrowSearch.clear()
                    registerForContextMenu(list)
                    //list?.adapter = null
                    list?.adapter = arrayAdapter2
                }
                if(s?.toString().isNullOrEmpty()){
                    registerForContextMenu(list)
                    list?.adapter = arrayAdapter
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })


        val creator = SwipeMenuCreator { menu ->
            // create "delete" item
            val deleteItem = SwipeMenuItem(
                context
            )
            // set item background
            deleteItem.background = ColorDrawable(
                Color.rgb(
                    0xF9,
                    0x3F, 0x25
                )
            )
            // set item width
            deleteItem.width = 170
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete_black_24dp)
            // add to menu
            menu.addMenuItem(deleteItem)
        }

        list?.setMenuCreator(creator)

        list!!.setOnMenuItemClickListener { position, menu, index ->
            when (index) {
                0 -> deleteBorrow(position, listBorrows)

            }
            // false : close the menu; true : not close the menu
            false
        }

        // Inflate the layout for this fragment
        return view
    }

    fun deleteBorrow(position: Int, listBorrow: List<Entities.Borrow>){
        val borrowDelete = listBorrow.get(position) as Entities.Borrow

        MyApplication.database!!.BorrowDAO().deleteBorrow(borrowDelete)
        refreshList()
    }

    fun refreshList (){
        val nextFrag = HistoryFragment()
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.frame, nextFrag, "findThisFragment")
            .addToBackStack(null)
            .commit()
    }




}
