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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

final class GenerationUtil {

    private GenerationUtil() {}

    static final String OPT_CDI = "cdi";
    static final String OPT_DISPATCHER_NAME = "dispatcherName";
    static final String OPT_DISPATCHER_PACKAGE = "dispatcherPackage";
    static final String DEFAULT_DISPATCHER_NAME = "ApplicationDispatcher";

    /**
     * Handy constant for an emtpy array of argument types.
     */
    static final String[] NO_PARAMS = new String[0];

    /**
     * Passing a reference to exactly this array causes
     * {@link #getAnnotatedMethods(javax.lang.model.element.TypeElement, javax.annotation.processing.ProcessingEnvironment,
     * String, javax.lang.model.type.TypeMirror, String[], StringBuilder)} not to care about parameter types.
     */
    static final String[] ANY_PARAMS = new String[0];

    static String actionImplementation(String payload) {
        return payload + "Action";
    }

    static String storeImplementation(String delegate) {
        return delegate + "Impl";
    }

    /**
     * Finds all public, non-static, no-args method annotated with the given annotation which returns the given type.
     * <p/>
     * If a method with the given annotation is found but the method does not satisfy the requirements listed above,
     * the
     * method will be marked with an error explaining the problem. This will trigger a compilation failure.
     * <p/>
     * If more than one method satisfies all the criteria, all such methods are marked with an error explaining the
     * problem.
     *
     * @param originalClassElement   the class to search for the annotated method.
     * @param processingEnvironment  the current annotation processing environment.
     * @param annotationName         the fully-qualified name of the annotation to search for.
     * @param requiredReturnType     the fully qualified name of the type the method must return.
     * @param requiredParameterTypes the parameter types the method must take. If the method must take no parameters,
     *                               use
     *                               {@link #NO_PARAMS}. If the method can take any parameters, use {@link
     *                               #ANY_PARAMS}.
     * @param errorHolder            filled with an error, if the given criteria do not match.
     *
     * @return a list of references to the methods that satisfy the criteria (empty list if no such method exists).
     */
    static List<ExecutableElement> getAnnotatedMethods(final TypeElement originalClassElement,
            final ProcessingEnvironment processingEnvironment, final String annotationName,
            final TypeMirror requiredReturnType, final String[] requiredParameterTypes, StringBuilder errorHolder) {

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

                List<String> problems = new ArrayList<>();

                if (!typeUtils.isAssignable(actualReturnType, requiredReturnType)) {
                    problems.add("return " + requiredReturnType);
                }
                if (!doParametersMatch(typeUtils, elementUtils, e, requiredParameterTypes)) {
                    if (requiredParameterTypes.length == 0) {
                        problems.add("take no parameters");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("take ").append(requiredParameterTypes.length).append(" parameters of type (");
                        boolean first = true;
                        for (String p : requiredParameterTypes) {
                            if (!first) {
                                sb.append(", ");
                            }
                            sb.append(p);
                            first = false;
                        }
                        sb.append(")");
                        problems.add(sb.toString());
                    }
                }
                if (e.getModifiers().contains(Modifier.STATIC)) {
                    problems.add("be non-static");
                }
                if (e.getModifiers().contains(Modifier.PRIVATE)) {
                    problems.add("be non-private");
                }


                if (problems.isEmpty()) {
                    matches.add(e);
                } else {
                    if (errorHolder != null) {
                        errorHolder.append(formatProblemsList(annotationName, problems));
                    }
                }
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

    static Collection<String> extractValue(final AnnotationValue value) {
        if (value.getValue() instanceof Collection) {
            final Collection<?> varray = (List<?>) value.getValue();
            final ArrayList<String> result = new ArrayList<String>(varray.size());
            for (final Object active : varray) {
                result.addAll(extractValue((AnnotationValue) active));
            }
            return result;
        }
        return Collections.singleton(value.getValue().toString());
    }

    /**
     * Checks whether the ExecutableElement's parameter list matches the requiredParameterTypes (order matters).
     *
     * @param typeUtils              type utils from current processing environment.
     * @param elementUtils           element utils from current processing environment.
     * @param e                      the method whose parameter list to check.
     * @param requiredParameterTypes the required parameter types. Must not be null.
     *                               If a reference to {@link #ANY_PARAMS}, this method returns true without any
     *                               further
     *                               checks.
     *
     * @return true if the target method's parameter list matches the given required parameter types, or if the special
     * {@link #ANY_PARAMS} value is passed as {@code requiredParameterTypes}. False otherwise.
     */
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

    /**
     * Renders the given list of problems with an annotated method as an English sentence.
     * The sentence takes the form "Methods annotated with <i>annotationSimpleName</i> must <i>list of problems</i>".
     * Commas and "and" are inserted as appropriate.
     *
     * @param annotationFqcn the fully-qualified name of the annotation the problems pertain to.
     * @param problems       the list of problems, as verb phrases. Must not be null, and should contain at least one
     *                       item.
     *
     * @return a nice English sentence summarizing the problems.
     */
    private static String formatProblemsList(final String annotationFqcn, List<String> problems) {
        StringBuilder msg = new StringBuilder();
        msg.append("Methods annotated with @")
                .append(fqcnToSimpleName(annotationFqcn))
                .append(" must ");
        for (int i = 0; i < problems.size(); i++) {
            if (problems.size() > 2 && i > 0) {
                msg.append(", ");
            }
            if (problems.size() == 2 && i == 1) {
                msg.append(" and ");
            }
            if (problems.size() > 2 && i == problems.size() - 1) {
                msg.append("and ");
            }
            msg.append(problems.get(i));
        }
        return msg.toString();
    }

    private static String fqcnToSimpleName(String fqcn) {
        int lastIndexOfDot = fqcn.lastIndexOf('.');
        if (lastIndexOfDot != -1) {
            return fqcn.substring(lastIndexOfDot + 1);
        }
        return fqcn;
    }
}
