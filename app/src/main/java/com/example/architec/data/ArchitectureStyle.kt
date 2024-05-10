package com.example.architec.data

class ArchitectureStyle (
    var id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val features: List<String>? = null,
    val origin: String? = null,
    val time_period: String? = null,
    val icon_url: String? = null,
    ){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArchitectureStyle) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
    override fun toString(): String {
        return name ?: ""
    }
}