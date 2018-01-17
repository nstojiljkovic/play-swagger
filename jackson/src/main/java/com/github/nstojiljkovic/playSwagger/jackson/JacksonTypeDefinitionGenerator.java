package com.github.nstojiljkovic.playSwagger.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.github.nstojiljkovic.playSwagger.TypeDefinitionGenerator;
import play.routes.compiler.Parameter;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JacksonTypeDefinitionGenerator implements TypeDefinitionGenerator {

    final private JsonSchemaGenerator jsonSchemaGenerator;
    final private ObjectMapper mapper;

    public JacksonTypeDefinitionGenerator(final ObjectMapper mapper) {
        this.mapper = mapper;
        this.jsonSchemaGenerator = new JsonSchemaGenerator(mapper);
    }

    private String wrapRequired(String tpe, Boolean required) {
        if (required != null && required) {
            return "scala.Option[" + tpe + "]";
        } else {
            return tpe;
        }
    }

    private String getTypeName(final JsonSchema schema) {
        switch (schema.getType()) {
            case STRING:
                return wrapRequired("java.lang.String", schema.getRequired());
            case NUMBER:
                // possible improvement: make differentiation between Double and Float
                return wrapRequired("scala.Double", schema.getRequired());
            case INTEGER:
                // possible improvement: make differentiation between Long and Int
                return wrapRequired("scala.Long", schema.getRequired());
            case BOOLEAN:
                return wrapRequired("scala.Boolean", schema.getRequired());
            case OBJECT:
                if (schema.get$ref() != null) {
                    final String s = schema.get$ref().replace("urn:jsonschema:", "").replaceAll(":", ".");
                    return wrapRequired(s, schema.getRequired());
                } else if (schema.getId() != null) {
                    final String s = schema.getId().replace("urn:jsonschema:", "").replaceAll(":", ".");
                    return wrapRequired(s, schema.getRequired());
                } else {
                    return wrapRequired("scala.Any", schema.getRequired());
                }
            case ARRAY:
                if (schema.asArraySchema().getItems().isSingleItems()) {
                    JsonSchema itemsSchema = schema.asArraySchema().getItems().asSingleItems().getSchema();
                    return wrapRequired("scala.Seq[" + getTypeName(itemsSchema) + "]", schema.getRequired());
                } else {
                    // possible improvement: support for multiple item types
                    return wrapRequired("scala.Seq[scala.Any]", schema.getRequired());
                }
            case NULL:
                return wrapRequired("scala.Null", schema.getRequired());
            case ANY:
            default:
                return wrapRequired("scala.Any", schema.getRequired());
        }
    }

    @Override
    public Seq<Parameter> parameters(final Class<?> clazz, final ClassLoader cl) {
        final List<Parameter> parameters = new ArrayList<>();

        try {
            JsonSchema schema = jsonSchemaGenerator.generateSchema(clazz);
            if (schema instanceof ObjectSchema) {
                final Map<String, JsonSchema> properties = ((ObjectSchema) schema).getProperties();
                final Option<String> none = Option.apply(null);
                for (Map.Entry<String, JsonSchema> entry : properties.entrySet()) {
                    final JsonSchema propertySchema = entry.getValue();
                    if (propertySchema == null) {
                        continue;
                    }
                    final Parameter parameter = Parameter.apply(entry.getKey(), getTypeName(propertySchema), none, none);
                    parameters.add(parameter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JavaConverters.asScalaIteratorConverter(parameters.iterator()).asScala().toSeq();
    }
}
