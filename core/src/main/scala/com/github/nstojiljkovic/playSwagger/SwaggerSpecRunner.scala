package com.github.nstojiljkovic.playSwagger

import java.nio.file.{ Files, Paths, StandardOpenOption }

import scala.util.{ Success, Failure, Try }

object SwaggerSpecRunner extends App {
  implicit def cl = getClass.getClassLoader
  implicit val typeDefinitionGenerator: TypeDefinitionGenerator = new DefaultTypeDefinitionGenerator

  val (targetFile :: routesFile :: domainNameSpaceArgs :: outputTransformersArgs :: swaggerV3String :: Nil) = args.toList
  private def fileArg = Paths.get(targetFile)
  private def swaggerJson = {
    val swaggerV3 = java.lang.Boolean.parseBoolean(swaggerV3String)
    val domainModelQualifier = PrefixDomainModelQualifier(domainNameSpaceArgs.split(","): _*)
    val transformersStrs: Seq[String] = if (outputTransformersArgs.isEmpty) Seq() else outputTransformersArgs.split(",")
    val transformers = transformersStrs.map { clazz =>
      Try(cl.loadClass(clazz).asSubclass(classOf[OutputTransformer]).getDeclaredConstructor().newInstance()) match {
        case Failure(ex: ClassCastException) =>
          throw new IllegalArgumentException("Transformer should be a subclass of com.github.nstojiljkovic.playSwagger.OutputTransformer:" + clazz, ex)
        case Failure(ex) => throw new IllegalArgumentException("Could not create transformer", ex)
        case Success(el) => el
      }
    }
    SwaggerSpecGenerator(
      domainModelQualifier,
      outputTransformers = transformers,
      swaggerV3 = swaggerV3).generate(routesFile).get.toString
  }

  Files.write(fileArg, swaggerJson.getBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
}
