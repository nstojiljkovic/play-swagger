package com.github.nstojiljkovic.playSwagger.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nstojiljkovic.playSwagger.SwaggerSpecGenerator;
import com.github.nstojiljkovic.playSwagger.TypeDefinitionGenerator;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.api.libs.json.JsObject;
import play.api.libs.json.StaticBinding;
import play.routes.compiler.Parameter;
import scala.Option;
import scala.collection.JavaConverters;
import static scala.collection.JavaConverters.seqAsJavaList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class JacksonTypeDefinitionGeneratorTest {

    @Test
    public void jackson() {
        final Option<String> none = Option.apply(null);
        final ObjectMapper mapper = new ObjectMapper();
        final JacksonTypeDefinitionGenerator jacksonTypeDefinitionGenerator = new JacksonTypeDefinitionGenerator(mapper);
        final List<Parameter> childParameters = seqAsJavaList(jacksonTypeDefinitionGenerator.parameters(ChildModel.class, getClass().getClassLoader()));
        final List<Parameter> expectedChildParameters = Arrays.asList(
                Parameter.apply("childSampleField", "scala.Boolean", none, none),
                Parameter.apply("anotherChildSampleField", "scala.Long", none, none)
        );
        assertEquals(expectedChildParameters, childParameters);

        final List<Parameter> parentParameters = seqAsJavaList(jacksonTypeDefinitionGenerator.parameters(ParentModel.class, getClass().getClassLoader()));
        final List<Parameter> expectedParentParameters = Arrays.asList(
                Parameter.apply("sampleField", "java.lang.String", none, none),
                Parameter.apply("anotherSampleField", "scala.Double", none, none),
                Parameter.apply("firstChild", "com.github.nstojiljkovic.playSwagger.jackson.ChildModel", none, none),
                Parameter.apply("children", "scala.Seq[com.github.nstojiljkovic.playSwagger.jackson.ChildModel]", none, none)
        );
        assertEquals(expectedParentParameters, parentParameters);
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
        final String cleanedUpExpectedJson = StaticBinding.prettyPrint(StaticBinding.parseJsValue(expectedJson));
        assertEquals(cleanedUpExpectedJson, swaggerJson);
    }
}
