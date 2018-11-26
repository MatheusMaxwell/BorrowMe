package com.example.matheusmaxwellmeireles.borrowme


import android.Manifest
import android.app.*
import android.content.*
import android.content.Context.ALARM_SERVICE
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.util.DateInterval
import android.media.Image
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.app.AlertDialog
import android.support.v7.appcompat.R.id.content
import android.support.v7.appcompat.R.id.end
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_new.*
import kotlinx.android.synthetic.main.layout_dialog_item.*
import kotlinx.android.synthetic.main.layout_dialog_person.*
import java.io.ByteArrayOutputStream
import java.lang.RuntimeException
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class NewFragment : Fragment() {

    var btnAddPerson: Button? = null
    var btnAddItem: Button? = null
    var btnDeletePerson: Button? = null
    var btnDeleteItem: Button? = null
    var mainActivity: MainActivity? = null
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    var imageItem: ImageView? = null
    var spinnerPerson: Spinner? = null
    var spinnerItem: Spinner? = null
    var edtBorrowDate: TextView?= null
    var edtReturnDate: TextView?= null
    var btnCreate: Button? = null
    var idPersonUse: Int? = -1
    var idItemUse: Int? = -1
    var dateBorrowInverse: Int = 0
    var dateReturnInverse: Int = 0
    var CHANNEL_ID: String = "channel_id_borrowme"

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (!(activity is MainActivity)) {
            throw RuntimeException("O contexto nao é da activity")
        }

        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createNotificationChannel()
        val view = inflater.inflate(R.layout.fragment_new, container, false)
        spinnerPerson = view.findViewById(R.id.spPerson)
        spinnerItem = view.findViewById(R.id.spItem)
        btnAddPerson = view.findViewById(R.id.btnAddPerson) as Button
        btnAddItem = view.findViewById(R.id.btnAddItem) as Button
        edtBorrowDate = view.findViewById(R.id.edtBorrowDate)
        edtReturnDate = view.findViewById(R.id.edtReturnDate)
        btnCreate = view.findViewById(R.id.btnCreate)
        btnDeletePerson = view.findViewById(R.id.btnDeletePerson) as Button
        btnDeleteItem = view.findViewById(R.id.btnDeleteItem) as Button

        edtBorrowDate!!.setOnClickListener {
            returnDateBorrow()
        }

        edtReturnDate!!.setOnClickListener {
            returnDateReturn()
        }

        spinnerPerson!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as Entities.Person
                idPersonUse = selectedItem.idPerson

            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
                val listPerson = MyApplication.database!!.ItemDAO().getAllItems()
                if(listPerson.size > 0){
                    idPersonUse = listPerson.get(0).idItem
                }
            }
        }
        spinnerItem!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as Entities.Item
                idItemUse = selectedItem.idItem

            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
                val listItem = MyApplication.database!!.ItemDAO().getAllItems()
                if(listItem.size > 0){
                    idItemUse = listItem.get(0).idItem
                }

            }
        }

        btnDeletePerson!!.setOnClickListener {
            var builder = AlertDialog.Builder(context!!)
            val persons = MyApplication.database!!.PersonDAO().getAllPerson()
            val borrows = MyApplication.database!!.BorrowDAO().getAllBorrow()
            var personBorrow = false
            for (i in borrows){
                if(i.idPerson == idPersonUse){
                    personBorrow = true
                }
            }
            if(personBorrow == false){
                for (i in persons){
                    if(i.idPerson == idPersonUse){
                        builder
                            .setTitle("Delete Person")
                            .setMessage("Are you sure you want to delete "+i.name+"?")
                            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                                MyApplication.database!!.PersonDAO().deletePerson(i)
                                return@OnClickListener

                            }).setNegativeButton(android.R.string.cancel) { dialog, which ->
                                dialog.dismiss()
                            }
                    }
                }

                builder.create().show()
            }
            else{
                showMensagemAlert("Erro", "This person is involved in a borrow.")
            }


        }

        btnDeleteItem!!.setOnClickListener {
            var builder = AlertDialog.Builder(context!!)
            val items = MyApplication.database!!.ItemDAO().getAllItems()
            val borrows = MyApplication.database!!.BorrowDAO().getAllBorrow()
            var itemBorrow = false
            for (i in borrows){
                if(i.idItem == idItemUse){
                    itemBorrow = true
                }
            }
            if(itemBorrow == false){
                for (i in items){
                    if(i.idItem == idItemUse){
                        builder
                            .setTitle("Delete Person")
                            .setMessage("Are you sure you want to delete "+i.nome+"?")
                            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                                MyApplication.database!!.ItemDAO().deleteItem(i)
                                return@OnClickListener

                            }).setNegativeButton(android.R.string.cancel) { dialog, which ->
                                dialog.dismiss()
                            }
                    }
                }
                builder.create().show()
            }
            else{
                showMensagemAlert("Erro", "This item is involved in a borrow.")
            }

        }


        btnCreate!!.setOnClickListener {
            if(edtBorrowDate!!.text.toString().isNullOrEmpty() || edtReturnDate!!.text.toString().isNullOrEmpty() || idItemUse == -1 || idPersonUse == -1){
                //val dateReturn = LocalDate.parse(edtReturnDate!!.text.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                //val dateBorrow = LocalDate.parse(edtBorrowDate!!.text.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                showMensagemAlert("Erro", "Blank values.")
            }
            else if (dateBorrowInverse-dateReturnInverse > 0){
                showMensagemAlert("Erro", "The date of return must be greater than the borrow date.")
            }
            else{
                val borrow = Entities.Borrow(edtBorrowDate!!.text.toString(), edtReturnDate!!.text.toString(),idItemUse, idPersonUse)
                MyApplication.database!!.BorrowDAO().insertBorrow(borrow)
                Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
                val items = MyApplication.database!!.ItemDAO().getAllItems()
                var nameItem = ""
                for (i in items){
                    if (idItemUse == i.idItem){
                        nameItem = i.nome!!
                    }
                }
                val content = "Today is the day of return of the "+nameItem
                val days: Long = returnQtdDias(edtBorrowDate!!.text.toString(), edtReturnDate!!.text.toString())

                scheduleNotification(getNotification(content), days.toInt())
            }




        }



        fillSpinnerPerson()
        fillSpinnerItem()

        btnAddPerson?.setOnClickListener {
            val viewLayoutPerson = inflater.inflate(R.layout.layout_dialog_person, container, false)
            val builder = AlertDialog.Builder(context!!)
            val edtNamePerson = viewLayoutPerson.findViewById<EditText>(R.id.edtNamePerson)
            val edtPhonePerson = viewLayoutPerson.findViewById<EditText>(R.id.edtPhonePerson)
            var edtCepPerson = viewLayoutPerson.findViewById<EditText>(R.id.edtCepPerson)
            val txtAddressPerson = viewLayoutPerson.findViewById<TextView>(R.id.txtAddressPerson)
            var progressBar = viewLayoutPerson.findViewById<ProgressBar>(R.id.progressBar)


            txtAddressPerson.setText("")

            builder
                .setTitle("Insert")
                .setMessage("Insert person")
                .setView(viewLayoutPerson)
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    if (edtNamePerson.text.toString().isNullOrEmpty() || edtCepPerson.text.toString().isNullOrEmpty()) {
                        showMensagemAlert("Erro", "Blank name or CEP.")
                        return@OnClickListener
                    } else{

                        val persons = MyApplication.database!!.PersonDAO().getAllPerson()
                        var existe = false
                        for(i in persons){
                            if(i.name == edtNamePerson.text.toString()){
                                existe = true
                            }
                        }
                        if(!existe){
                            val person = Entities.Person(edtNamePerson.text.toString(), edtPhonePerson.text.toString(),
                                edtCepPerson.text.toString(), txtAddressPerson.text.toString()
                            )
                            MyApplication.database?.PersonDAO()?.insertPerson(person)
                            Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
                            fillSpinnerPerson()
                        }
                        else{
                            showMensagemAlert("Erro", "A person with this name already exists!")
                        }


                    }
                }).setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }

            edtCepPerson.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 8) {
                        if(verificaConexao()){
                            if(progressBar.visibility == View.INVISIBLE) {
                                //viewLayoutPerson.alpha = 0.5 as Float
                                progressBar.visibility = View.VISIBLE

                            }
                            runHttpRequest(s?.toString(), txtAddressPerson, progressBar)
                        }
                        else{
                            Toast.makeText(context, "No internet connection to request the address from the CEP.", Toast.LENGTH_LONG).show()
                        }


                        //progressBar.visibility = View.INVISIBLE
                        //txtAddressPerson.text = s.toString()
                    } else {
                        txtAddressPerson.text = ""
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

            })
            builder.create().show()


        }
        btnAddItem?.setOnClickListener {

            var view2 = inflater.inflate(R.layout.layout_dialog_item, container, false)
            val edtNameItem = view2.findViewById<EditText>(R.id.edtNameItem)
            val buttonImage = view2.findViewById<Button>(R.id.btnAddImage)
            val builder = AlertDialog.Builder(context!!)
            imageItem = view2.findViewById<ImageView>(R.id.imageItem)


            builder
                .setTitle("Insert")
                .setMessage("Insert item")
                .setView(view2)
                .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    if (edtNameItem.text.toString().isNullOrEmpty()) {
                        showMensagemAlert("Erro", "Insert name item!")
                        return@OnClickListener

                    } else {
                        val persons = MyApplication.database!!.ItemDAO().getAllItems()
                        var existe = false
                        for(i in persons){
                            if(i.nome == edtNameItem.text.toString()){
                                existe = true
                            }
                        }
                        if(!existe){
                            if(image_uri == null){
                                val resources = context!!.resources
                                image_uri = Uri.parse("android.resource://"+context!!.getPackageName()+"/drawable/image_blank")
                                imageItem!!.setImageURI(image_uri)
                            }

                            val item = Entities.Item(edtNameItem.text.toString(), imageToBitmap(imageItem!!))
                            MyApplication.database?.ItemDAO()?.insertItem(item)
                            Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
                            fillSpinnerItem()
                        }
                        else{
                            showMensagemAlert("Erro", "A item with this name already exists!")
                        }

                    }
                }).setNegativeButton(android.R.string.cancel) { dialog, which ->
                    dialog.dismiss()
                }

            buttonImage.setOnClickListener {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(context!!.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permission, PERMISSION_CODE)
                    }
                    else{

                        openCamera()

                    }

                }
                else{
                        openCamera() 
                }
            }
            builder.create().show()
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = context!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }
                else{
                    Toast.makeText(context, "Permission danied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            imageItem!!.setImageURI(image_uri)
        }
    }

    fun fillSpinnerPerson(){
        val listPerson = MyApplication.database!!.PersonDAO().getAllPerson()
        val arrayAdapter = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, listPerson)
        spinnerPerson!!.adapter = arrayAdapter
    }

    fun fillSpinnerItem(){
        val listItems = MyApplication.database!!.ItemDAO().getAllItems()
        val arrayAdapter = ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item, listItems)
        spinnerItem!!.adapter = arrayAdapter
    }

    fun runHttpRequest(s: String?, txtAddressPerson: TextView, progressBar: ProgressBar){
        val que = Volley.newRequestQueue(mainActivity)
        var req: StringRequest? = null
        var url = "https://viacep.com.br/ws/${s}/json"
        val retryPolicy = DefaultRetryPolicy(500, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)


        req = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener { response ->
                var endereco = convertJSONTOObject(response)
                if(endereco.logradouro.isNullOrEmpty()){
                    showMensagemAlert("Erro", "Invalid CEP")
                }
                else{
                    txtAddressPerson.text = endereco.logradouro + " "+endereco.localidade + "-" + endereco.uf
                    progressBar.visibility = View.INVISIBLE

                }

            },
            Response.ErrorListener {
                //throw Exception()
                Toast.makeText(mainActivity, "I didn't find the CEP, its connection must be bad, I'll try again later.", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.INVISIBLE

            }
        )
        req.setRetryPolicy(retryPolicy)
        que.add(req)

    }



    fun convertJSONTOObject(jsonString: String): CEP {
        var jsonObject = JSONParser.getJsonObjectFromResponse(jsonString)
        var gson = Gson()

        return gson.fromJson(jsonObject.toString(), CEP::class.java)
    }

    private fun imageToBitmap(image: ImageView): ByteArray {
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }


    fun returnDateBorrow(){
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
            var month1 = mMonth +1
            edtBorrowDate!!.setText(""+mDay+"/"+month1+"/"+mYear)
            val str = "${mYear}${month1}${mDay}"
            dateBorrowInverse = str.toInt()
            //Toast.makeText(context, "date: "+dateBorrowInverse,Toast.LENGTH_LONG).show()
        }, year, month, day)
        dpd.show()

    }

    fun returnDateReturn(){
        val c = Calendar.getInstance()
        val day = c.get(Calendar.DAY_OF_MONTH)
        val month = c.get(Calendar.MONTH)
        val year = c.get(Calendar.YEAR)
        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
            var month1 = mMonth +1
            edtReturnDate!!.setText(""+mDay+"/"+month1+"/"+mYear)
            val str = "${mYear}${month1}${mDay}"
            dateReturnInverse= str.toInt()
            //Toast.makeText(context, "date: "+dateReturnInverse,Toast.LENGTH_LONG).show()
        }, year, month, day)
        dpd.show()

    }

    fun returnQtdDias (dateBorrow: String, dateReturn: String): Long{
        val myFormat = SimpleDateFormat("dd/MM/yyyy")
        var days: Long = 0
        //val inputString1 = "21/11/2018"
        //val inputString2 = "01/01/2019"
        try {
            val date1 = myFormat.parse(dateBorrow)
            val date2 = myFormat.parse(dateReturn)
            val diff = date2.getTime() - date1.getTime()
            //System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.DAYS))
            //days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) as Int
            days = diff/86400000
            Toast.makeText(context, "Dias: ${days}", Toast.LENGTH_LONG).show()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return days
    }

    fun showMensagemAlert(title: String, message: String){
        val builder = AlertDialog.Builder(context!!)

        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                    return@OnClickListener
            })

        builder.create().show()

    }


    fun  verificaConexao(): Boolean {
	    var conectado = false;
		//var conectivtyManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var conectivityManager = context!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
	    if (conectivityManager.getActiveNetworkInfo() != null
	            && conectivityManager.getActiveNetworkInfo().isAvailable()
	            && conectivityManager.getActiveNetworkInfo().isConnected()) {
	    	conectado = true;
	    } else {
	        conectado = false;
	    }
	    return conectado;
	}

    fun scheduleNotification(notification: Notification,days: Int) {
        var delay = days * 86400000 + 5000
        var notificationIntent = Intent(context, NotificationPublisher::class.java);
        notificationIntent.putExtra("notification-id1", 1);
        notificationIntent.putExtra("notification1", notification);
        var pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        var futureInMillis = SystemClock.elapsedRealtime() + delay;

        var alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //Toast.makeText(context,"Alarme: "+ futureInMillis, Toast.LENGTH_LONG).show()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)
        }
        else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)
        }
    }
    fun getNotification(content: String): Notification {
        var builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
        builder.setContentTitle("Return Date!");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel1"
            val descriptionText = "channel for notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}


