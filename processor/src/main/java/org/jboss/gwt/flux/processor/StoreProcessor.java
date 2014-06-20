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

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.jboss.gwt.flux.processor.GenerationUtil.getAnnotatedMethods;
import static org.jboss.gwt.flux.processor.GenerationUtil.storeImplementation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.meta.Action;
import org.jboss.gwt.flux.meta.Receive;
import org.jboss.gwt.flux.meta.Store;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("org.jboss.gwt.flux.meta.Store")
public class StoreProcessor extends AbstractErrorAbsorbingProcessor {

    @Override
    protected boolean processWithExceptions(final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv)
            throws Exception {

        if (!roundEnv.processingOver()) {
            final Messager messager = processingEnv.getMessager();
            final Types typeUtils = processingEnv.getTypeUtils();

            for (Element e : roundEnv.getElementsAnnotatedWith(Store.class)) {
                if (e.getKind() == ElementKind.CLASS) {

                    TypeElement storeElement = (TypeElement) e;
                    PackageElement packageElement = (PackageElement) storeElement.getEnclosingElement();

                    final String packageName = packageElement.getQualifiedName().toString();
                    final String storeDelegate = storeElement.getSimpleName().toString();
                    final String storeClassName = GenerationUtil.storeImplementation(storeDelegate);
                    messager.printMessage(Diagnostic.Kind.NOTE, "Discovered annotated store [" + storeDelegate + "]");

                    StringBuilder errorMessage = new StringBuilder();
                    NoType voidType = typeUtils.getNoType(TypeKind.VOID);
                    List<ExecutableElement> receiveMethods = getAnnotatedMethods(storeElement, processingEnv,
                            Receive.class.getName(), voidType,
                            new String[]{Object.class.getName(), Dispatcher.Channel.class.getName().replace('$', '.')},
                            errorMessage);
                    if (receiveMethods.isEmpty()) {
                        messager.printMessage(ERROR,
                                "No receive methods found in [" + storeDelegate + "]. Please use @Receive to mark one or several methods as receive methods.");
                    } else {
                        Set<ReceiveInfo> receiveInfos = getReceiveInfos(messager, typeUtils, storeDelegate,
                                receiveMethods);

                        try {
                            messager.printMessage(Diagnostic.Kind.NOTE,
                                    "Generating code for [" + storeClassName + "]");
                            StoreGenerator generator = new StoreGenerator();
                            final StringBuffer code = generator.generate(packageName, storeClassName, storeDelegate,
                                    receiveInfos);
                            writeCode(packageName, storeClassName, code);

                            messager.printMessage(NOTE,
                                    "Successfully generated store implementation [" + storeClassName + "]");
                        } catch (GenerationException ge) {
                            final String msg = ge.getMessage();
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, storeElement);
                        }
                    }
                }
            }
        }
        return true;
    }

    private Set<ReceiveInfo> getReceiveInfos(final Messager messager, final Types typeUtils, final String storeDelegate,
            final List<ExecutableElement> receiveMethods) throws GenerationException {

        Set<ReceiveInfo> receiveInfos = new HashSet<>();
        for (ExecutableElement methodElement : receiveMethods) {

            // First parameter must be something annotated with @Action
            VariableElement payloadParameter = methodElement.getParameters().get(0);
            TypeElement payloadParameterType = (TypeElement) typeUtils.asElement(payloadParameter.asType());
            if (payloadParameterType.getAnnotation(Action.class) == null) {
                String error = String.format(
                        "The first parameter '%s' of the receive method '%s' in store '%s' must be annotated with @%s in order to act as the payload!",
                        payloadParameter.getSimpleName(), methodElement, storeDelegate, Action.class);
                messager.printMessage(Diagnostic.Kind.ERROR, error);
                throw new GenerationException(error);
            }

            ReceiveInfo receiveInfo = new ReceiveInfo(methodElement.getSimpleName().toString(),
                    payloadParameterType.getQualifiedName().toString());
            receiveInfos.add(receiveInfo);

            // read dependencies
            Collection<String> dependencies = Collections.emptySet();
            for (AnnotationMirror am : methodElement.getAnnotationMirrors()) {
                if (Receive.class.getName().equals(am.getAnnotationType().toString())) {
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am
                            .getElementValues().entrySet()) {
                        if ("dependencies".equals(entry.getKey().getSimpleName().toString())) {
                            dependencies = GenerationUtil.extractValue(entry.getValue());
                        }
                    }
                }
            }
            for (String dependency : dependencies) {
                receiveInfo.addDependency(storeImplementation(dependency) + ".class");
            }
        }
        return receiveInfos;
    }
}
