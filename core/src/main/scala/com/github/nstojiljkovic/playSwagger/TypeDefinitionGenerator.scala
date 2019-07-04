package com.github.nstojiljkovic.playSwagger

import com.github.nstojiljkovic.playSwagger.SwaggerParameterMapper.Parameter
import scala.collection.immutable.Seq

trait TypeDefinitionGenerator {
  def parameters(clazz: Class[_])(implicit cl: ClassLoader): Seq[Parameter]
}
