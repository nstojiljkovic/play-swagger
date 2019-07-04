package com.github.nstojiljkovic.playSwagger

import com.github.nstojiljkovic.playSwagger.SwaggerParameterMapper.Parameter
import scala.collection.immutable.Seq

import scala.reflect.runtime.universe._

class DefaultTypeDefinitionGenerator extends TypeDefinitionGenerator {
  def dealiasParams(t: Type): Type = {
    appliedType(t.dealias.typeConstructor, t.typeArgs.map { arg =>
      dealiasParams(arg.dealias)
    })
  }

  override def parameters(clazz: Class[_])(implicit cl: ClassLoader): Seq[Parameter] = {
    val tpe = runtimeMirror(cl).classSymbol(clazz).toType
    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.toList.flatMap(_.paramLists).headOption.getOrElse(Nil)

    fields.map { field =>
      //TODO: find a better way to get the string representation of typeSignature
      val name = field.name.decodedName.toString
      val typeName = dealiasParams(field.typeSignature).toString
      // passing None for 'fixed' and 'default' here, since we're not dealing with route parameters
      Parameter(name, typeName, None, None, None)
    }
  }
}
