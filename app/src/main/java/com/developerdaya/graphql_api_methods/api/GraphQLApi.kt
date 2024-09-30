package com.developerdaya.graphql_api_methods.api
import com.developerdaya.graphql_api_methods.model.GraphQLRequest
import com.developerdaya.graphql_api_methods.model.GraphQLResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GraphQLApi {

    @POST("/")
    fun executeQuery(@Body request: GraphQLRequest): Call<GraphQLResponse>
}
