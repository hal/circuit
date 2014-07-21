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
package org.jboss.gwt.circuit.processor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Contains a series of adaptations and workarounds to make annotation processors work well under Eclipse JDT APT. Does
 * not limit compatibility with other annotation processing environments (such as javac).
 */
public abstract class AbstractErrorAbsorbingProcessor extends AbstractProcessor {

    private Throwable rememberedInitError;

    protected AbstractErrorAbsorbingProcessor() {
        try {
            freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_NONE);
        } catch (ClassNotFoundException e) {
            rememberedInitError = e;
        }
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (rememberedInitError != null) {
                throw rememberedInitError;
            }
            return processWithExceptions(annotations, roundEnv);
        } catch (Throwable e) {
            // eclipse JDT goes into an infinite loop when the annotation processor throws any exception
            // so we have to catch EVERYTHING, even Errors.

            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            final String errorMessage = "Internal error in " + getClass().getName() + stringWriter.toString();

            boolean emittedSpecificError = false;
            for (TypeElement annotation : annotations) {
                for (Element annotationTarget : roundEnv.getElementsAnnotatedWith(annotation)) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            errorMessage,
                            annotationTarget,
                            findAnnotationMirror(annotationTarget, annotation));
                    emittedSpecificError = true;
                }
            }

            // if the above loop caught nothing, the type we were called for didn't contain an annotation
            // we handle (maybe it was inherited). In this case, we'll just emit a non-location-specific error
            // so there is at least some sort of diagnostic message for the user to go on!
            if (!emittedSpecificError) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        errorMessage);
            }

            return false;
        }
    }

    private static AnnotationMirror findAnnotationMirror(Element target, TypeElement annotationType) {
        final Name annotationTypeName = annotationType.getQualifiedName();
        for (AnnotationMirror am : target.getAnnotationMirrors()) {
            if (GenerationUtil.getQualifiedName(am).contentEquals(annotationTypeName)) {
                return am;
            }
        }
        return null;
    }

    /**
     * Subclasses must call this from their constructors if something throws an
     * exception during initialization of the instance. Once this method has
     * been called with a non-null throwable, the
     * {@link #processWithExceptions(Set, RoundEnvironment)} method will not be
     * called on this instance.
     *
     * @param t the exception that occurred (and was caught) during instance
     *          creation of this annotation processor instance.
     */
    protected void rememberInitializationError(Throwable t) {
        rememberedInitError = t;
    }

    /**
     * Same contract as {@link #process(Set, RoundEnvironment)}, except that any
     * exceptions thrown are caught and printed as messages of type
     * {@link javax.tools.Diagnostic.Kind#ERROR}. This is done to keep Eclipse JDT from going into an
     * infinite processing loop.
     */
    protected abstract boolean processWithExceptions(
            Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception;

    /**
     * Writes the given code to javac's Filer.
     */
    protected final void writeCode(final ProcessingEnvironment pe, final String packageName, final String className,
            final StringBuffer code)
            throws IOException {

        JavaFileObject jfo = pe.getFiler().createSourceFile(packageName + "." + className);
        Writer w = jfo.openWriter();
        BufferedWriter bw = new BufferedWriter(w);
        bw.append(code);
        bw.close();
        w.close();
    }
}
