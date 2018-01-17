package com.github.nstojiljkovic.playSwagger

import play.routes.compiler.Parameter

trait TypeDefinitionGenerator {
  def parameters(clazz: Class[_])(implicit cl: ClassLoader): Seq[Parameter]
}
