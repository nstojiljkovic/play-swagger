package com.github.nstojiljkovic.playSwagger.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.types.*;
import com.github.nstojiljkovic.playSwagger.SwaggerParameterMapper.Parameter;
import com.github.nstojiljkovic.playSwagger.SwaggerSpecGenerator;
import com.github.nstojiljkovic.playSwagger.TypeDefinitionGenerator;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import play.api.libs.json.StaticBinding;
import scala.Option;
import scala.collection.JavaConverters;
import static scala.collection.JavaConverters.seqAsJavaList;

import java.util.*;

import static org.junit.Assert.*;

public class JacksonTypeDefinitionGeneratorTest {

    @Test
    public void jackson() {
        final Option<String> none = Option.apply(null);
        final Option<JsonSchema> noneSchema = Option.apply(null);
        final ObjectMapper mapper = new ObjectMapper();
        final JacksonTypeDefinitionGenerator jacksonTypeDefinitionGenerator = new JacksonTypeDefinitionGenerator(mapper);
        final Parameter[] childParameters = seqAsJavaList(jacksonTypeDefinitionGenerator.parameters(ChildModel.class, getClass().getClassLoader())).toArray(new Parameter[0]);
        final BooleanSchema booleanRequiredSchema = new BooleanSchema();
        booleanRequiredSchema.setRequired(true);
        final Parameter[] expectedChildParameters = Arrays.asList(
                new Parameter("childSampleField", "scala.Boolean", none, none, Option.apply(booleanRequiredSchema)),
                new Parameter("anotherChildSampleField", "scala.Long", none, none, Option.apply(new IntegerSchema()))
        ).toArray(new Parameter[0]);
        assertArrayEquals(expectedChildParameters, childParameters);

        final Parameter[] parentParameters = seqAsJavaList(jacksonTypeDefinitionGenerator.parameters(ParentModel.class, getClass().getClassLoader())).toArray(new Parameter[0]);
        final Map<String, JsonSchema> properties = new LinkedHashMap<>();
        properties.put("childSampleField", booleanRequiredSchema);
        properties.put("anotherChildSampleField", new IntegerSchema());
        final ObjectSchema childObjectSchema = (new ObjectSchema());
        childObjectSchema.setId("urn:jsonschema:com:github:nstojiljkovic:playSwagger:jackson:ChildModel");
        childObjectSchema.setProperties(properties);
        final ArraySchema childArraySchema = (new ArraySchema());
        childArraySchema.setItems(new ArraySchema.SingleItems(new ReferenceSchema("urn:jsonschema:com:github:nstojiljkovic:playSwagger:jackson:ChildModel")));
        final StringSchema stringRequiredSchema = new StringSchema();
        stringRequiredSchema.setRequired(true);
        final Parameter[] expectedParentParameters = Arrays.asList(
                new Parameter("sampleField", "java.lang.String", none, none, Option.apply(stringRequiredSchema)),
                new Parameter("anotherSampleField", "scala.Double", none, none, Option.apply(new NumberSchema())),
                new Parameter("firstChild", "com.github.nstojiljkovic.playSwagger.jackson.ChildModel", none, none, Option.apply(childObjectSchema)),
                new Parameter("children", "scala.Seq[com.github.nstojiljkovic.playSwagger.jackson.ChildModel]", none, none, Option.apply(childArraySchema))
        ).toArray(new Parameter[0]);
        assertArrayEquals(expectedParentParameters, parentParameters);
    }

    @Test
    public void jackson2() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final List<String> domainPackage = Collections.singletonList("com.github.nstojiljkovic.playSwagger.jackson");
        final TypeDefinitionGenerator typeDefinitionGenerator = new JacksonTypeDefinitionGenerator(mapper);
        final SwaggerSpecGenerator generator =
                SwaggerSpecGenerator.apply(
                        true,
                        JavaConverters.asScalaIteratorConverter(domainPackage.iterator()).asScala().toSeq(), getClass().getClassLoader(),
                        typeDefinitionGenerator
                );


        final String swaggerJson = StaticBinding.prettyPrint(generator.generate("routes").get());
        final String expectedJson = IOUtils.toString(
                this.getClass().getResourceAsStream("expected.json"),
                "UTF-8"
        );
        JSONAssert.assertEquals(swaggerJson, expectedJson, false);
        JSONAssert.assertEquals(expectedJson, swaggerJson, false);
    }
}
