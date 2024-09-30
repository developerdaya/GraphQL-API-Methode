package com.developerdaya.graphql_api_methods.model

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
