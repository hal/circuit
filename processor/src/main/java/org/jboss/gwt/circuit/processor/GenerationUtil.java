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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class GenerationUtil {

    private GenerationUtil() {}

    /**
     * Passing a reference to exactly this array causes
     * {@link #getAnnotatedMethods(javax.lang.model.element.TypeElement, javax.annotation.processing.ProcessingEnvironment,
     * String, javax.lang.model.type.TypeMirror, String[])} not to care about parameter types.
     */
    static final String[] ANY_PARAMS = new String[0];

    static List<ExecutableElement> getAnnotatedMethods(final TypeElement originalClassElement,
            final ProcessingEnvironment processingEnvironment, final String annotationName,
            final TypeMirror requiredReturnType, final String[] requiredParameterTypes) {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();

        TypeElement classElement = originalClassElement;
        while (true) {
            final List<ExecutableElement> methods = ElementFilter.methodsIn(classElement.getEnclosedElements());

            List<ExecutableElement> matches = new ArrayList<>();
            for (ExecutableElement e : methods) {
                final TypeMirror actualReturnType = e.getReturnType();
                if (getAnnotation(elementUtils, e, annotationName) == null) {
                    continue;
                }
                if (!typeUtils.isAssignable(actualReturnType, requiredReturnType)) {
                    NoType voidType = typeUtils.getNoType(TypeKind.VOID);
                    if (!(voidType.equals(actualReturnType) && voidType.equals(requiredReturnType))) {
                        continue;
                    }
                }
                if (!doParametersMatch(typeUtils, elementUtils, e, requiredParameterTypes)) {
                    continue;
                }
                if (e.getModifiers().contains(Modifier.STATIC)) {
                    continue;
                }
                if (e.getModifiers().contains(Modifier.PRIVATE)) {
                    continue;
                }
                matches.add(e);
            }

            if (!matches.isEmpty()) {
                return matches;
            }

            TypeMirror superclass = classElement.getSuperclass();
            if (superclass instanceof DeclaredType) {
                classElement = (TypeElement) ((DeclaredType) superclass).asElement();
            } else {
                break;
            }
        }
        return Collections.emptyList();
    }

    static AnnotationMirror getAnnotation(Elements elementUtils, Element annotationTarget,
            String annotationName) {
        for (AnnotationMirror annotation : elementUtils.getAllAnnotationMirrors(annotationTarget)) {
            if (annotationName.contentEquals(getQualifiedName(annotation))) {
                return annotation;
            }
        }
        return null;
    }

    static Name getQualifiedName(AnnotationMirror annotation) {
        return ((TypeElement) annotation.getAnnotationType().asElement()).getQualifiedName();
    }

    static boolean doParametersMatch(final Types typeUtils,
            final Elements elementUtils,
            final ExecutableElement e,
            final String[] requiredParameterTypes) {
        if (requiredParameterTypes == ANY_PARAMS) {
            return true;
        }
        if (e.getParameters().size() != requiredParameterTypes.length) {
            return false;
        }
        List<TypeMirror> requiredTypes = new ArrayList<>();
        for (String parameterType : requiredParameterTypes) {
            requiredTypes.add(elementUtils.getTypeElement(parameterType).asType());
        }
        for (int i = 0; i < requiredTypes.size(); i++) {
            final TypeMirror actualType = e.getParameters().get(i).asType();
            final TypeMirror requiredType = requiredTypes.get(i);
            if (!typeUtils.isAssignable(actualType,
                    requiredType)) {
                return false;
            }
        }
        return true;
    }

    static Collection<String> extractValue(final AnnotationValue value) {
        if (value.getValue() instanceof Collection) {
            final Collection<?> varray = (List<?>) value.getValue();
            final ArrayList<String> result = new ArrayList<>(varray.size());
            for (final Object active : varray) {
                result.addAll(extractValue((AnnotationValue) active));
            }
            return result;
        }
        return Collections.singleton(value.getValue().toString());
    }
}
