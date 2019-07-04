package com.github.nstojiljkovic.playSwagger.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema
import com.fasterxml.jackson.module.jsonSchema.{ JsonSchema, JsonSchemaGenerator }
import com.github.nstojiljkovic.playSwagger.SwaggerParameterMapper.Parameter
import com.github.nstojiljkovic.playSwagger.{ SwaggerParameterMapper, TypeDefinitionGenerator }

import scala.collection.immutable.Seq
import scala.collection.immutable.Map

class JacksonTypeDefinitionGenerator(val mapper: ObjectMapper) extends TypeDefinitionGenerator {
  private val jsonSchemaGenerator: JsonSchemaGenerator = new JsonSchemaGenerator(mapper)

  private def getTypeName(schema: JsonSchema): String = schema.getType match {
    case JsonFormatTypes.STRING =>
      "java.lang.String"
    case JsonFormatTypes.NUMBER =>
      // possible improvement: make differentiation between Double and Float
      "scala.Double"
    case JsonFormatTypes.INTEGER =>
      // possible improvement: make differentiation between Long and Int
      "scala.Long"
    case JsonFormatTypes.BOOLEAN =>
      "scala.Boolean"
    case JsonFormatTypes.OBJECT =>
      if (schema.get$ref != null) {
        val s: String = schema.get$ref.replace("urn:jsonschema:", "").replaceAll(":", ".")
        s
      } else if (schema.getId != null) {
        val s: String = schema.getId.replace("urn:jsonschema:", "").replaceAll(":", ".")
        s
      } else "scala.Any"
    case JsonFormatTypes.ARRAY =>
      if (schema.asArraySchema.getItems.isSingleItems) {
        val itemsSchema: JsonSchema = schema.asArraySchema.getItems.asSingleItems.getSchema
        "scala.Seq[" + getTypeName(itemsSchema) + "]"
      } else {
        // possible improvement: support for multiple item types
        "scala.Seq[scala.Any]"
      }
    case JsonFormatTypes.NULL =>
      "scala.Null"
    case JsonFormatTypes.ANY =>
      "scala.Any"
    case _ =>
      "scala.Any"
  }

  override def parameters(clazz: Class[_])(implicit cl: ClassLoader): Seq[Parameter] = {
    import scala.collection.JavaConverters._

    try {
      val schema: JsonSchema = jsonSchemaGenerator.generateSchema(clazz)
      schema match {
        case s: ObjectSchema =>
          val properties: Map[String, JsonSchema] = Map.empty ++ s.getProperties.asScala
          val none: Option[String] = Option.apply(null)
          Seq.empty ++ properties.toSeq.flatMap(t => {
            val key = t._1
            val propertySchema = t._2
            if (propertySchema != null) {
              Some(SwaggerParameterMapper.Parameter(key, getTypeName(propertySchema), none, none, Option.apply(propertySchema)))
            } else {
              None
            }
          })
        case _ =>
          Seq.empty
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        Seq.empty
    }
  }
}
