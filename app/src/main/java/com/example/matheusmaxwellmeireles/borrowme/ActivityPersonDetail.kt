package com.example.matheusmaxwellmeireles.borrowme

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_person_detail.*
import java.lang.RuntimeException

class ActivityPersonDetail : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_detail)

        val idPerson = intent.getIntExtra("idPerson", -1)
        val listPerson = MyApplication.database!!.PersonDAO().getAllPerson()

        btnVoltarItemDetail.setOnClickListener {
            onBackPressed()
        }

        if(idPerson != -1){
            for (i in listPerson){
                if(i.idPerson == idPerson){
                    txtNameDinamic.setText(i.name)
                    txtPhoneDinamic.setText(i.phone)
                    txtCepDinamic.setText(i.cep)
                    if(i.address.isNullOrEmpty()){
                        runHttpRequest(i.cep, txtAddressDinamic)
                    }
                    else{
                        txtAddressDinamic.setText(i.address)
                    }

                }
            }
        }
        else{
            Toast.makeText(this, "Some error occurred.", Toast.LENGTH_SHORT).show()
        }
    }

    fun runHttpRequest(s: String?, txtAddressPerson: TextView) {
        val que = Volley.newRequestQueue(this)
        var req: StringRequest? = null
        //var cepDigito = mainActivity?.findViewById<EditText>(R.id.edtCepPerson)
        var url = "https://viacep.com.br/ws/${s}/json"
        //var url = "https://viacep.com.br/ws/72800190/json"
        req = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener { response ->
                var endereco = convertJSONTOObject(response)
                txtAddressPerson.text = endereco.logradouro + " "+endereco.localidade + "-" + endereco.uf
                Log.i("teste", endereco.logradouro)
            },
            Response.ErrorListener {
                throw Exception()
                Toast.makeText(this, "Could not get the address, check your internet connection.", Toast.LENGTH_LONG).show()
            }
        )
        que.add(req)
    }



    fun convertJSONTOObject(jsonString: String): CEP {
        var jsonObject = JSONParser.getJsonObjectFromResponse(jsonString)
        var gson = Gson()

        return gson.fromJson(jsonObject.toString(), CEP::class.java)
    }
}
