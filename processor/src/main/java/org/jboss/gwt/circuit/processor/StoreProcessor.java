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

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.jboss.gwt.circuit.processor.GenerationUtil.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.meta.Action;
import org.jboss.gwt.circuit.meta.Receive;
import org.jboss.gwt.circuit.meta.Store;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

@SupportedOptions({"cdi"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("org.jboss.gwt.circuit.meta.Store")
public class StoreProcessor extends AbstractErrorAbsorbingProcessor {

    static final String GRAPH_VIZ_OUTPUT = "dependencies.gv";

    private final Map<String, GraphVizInfo> graphVizInfos;
    private final Map<String, Multimap<String, String>> dagValidation;

    public StoreProcessor() {
        graphVizInfos = new HashMap<>();
        dagValidation = new HashMap<>();
    }

    @Override
    protected boolean processWithExceptions(final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv)
            throws Exception {

        if (roundEnv.errorRaised()) {
            return false;
        }

        final Messager messager = processingEnv.getMessager();
        if (!roundEnv.processingOver()) {
            final Types typeUtils = processingEnv.getTypeUtils();
            final Elements elementUtils = processingEnv.getElementUtils();
            final Map<String, String> options = processingEnv.getOptions();
            final Boolean cdi = Boolean.valueOf(options.get(OPT_CDI));

            for (Element e : roundEnv.getElementsAnnotatedWith(Store.class)) {
                TypeElement storeElement = (TypeElement) e;
                PackageElement packageElement = (PackageElement) storeElement.getEnclosingElement();

                final String packageName = packageElement.getQualifiedName().toString();
                final String storeDelegate = storeElement.getSimpleName().toString();
                final String storeClassName = GenerationUtil.storeImplementation(storeDelegate);
                messager.printMessage(NOTE, "Discovered annotated store [" + storeElement.getQualifiedName() + "]");

                List<ExecutableElement> receiveMethods = new ArrayList<>();
                if (findValidReceiveMethods(messager, typeUtils, elementUtils, storeElement, receiveMethods)) {
                    Set<ReceiveInfo> receiveInfos = getReceiveInfos(messager, typeUtils, storeElement,
                            receiveMethods);
                    try {
                        messager.printMessage(NOTE, "Generating code for [" + storeClassName + "]");
                        StoreGenerator generator = new StoreGenerator();
                        final StringBuffer code = generator.generate(packageName, storeClassName, storeDelegate,
                                receiveInfos, cdi);
                        writeCode(packageName, storeClassName, code);

                        messager.printMessage(NOTE,
                                "Successfully generated store implementation [" + storeClassName + "]");
                    } catch (GenerationException ge) {
                        final String msg = ge.getMessage();
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, storeElement);
                    }
                } else {
                    // no valid receive methods - get out!
                    break;
                }
            }
        } else {
            // After all files were generated write GraphViz and validate dependencies
            String graphVizFile = writeGraphViz(messager);
            validateDAG(messager, graphVizFile);
        }
        return true;
    }

    private boolean findValidReceiveMethods(final Messager messager, final Types typeUtils, final Elements elementUtils,
            final TypeElement storeElement, List<ExecutableElement> receiveMethods) {

        boolean valid = true;
        StringBuilder errorMessage = new StringBuilder();
        NoType voidType = typeUtils.getNoType(TypeKind.VOID);
        List<ExecutableElement> allReceiveMethods = getAnnotatedMethods(storeElement, processingEnv,
                Receive.class.getName(), voidType, ANY_PARAMS, errorMessage);
        if (allReceiveMethods.isEmpty()) {
            messager.printMessage(ERROR, String.format(
                    "No receive methods found in [%s]. Please use @%s to mark one or several methods as receive methods.",
                    storeElement.getQualifiedName(), Receive.class.getName()));
            valid = false;
        }
        for (ExecutableElement receiveMethod : allReceiveMethods) {
            if (receiveMethod.getParameters().size() != 2) {
                messager.printMessage(ERROR, String.format(
                        "Receive method '%s' in store '%s' has wrong number of arguments: Expected 2, found %d",
                        receiveMethod, storeElement.getQualifiedName(), receiveMethod.getParameters().size()));
                valid = false;
            }
            else if (!GenerationUtil.doParametersMatch(typeUtils, elementUtils, receiveMethod,
                    new String[]{Object.class.getName(), Dispatcher.Channel.class.getName().replace('$', '.')})) {
                messager.printMessage(ERROR, String.format(
                        "Receive method '%s' in store '%s' has wrong signature: Expected '%s(<Payload>, %s)'",
                        receiveMethod, storeElement.getQualifiedName(), receiveMethod.getSimpleName(),
                        Dispatcher.Channel.class.getName()));
                valid = false;
            } else {
                receiveMethods.add(receiveMethod);
            }
        }
        return valid;
    }

    private Set<ReceiveInfo> getReceiveInfos(final Messager messager, final Types typeUtils,
            final TypeElement storeElement, final List<ExecutableElement> receiveMethods) throws GenerationException {

        final Set<ReceiveInfo> receiveInfos = new HashSet<>();
        final String storeDelegate = storeElement.getSimpleName().toString();
        for (ExecutableElement methodElement : receiveMethods) {

            // First parameter must be something annotated with @Action
            VariableElement payloadParameter = methodElement.getParameters().get(0);
            TypeElement payloadParameterType = (TypeElement) typeUtils.asElement(payloadParameter.asType());
            if (payloadParameterType.getAnnotation(Action.class) == null) {
                String error = String.format(
                        "The first parameter '%s' of the receive method '%s' in store '%s' must be annotated with @%s in order to act as payload!",
                        payloadParameter.getSimpleName(), methodElement, storeElement.getQualifiedName(),
                        Action.class.getName());
                messager.printMessage(Diagnostic.Kind.ERROR, error);
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

            // record dependencies in a different data structures to generate GraphViz...
            String payload = payloadParameterType.getSimpleName().toString();
            GraphVizInfo graphVizInfo = graphVizInfos.get(payload);
            if (graphVizInfo == null) {
                graphVizInfo = new GraphVizInfo(payload);
                graphVizInfos.put(payload, graphVizInfo);
            }
            graphVizInfo.addStore(storeDelegate);
            List<String> simpleDependencies = new LinkedList<>();
            for (String dependency : dependencies) {
                String simpleDependency = dependency.substring(dependency.lastIndexOf('.') + 1);
                simpleDependencies.add(simpleDependency);
                graphVizInfo.addStore(simpleDependency);
                graphVizInfo.addDependency(storeDelegate, simpleDependency);
            }

            // ...and verify DAG
            Multimap<String, String> dag = dagValidation.get(payload);
            if (dag == null) {
                dag = HashMultimap.create();
                dagValidation.put(payload, dag);
            }
            dag.putAll(storeDelegate, simpleDependencies);
        }
        return receiveInfos;
    }

    private String writeGraphViz(final Messager messager) throws GenerationException, IOException {
        GraphVizGenerator generator = new GraphVizGenerator();
        StringBuffer code = generator.generate(graphVizInfos.values());
        messager.printMessage(NOTE,
                "Generating GraphViz file to visualize store dependencies [" + GRAPH_VIZ_OUTPUT + "]");
        FileObject fo = processingEnv.getFiler()
                .createResource(StandardLocation.SOURCE_OUTPUT, "", GRAPH_VIZ_OUTPUT);
        Writer w = fo.openWriter();
        BufferedWriter bw = new BufferedWriter(w);
        bw.append(code);
        bw.close();
        w.close();
        messager.printMessage(NOTE, "Successfully generated GraphViz file [" + GRAPH_VIZ_OUTPUT + "]");
        return fo.getName();
    }

    private void validateDAG(final Messager messager, final String graphVizFile) throws GenerationException {
        boolean cyclesFound = false;
        for (Map.Entry<String, Multimap<String, String>> entry : dagValidation.entrySet()) {
            String payload = entry.getKey();
            Multimap<String, String> dependencies = entry.getValue();
            messager.printMessage(NOTE, "Check cyclic dependencies for action [" + payload + "]");
            DirectedGraph<String, DefaultEdge> dg = new DefaultDirectedGraph<>(DefaultEdge.class);

            // vertices
            for (String store : dependencies.keySet()) {
                dg.addVertex(store);
            }
            for (String store : dependencies.values()) {
                dg.addVertex(store);
            }

            // edges
            for (String store : dependencies.keySet()) {
                Collection<String> storeDependencies = dependencies.get(store);
                for (String storeDependency : storeDependencies) {
                    dg.addEdge(store, storeDependency);
                }
            }

            // cycles?
            CycleDetector<String, DefaultEdge> detector = new CycleDetector<>(dg);
            List<String> cycles = new LinkedList<>(detector.findCycles());
            if (!cycles.isEmpty()) {
                cyclesFound = true;
                StringBuilder cycleInfo = new StringBuilder();
                for (String cycle : cycles) {
                    cycleInfo.append(cycle).append(" -> ");
                }
                cycleInfo.append(cycles.get(0));
                messager.printMessage(ERROR,
                        "Cyclic dependencies detected for action [" + payload + "]: " + cycleInfo);
                messager.printMessage(ERROR, "Please review [" + graphVizFile + "] for more details.");
            }
            if (!cyclesFound) {
                messager.printMessage(NOTE, "No cyclic dependencies found for action [" + payload + "]");
            }
        }
    }
}
