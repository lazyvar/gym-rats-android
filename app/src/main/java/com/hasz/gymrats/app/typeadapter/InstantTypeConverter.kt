package com.hasz.gymrats.app.typeadapter

import com.google.gson.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.reflect.Type

class LocalDateTimeConverter: JsonSerializer<LocalDateTime?>, JsonDeserializer<LocalDateTime?> {
  override fun serialize(
    src: LocalDateTime?,
    srcType: Type?,
    context: JsonSerializationContext?
  ): JsonElement? {
    throw Error()
  }

  @Throws(JsonParseException::class)
  override fun deserialize(
    json: JsonElement,
    type: Type?,
    context: JsonDeserializationContext?
  ): LocalDateTime {
    val date = json.asString.split(".").first()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    return LocalDateTime.parse(date, formatter)
  }
}