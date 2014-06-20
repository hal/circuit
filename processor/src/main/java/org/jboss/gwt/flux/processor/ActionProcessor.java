/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.gwt.flux.processor;

import static javax.tools.Diagnostic.Kind.NOTE;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.jboss.gwt.flux.meta.Action;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("org.jboss.gwt.flux.meta.Action")
public class ActionProcessor extends AbstractErrorAbsorbingProcessor {

    @Override
    protected boolean processWithExceptions(final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv)
            throws Exception {

        if (!roundEnv.processingOver()) {
            final Messager messager = processingEnv.getMessager();

            for (Element e : roundEnv.getElementsAnnotatedWith(Action.class)) {
                if (e.getKind() == ElementKind.CLASS) {

                    TypeElement classElement = (TypeElement) e;
                    PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();

                    final String packageName = packageElement.getQualifiedName().toString();
                    final String payloadClassName = classElement.getSimpleName().toString();
                    final String actionClassName = GenerationUtil.actionImplementation(payloadClassName);
                    messager.printMessage(Diagnostic.Kind.NOTE,
                            "Discovered annotated action [" + payloadClassName + "]");

                    try {
                        messager.printMessage(Diagnostic.Kind.NOTE, "Generating code for [" + actionClassName + "]");
                        ActionGenerator generator = new ActionGenerator();
                        final StringBuffer code = generator.generate(packageName, actionClassName,
                                payloadClassName);
                        writeCode(packageName, actionClassName, code);

                        messager.printMessage(NOTE,
                                "Successfully generated action implementation [" + actionClassName + "]");
                    } catch (GenerationException ge) {
                        final String msg = ge.getMessage();
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, classElement);
                    }
                }
            }
        }
        return true;
    }
}
