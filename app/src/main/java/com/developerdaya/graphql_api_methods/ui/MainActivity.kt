package com.developerdaya.graphql_api_methods.ui
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.developerdaya.graphql_api_methods.R
import com.developerdaya.graphql_api_methods.api.GraphQLApi
import com.developerdaya.graphql_api_methods.model.GraphQLRequest
import com.developerdaya.graphql_api_methods.model.GraphQLResponse
import com.developerdaya.graphql_api_methods.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var ceoTextView: TextView
    private lateinit var summaryTextView: TextView
    private lateinit var employeesTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ceoTextView = findViewById<TextView>(R.id.text_ceo)
        summaryTextView = findViewById(R.id.text_summary)
        employeesTextView = findViewById(R.id.text_employees)
        val query = """
            query ExampleQuery {
              company {
                ceo
                summary
                employees
              }
            }
        """.trimIndent()

        val graphQLRequest = GraphQLRequest(query)
        val api = RetrofitClient.retrofit.create(GraphQLApi::class.java)
        val call = api.executeQuery(graphQLRequest)
        call.enqueue(object : Callback<GraphQLResponse> {
            override fun onResponse(call: Call<GraphQLResponse>, response: Response<GraphQLResponse>) {
                if (response.isSuccessful) {
                    val company = response.body()?.data?.company
                    company?.let {
                        ceoTextView.text = "CEO: ${it.ceo}"
                        summaryTextView.text = "Summary: ${it.summary}"
                        employeesTextView.text = "Employees: ${it.employees}"
                    }
                }
            }
            override fun onFailure(call: Call<GraphQLResponse>, t: Throwable) {
                ceoTextView.text = "Error: ${t.message}"
            }
        })
    }
}
