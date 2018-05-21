package com.github.nstojiljkovic.playSwagger.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.github.nstojiljkovic.playSwagger.SwaggerParameterMapper.Parameter;
import com.github.nstojiljkovic.playSwagger.TypeDefinitionGenerator;
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

    private String getTypeName(final JsonSchema schema) {
        switch (schema.getType()) {
            case STRING:
                return "java.lang.String";
            case NUMBER:
                // possible improvement: make differentiation between Double and Float
                return "scala.Double";
            case INTEGER:
                // possible improvement: make differentiation between Long and Int
                return "scala.Long";
            case BOOLEAN:
                return "scala.Boolean";
            case OBJECT:
                if (schema.get$ref() != null) {
                    final String s = schema.get$ref().replace("urn:jsonschema:", "").replaceAll(":", ".");
                    return s;
                } else if (schema.getId() != null) {
                    final String s = schema.getId().replace("urn:jsonschema:", "").replaceAll(":", ".");
                    return s;
                } else {
                    return "scala.Any";
                }
            case ARRAY:
                if (schema.asArraySchema().getItems().isSingleItems()) {
                    JsonSchema itemsSchema = schema.asArraySchema().getItems().asSingleItems().getSchema();
                    return "scala.Seq[" + getTypeName(itemsSchema) + "]";
                } else {
                    // possible improvement: support for multiple item types
                    return "scala.Seq[scala.Any]";
                }
            case NULL:
                return "scala.Null";
            case ANY:
            default:
                return "scala.Any";
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
                    final Parameter parameter = new Parameter(entry.getKey(), getTypeName(propertySchema), none, none, Option.apply(propertySchema));
                    parameters.add(parameter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JavaConverters.asScalaIteratorConverter(parameters.iterator()).asScala().toSeq();
    }
}
