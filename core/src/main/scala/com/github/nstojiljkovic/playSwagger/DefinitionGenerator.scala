package com.github.nstojiljkovic.playSwagger

import com.github.nstojiljkovic.playSwagger.Domain.{ CustomMappings, Definition, GenSwaggerParameter, SwaggerParameter }
import com.github.nstojiljkovic.playSwagger.SwaggerParameterMapper.mapParam

import scala.reflect.runtime.universe._

final case class DefinitionGenerator(
  modelQualifier: DomainModelQualifier = PrefixDomainModelQualifier(),
  mappings:       CustomMappings       = Nil)(implicit cl: ClassLoader, typeDefinitionGenerator: TypeDefinitionGenerator) {

  def dealiasParams(t: Type): Type = {
    appliedType(t.dealias.typeConstructor, t.typeArgs.map { arg ⇒
      dealiasParams(arg.dealias)
    })
  }

  def definition(tpe: Type): Definition = {
    val clazz: Class[_] = runtimeMirror(cl).runtimeClass(tpe)
    val properties = typeDefinitionGenerator.parameters(clazz).map(mapParam(_, modelQualifier, mappings))

    Definition(
      name = tpe.typeSymbol.fullName,
      properties = properties)
  }

  def definition[T: TypeTag]: Definition = definition(weakTypeOf[T])

  def definition(className: String): Definition = {
    val mirror = runtimeMirror(cl)
    val sym = mirror.staticClass(className)
    val tpe = sym.selfType
    definition(tpe)
  }

  def allDefinitions(typeNames: Seq[String]): List[Definition] = {
    def genSwaggerParameter: PartialFunction[SwaggerParameter, GenSwaggerParameter] =
      { case p: GenSwaggerParameter ⇒ p }

    def allReferredDefs(defName: String, memo: List[Definition]): List[Definition] = {
      memo.find(_.name == defName) match {
        case Some(_) ⇒ memo
        case None ⇒
          val thisDef = definition(defName)
          val refNames: Seq[String] = for {
            p ← thisDef.properties.collect(genSwaggerParameter)
            className ← p.referenceType orElse p.items.collect(genSwaggerParameter).flatMap(_.referenceType)
            if modelQualifier.isModel(className)
          } yield className

          refNames.foldLeft(thisDef :: memo) { (foundDefs, refName) ⇒
            allReferredDefs(refName, foundDefs)
          }
      }
    }

    typeNames.foldLeft(List.empty[Definition]) { (memo, typeName) ⇒
      allReferredDefs(typeName, memo)
    }
  }
}

object DefinitionGenerator {
  implicit val typeDefinitionGenerator: TypeDefinitionGenerator = new DefaultTypeDefinitionGenerator

  def apply(
    domainNameSpace:             String,
    customParameterTypeMappings: CustomMappings)(implicit cl: ClassLoader): DefinitionGenerator =
    DefinitionGenerator(
      PrefixDomainModelQualifier(domainNameSpace), customParameterTypeMappings)
}
