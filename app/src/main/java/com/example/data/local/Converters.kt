package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AiFinding
import com.example.data.model.AttackStep
import com.example.data.model.InfrastructureNode
import com.example.data.model.RedirectHop
import com.example.data.model.ScamLesson
import com.example.data.model.ThreatLevel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
  private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  @TypeConverter
  fun fromThreatLevel(value: ThreatLevel): String = value.name

  @TypeConverter
  fun toThreatLevel(value: String): ThreatLevel = try {
    ThreatLevel.valueOf(value)
  } catch (e: Exception) {
    ThreatLevel.SUSPICIOUS
  }

  // String List
  @TypeConverter
  fun fromStringList(list: List<String>?): String {
    if (list == null) return "[]"
    val type = Types.newParameterizedType(List::class.java, String::class.java)
    val adapter = moshi.adapter<List<String>>(type)
    return adapter.toJson(list)
  }

  @TypeConverter
  fun toStringList(json: String?): List<String> {
    if (json.isNullOrBlank()) return emptyList()
    val type = Types.newParameterizedType(List::class.java, String::class.java)
    val adapter = moshi.adapter<List<String>>(type)
    return adapter.fromJson(json) ?: emptyList()
  }

  // Moshi Helper serialization methods for the Repository layer
  companion object {
    private val moshiInstance = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun serializeFindings(list: List<AiFinding>): String {
      val type = Types.newParameterizedType(List::class.java, AiFinding::class.java)
      return moshiInstance.adapter<List<AiFinding>>(type).toJson(list)
    }

    fun deserializeFindings(json: String): List<AiFinding> {
      if (json.isBlank()) return emptyList()
      return try {
        val type = Types.newParameterizedType(List::class.java, AiFinding::class.java)
        moshiInstance.adapter<List<AiFinding>>(type).fromJson(json) ?: emptyList()
      } catch (e: Exception) {
        emptyList()
      }
    }

    fun serializeAttackSteps(list: List<AttackStep>): String {
      val type = Types.newParameterizedType(List::class.java, AttackStep::class.java)
      return moshiInstance.adapter<List<AttackStep>>(type).toJson(list)
    }

    fun deserializeAttackSteps(json: String): List<AttackStep> {
      if (json.isBlank()) return emptyList()
      return try {
        val type = Types.newParameterizedType(List::class.java, AttackStep::class.java)
        moshiInstance.adapter<List<AttackStep>>(type).fromJson(json) ?: emptyList()
      } catch (e: Exception) {
        emptyList()
      }
    }

    fun serializeInfraNodes(list: List<InfrastructureNode>): String {
      val type = Types.newParameterizedType(List::class.java, InfrastructureNode::class.java)
      return moshiInstance.adapter<List<InfrastructureNode>>(type).toJson(list)
    }

    fun deserializeInfraNodes(json: String): List<InfrastructureNode> {
      if (json.isBlank()) return emptyList()
      return try {
        val type = Types.newParameterizedType(List::class.java, InfrastructureNode::class.java)
        moshiInstance.adapter<List<InfrastructureNode>>(type).fromJson(json) ?: emptyList()
      } catch (e: Exception) {
        emptyList()
      }
    }

    fun serializeRedirectChain(list: List<RedirectHop>): String {
      val type = Types.newParameterizedType(List::class.java, RedirectHop::class.java)
      return moshiInstance.adapter<List<RedirectHop>>(type).toJson(list)
    }

    fun deserializeRedirectChain(json: String): List<RedirectHop> {
      if (json.isBlank()) return emptyList()
      return try {
        val type = Types.newParameterizedType(List::class.java, RedirectHop::class.java)
        moshiInstance.adapter<List<RedirectHop>>(type).fromJson(json) ?: emptyList()
      } catch (e: Exception) {
        emptyList()
      }
    }

    fun serializeLessons(list: List<ScamLesson>): String {
      val type = Types.newParameterizedType(List::class.java, ScamLesson::class.java)
      return moshiInstance.adapter<List<ScamLesson>>(type).toJson(list)
    }

    fun deserializeLessons(json: String): List<ScamLesson> {
      if (json.isBlank()) return emptyList()
      return try {
        val type = Types.newParameterizedType(List::class.java, ScamLesson::class.java)
        moshiInstance.adapter<List<ScamLesson>>(type).fromJson(json) ?: emptyList()
      } catch (e: Exception) {
        emptyList()
      }
    }
  }
}
