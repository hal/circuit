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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jboss.gwt.circuit.ChangeSupport;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.meta.Process;
import org.jboss.gwt.circuit.meta.Store;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.jboss.gwt.circuit.processor.GenerationUtil.ANY_PARAMS;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("org.jboss.gwt.circuit.meta.Store")
public class StoreProcessor extends AbstractErrorAbsorbingProcessor {

    static final String GRAPH_VIZ_OUTPUT = "dependencies.gv";

    private final Map<String, GraphVizInfo> graphVizInfos;
    private final Map<String, Multimap<String, String>> dagValidation;
    private final List<StoreDelegateMetadata> metadata;

    public StoreProcessor() {
        graphVizInfos = new HashMap<>();
        dagValidation = new HashMap<>();
        metadata = new ArrayList<>();
    }

    @Override
    protected boolean processWithExceptions(final Set<? extends TypeElement> annotations,
                                            final RoundEnvironment roundEnv) throws Exception {

        if (roundEnv.errorRaised()) {
            return false;
        }
        if (!roundEnv.processingOver()) {
            collectData(roundEnv);
        } else {
            generateFiles();
        }
        return true;
    }


    // ------------------------------------------------------ collect methods

    private void collectData(final RoundEnvironment roundEnv) throws Exception {

        final Messager messager = processingEnv.getMessager();
        final Types typeUtils = processingEnv.getTypeUtils();
        final Elements elementUtils = processingEnv.getElementUtils();

        for (Element e : roundEnv.getElementsAnnotatedWith(Store.class)) {
            TypeElement storeElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) storeElement.getEnclosingElement();

            final String packageName = packageElement.getQualifiedName().toString();
            final String storeDelegate = storeElement.getSimpleName().toString();
            final boolean changeSupport = typeUtils.isAssignable(storeElement.asType(),
                    elementUtils.getTypeElement(ChangeSupport.class.getName()).asType());
            final String storeClassName = GenerationUtil.storeImplementation(storeDelegate);
            messager.printMessage(NOTE,
                    String.format("Discovered annotated store [%s]", storeElement.getQualifiedName()));

            List<ExecutableElement> processMethods = new ArrayList<>();
            if (findValidProcessMethods(messager, typeUtils, storeElement, processMethods)) {
                Collection<ProcessInfo> processInfos = createProcessInfos(messager, typeUtils, elementUtils,
                        storeElement, processMethods);

                metadata.add(new StoreDelegateMetadata(packageName, storeClassName, storeDelegate, changeSupport,
                        processInfos));
            } else {
                // no valid process methods!
                messager.printMessage(ERROR,
                        String.format("%s does not contain suitable methods annotated with %s.",
                                storeElement.getQualifiedName(), Process.class.getName()));
                break;
            }
        }
    }

    private boolean findValidProcessMethods(final Messager messager, final Types typeUtils,
                                            final TypeElement storeElement, List<ExecutableElement> processMethods) {

        boolean valid = true;
        NoType voidType = typeUtils.getNoType(TypeKind.VOID);
        List<ExecutableElement> allProcessMethods = GenerationUtil.getAnnotatedMethods(storeElement, processingEnv,
                Process.class.getName(), voidType, ANY_PARAMS);
        if (allProcessMethods.isEmpty()) {
            messager.printMessage(ERROR, String.format(
                    "No process methods found in [%s]. Please use @%s to mark one or several methods as process methods.",
                    storeElement.getQualifiedName(), Process.class.getName()));
            valid = false;
        }
        for (ExecutableElement processMethod : allProcessMethods) {
            processMethods.add(processMethod);
        }
        return valid;
    }

    private Collection<ProcessInfo> createProcessInfos(final Messager messager, final Types typeUtils,
                                                       Elements elementUtils, final TypeElement storeElement,
                                                       final List<ExecutableElement> processMethods)
            throws GenerationException {

        final List<ProcessInfo> processInfos = new LinkedList<>();
        final String storeDelegate = storeElement.getSimpleName().toString();
        for (ExecutableElement methodElement : processMethods) {

            String actionType = Void.class.getCanonicalName();
            Collection<String> dependencies = Collections.emptySet();
            AnnotationMirror processAnnotation = GenerationUtil.getAnnotation(elementUtils, methodElement, Process.class.getName());
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : processAnnotation.getElementValues().entrySet()) {
                if ("dependencies".equals(entry.getKey().getSimpleName().toString())) {
                    dependencies = GenerationUtil.extractValue(entry.getValue());
                } else if ("actionType".equals(entry.getKey().getSimpleName().toString())) {
                    actionType = (String) ((Set) GenerationUtil.extractValue(entry.getValue())).iterator().next();
                }
            }

            TypeElement actionTypeElement = elementUtils.getTypeElement(actionType);
            ProcessInfo processInfo = new ProcessInfo(methodElement.getSimpleName().toString(), actionType);
            processInfos.add(processInfo);
            for (String store : dependencies) {
                // IMPORTANT: The actual dependency is the store adaptee!
                processInfo.addDependency(store + ".class");
            }

            List<? extends VariableElement> parameters = methodElement.getParameters();
            if (parameters.size() == 1) {
                // if a single param is used it needs to be the dispatcher channel
                verifyDispatcherChannel(messager, typeUtils, storeElement, methodElement, parameters.get(0));
                continue;

            } else if (parameters.size() > 1) {
                // parameters 1..n-1 are payload, the last one is the dispatcher channel
                for (int i = 0; i < parameters.size(); i++) {
                    if (i == parameters.size() - 1) {
                        verifyDispatcherChannel(messager, typeUtils, storeElement, methodElement, parameters.get(i));
                    } else {
                        VariableElement parameter = parameters.get(i);
                        TypeElement parameterType = (TypeElement) typeUtils.asElement(parameter.asType());
                        String payloadType = parameterType.getQualifiedName().toString();
                        String payloadName = parameter.getSimpleName().toString();

                        // Check getter in action type
                        List<ExecutableElement> getter = GenerationUtil.findGetter(actionTypeElement, processingEnv, parameterType.asType(), payloadName);
                        if (getter.isEmpty()) {
                            String error = String.format("No getter found for payload parameter '%s %s' on method '%s' in class '%s'",
                                    payloadType, payloadName, methodElement.getSimpleName(), storeElement.getSimpleName());
                            messager.printMessage(Diagnostic.Kind.ERROR, error);
                            continue;
                        }
                        processInfo.addPayload(getter.get(0).getSimpleName().toString());
                    }
                }

            } else {
                // anything else is considered an error
                String error = String.format(
                        "No valid process method '%s' in class '%s'. Please provide at least a parameter of type '%s'",
                        methodElement.getSimpleName(), storeElement.getSimpleName(), Dispatcher.Channel.class.getSimpleName());
                messager.printMessage(Diagnostic.Kind.ERROR, error);
                continue;
            }

            // record dependencies in a different data structures to generate GraphViz...
            GraphVizInfo graphVizInfo = graphVizInfos.get(actionType);
            if (graphVizInfo == null) {
                String shortActionType = actionType.substring(actionType.lastIndexOf('.') + 1);
                graphVizInfo = new GraphVizInfo(shortActionType);
                graphVizInfos.put(actionType, graphVizInfo);
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
            Multimap<String, String> dag = dagValidation.get(actionType);
            if (dag == null) {
                dag = HashMultimap.create();
                dagValidation.put(actionType, dag);
            }
            dag.putAll(storeDelegate, simpleDependencies);
        }

        return processInfos;
    }

    private void verifyDispatcherChannel(final Messager messager, final Types typeUtils,
                                         TypeElement storeElement, ExecutableElement methodElement, VariableElement param) {
        TypeElement paramType = (TypeElement) typeUtils.asElement(param.asType());
        if (!paramType.getQualifiedName().toString().equals(Dispatcher.Channel.class.getCanonicalName())) {
            String error = String.format(
                    "Illegal type for parameter '%s' on method '%s' in class '%s'. Expected type '%s'",
                    param.getSimpleName(), methodElement.getSimpleName(), storeElement.getSimpleName(),
                    Dispatcher.Channel.class.getCanonicalName());
            messager.printMessage(Diagnostic.Kind.ERROR, error);
        }
    }


    // ------------------------------------------------------ generate methods

    private void generateFiles() throws Exception {
        final Messager messager = processingEnv.getMessager();

        // store delegates
        for (StoreDelegateMetadata md : metadata) {
            try {
                messager.printMessage(NOTE, String.format("Generating code for [%s]", md.storeClassName));
                StoreGenerator generator = new StoreGenerator();
                final StringBuffer code = generator.generate(md);
                writeCode(md.packageName, md.storeClassName, code);

                messager.printMessage(NOTE,
                        String.format("Successfully generated store implementation [%s]", md.storeClassName));
            } catch (GenerationException ge) {
                final String msg = ge.getMessage();
                messager.printMessage(Diagnostic.Kind.ERROR, msg/* , storeElement*/);
            }
        }

        // GraphVIZ
        String graphVizFile = writeGraphViz();
        validateDAG(graphVizFile);
    }

    private String writeGraphViz() throws GenerationException, IOException {
        final Messager messager = processingEnv.getMessager();
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

    private void validateDAG(final String graphVizFile) throws GenerationException {
        boolean cyclesFound = false;
        final Messager messager = processingEnv.getMessager();
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
