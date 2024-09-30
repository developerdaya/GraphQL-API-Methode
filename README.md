
### Implementing GraphQL API
Here's the structure we'll follow:

```
app/
 └── src/
     └── main/
         └── java/
             └── com/
                 └── example/
                     └── graphqlretrofit/
                         ├── api/
                         │    └── GraphQLApi.kt
                         ├── model/
                         │    ├── GraphQLRequest.kt
                         │    └── GraphQLResponse.kt
                         ├── network/
                         │    └── RetrofitClient.kt
                         └── ui/
                              └── MainActivity.kt
         └── res/
             └── layout/
                 └── activity_main.xml
```

### Step-by-Step Code

#### 1. Add Dependencies
In your `build.gradle`:
```gradle
dependencies {
    // Retrofit and Gson for GraphQL API
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // OkHttp for logging
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'
}
```

#### 2. Define GraphQL API

##### `api/GraphQLApi.kt`
```kotlin
package com.example.graphqlretrofit.api

import com.example.graphqlretrofit.model.GraphQLRequest
import com.example.graphqlretrofit.model.GraphQLResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GraphQLApi {
    @Headers("Content-Type: application/json")
    @POST("/")
    fun executeQuery(@Body request: GraphQLRequest): Call<GraphQLResponse>
}
```

#### 3. Create Retrofit Client

##### `network/RetrofitClient.kt`
```kotlin
package com.example.graphqlretrofit.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://spacex-production.up.railway.app/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

#### 4. Create Request and Response Models

##### `model/GraphQLRequest.kt`
```kotlin
package com.example.graphqlretrofit.model

data class GraphQLRequest(
    val query: String
)
```

##### `model/GraphQLResponse.kt`
```kotlin
package com.example.graphqlretrofit.model

data class GraphQLResponse(
    val data: Data
)

data class Data(
    val company: Company
)

data class Company(
    val ceo: String,
    val summary: String,
    val employees: Int
)
```

#### 5. Create Main Activity

##### `ui/MainActivity.kt`
```kotlin
package com.example.graphqlretrofit.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.graphqlretrofit.R
import com.example.graphqlretrofit.api.GraphQLApi
import com.example.graphqlretrofit.model.GraphQLRequest
import com.example.graphqlretrofit.model.GraphQLResponse
import com.example.graphqlretrofit.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var ceoTextView: TextView
    private lateinit var summaryTextView: TextView
    private lateinit var employeesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Binding views
        ceoTextView = findViewById(R.id.text_ceo)
        summaryTextView = findViewById(R.id.text_summary)
        employeesTextView = findViewById(R.id.text_employees)

        // Query string for GraphQL
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

        // Retrofit client and API call
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
```

#### 6. Design the Layout

##### `res/layout/activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/text_ceo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="CEO"
        android:textSize="18sp"
        android:layout_marginBottom="8dp"/>

    <TextView
        android:id="@+id/text_summary"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Summary"
        android:textSize="16sp"
        android:layout_marginBottom="8dp"/>

    <TextView
        android:id="@+id/text_employees"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Employees"
        android:textSize="16sp"/>
</LinearLayout>
```

### Key Points

- **API Interface (`GraphQLApi.kt`)**: Defines the POST method for sending the GraphQL query.
- **Retrofit Client (`RetrofitClient.kt`)**: Configures Retrofit with logging for HTTP requests.
- **Models (`GraphQLRequest.kt` and `GraphQLResponse.kt`)**: Handles the GraphQL request and response structure.
- **Main Activity (`MainActivity.kt`)**: Makes the API request and displays the data in the views.
- **XML Layout (`activity_main.xml`)**: Simple UI layout to display the CEO, summary, and employee count.

### How It Works:
- The app sends a GraphQL request to the SpaceX API using Retrofit.
- The response contains information about the company (CEO, summary, employees), which is displayed in the `TextView` components in `MainActivity`.

Let me know if you need further clarification!
