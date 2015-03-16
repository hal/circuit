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

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
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

import javax.annotation.processing.*;
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
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.jboss.gwt.circuit.processor.GenerationUtil.ANY_PARAMS;

@AutoService(Processor.class)
@SupportedOptions("debug")
@SupportedAnnotationTypes("org.jboss.gwt.circuit.meta.Store")
public class StoreProcessor extends AbstractProcessor {

    static final String GRAPH_VIZ_OUTPUT = "dependencies.gv";

    private final Map<String, GraphVizInfo> graphVizInfos;
    private final Map<String, Multimap<String, String>> dagValidation;
    private final List<StoreDelegateMetadata> metadata;

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    public StoreProcessor() {
        graphVizInfos = new HashMap<>();
        dagValidation = new HashMap<>();
        metadata = new ArrayList<>();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // collect data
        for (Element e : roundEnv.getElementsAnnotatedWith(Store.class)) {
            TypeElement storeElement = (TypeElement) e;
            PackageElement packageElement = (PackageElement) storeElement.getEnclosingElement();
            String packageName = packageElement.getQualifiedName().toString();
            String storeDelegate = storeElement.getSimpleName().toString();
            boolean changeSupport = typeUtils.isAssignable(storeElement.asType(),
                    elementUtils.getTypeElement(ChangeSupport.class.getName()).asType());
            String storeClassName = storeDelegate + "Adapter";
            debug("Discovered annotated store [%s]", storeElement.getQualifiedName());

            try {
                List<ExecutableElement> processMethods = findValidProcessMethods(storeElement);
                Collection<ProcessInfo> processInfos = createProcessInfos(storeElement, processMethods);
                validateProcessMethods(storeElement, processInfos);
                metadata.add(new StoreDelegateMetadata(packageName, storeClassName, storeDelegate, changeSupport,
                        processInfos));
            } catch (GenerationException ge) {
                error(ge);
                return true;
            }
        }

        // generate code
        try {
            for (StoreDelegateMetadata md : metadata) {
                debug("Generating code for [%s]", md.storeClassName);
                StoreGenerator generator = new StoreGenerator();
                final StringBuffer code = generator.generate(md);
                writeCode(md.packageName, md.storeClassName, code);
                info("Successfully processed store [%s] -> [%s]", md.storeDelegate, md.storeClassName);
            }
            metadata.clear();
            if (roundEnv.processingOver()) {
                // Write the GraphViz file only once!
                String graphVizFile = writeGraphViz();
                validateDAG(graphVizFile);
                graphVizInfos.clear();
                dagValidation.clear();
            }
        } catch (IOException ioe) {
            error("Error generating code: %s", ioe.getMessage());
        }
        return false;
    }


    // ------------------------------------------------------ collect methods

    private List<ExecutableElement> findValidProcessMethods(final TypeElement storeElement)
            throws NoSuchElementException {
        NoType voidType = typeUtils.getNoType(TypeKind.VOID);
        List<ExecutableElement> allProcessMethods = GenerationUtil.getAnnotatedMethods(storeElement, processingEnv,
                Process.class.getName(), voidType, ANY_PARAMS);
        if (allProcessMethods.isEmpty()) {
            // no valid process methods!
            throw new GenerationException(storeElement,
                    String.format("%s does not contain suitable methods annotated with %s.",
                            storeElement.getQualifiedName(), Process.class.getName()));
        }
        return allProcessMethods;
    }

    private Collection<ProcessInfo> createProcessInfos(final TypeElement storeElement,
                                                       final List<ExecutableElement> processMethods) {
        final List<ProcessInfo> processInfos = new ArrayList<>();
        final String storeDelegate = storeElement.getSimpleName().toString();
        for (ExecutableElement methodElement : processMethods) {

            String actionType = null;
            Collection<String> dependencies = Collections.emptyList();

            // parse @Process parameter
            Optional<AnnotationMirror> processAnnotation = MoreElements.getAnnotationMirror(methodElement, Process.class);
            if (processAnnotation.isPresent()) {
                Map<ExecutableElement, AnnotationValue> values = AnnotationMirrors.getAnnotationValuesWithDefaults(processAnnotation.get());
                for (Map.Entry<ExecutableElement, AnnotationValue> entry : values.entrySet()) {
                    if ("actionType".equals(entry.getKey().getSimpleName().toString())) {
                        actionType = (String) ((Set) GenerationUtil.extractValue(entry.getValue())).iterator().next();
                    } else if ("dependencies".equals(entry.getKey().getSimpleName().toString())) {
                        dependencies = GenerationUtil.extractValue(entry.getValue());
                    }
                }
            }
            assert actionType != null;
            ProcessInfo processInfo = new ProcessInfo(actionType, methodElement);
            processInfos.add(processInfo);

            // collect dependencies
            for (String store : dependencies) {
                // IMPORTANT: The actual dependency is the store adaptee!
                processInfo.addDependency(store + ".class");
            }

            // analyze the process signature
            List<? extends VariableElement> parameters = methodElement.getParameters();
            if (parameters.size() == 2) {
                // first parameter is the action, the second parameter the dispatcher channel
                verifyProcessParameter(storeElement, methodElement, parameters.get(0), actionType);
                verifyProcessParameter(storeElement, methodElement, parameters.get(1), Dispatcher.Channel.class.getCanonicalName());
            } else if (parameters.size() == 1) {
                // if a single param is used it has to be the dispatcher channel
                verifyProcessParameter(storeElement, methodElement, parameters.get(0), Dispatcher.Channel.class.getCanonicalName());
            } else {
                // anything else is considered as an error
                throw new GenerationException(methodElement,
                        String.format("Illegal number of arguments on method '%s' in class '%s'",
                                methodElement.getSimpleName(), storeElement.getSimpleName()));
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


    // ------------------------------------------------------ verify methods

    private void verifyProcessParameter(TypeElement storeElement, ExecutableElement methodElement,
                                        VariableElement parameter, String expected) {
        TypeElement parameterType = MoreTypes.asTypeElement(typeUtils, parameter.asType());
        if (!parameterType.getQualifiedName().toString().equals(expected)) {
            throw new GenerationException(parameter,
                    String.format("Illegal parameter '%s' on method '%s' in class '%s'. Expected type '%s'",
                            parameter.getSimpleName(), methodElement.getSimpleName(), storeElement.getSimpleName(),
                            expected));
        }
    }

    private void validateProcessMethods(TypeElement storeElement, Collection<ProcessInfo> processInfos) {
        Map<String, ProcessInfo> actionTypes = new HashMap<>();
        for (ProcessInfo processInfo : processInfos) {
            ProcessInfo otherPi = actionTypes.get(processInfo.getActionType());
            if (otherPi != null) {
                throw new GenerationException(processInfo.getMethodElement(),
                        String.format("Ambiguous process method %s in store %s. This method uses the same action type as %s. " +
                                        "Please make sure that the action type is unique across all process method in one store.",
                                processInfo.getMethod(), storeElement.getSimpleName().toString(), otherPi.getMethod()));
            }
            actionTypes.put(processInfo.getActionType(), processInfo);
        }
    }

    private void validateDAG(final String graphVizFile) {
        boolean cyclesFound = false;
        for (Map.Entry<String, Multimap<String, String>> entry : dagValidation.entrySet()) {
            String actionType = entry.getKey();
            Multimap<String, String> dependencies = entry.getValue();
            debug("Check cyclic dependencies for action [%s]", actionType);
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
                error("Cyclic dependencies detected for action [%s]: %s. Please review [%s] for more details.",
                        actionType, cycleInfo, graphVizFile);
            }
            if (!cyclesFound) {
                debug("No cyclic dependencies found for action [%s]", actionType);
            }
        }
    }

    // ------------------------------------------------------ generate methods

    private void writeCode(final String packageName, final String className, final StringBuffer code)
            throws IOException {
        JavaFileObject jfo = filer.createSourceFile(packageName + "." + className);
        Writer w = jfo.openWriter();
        BufferedWriter bw = new BufferedWriter(w);
        bw.append(code);
        bw.close();
        w.close();
    }

    private String writeGraphViz() throws IOException {
        GraphVizGenerator generator = new GraphVizGenerator();
        StringBuffer code = generator.generate(graphVizInfos.values());
        debug("Generating GraphViz file to visualize store dependencies [%s]", GRAPH_VIZ_OUTPUT);
        FileObject fo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", GRAPH_VIZ_OUTPUT);
        Writer w = fo.openWriter();
        BufferedWriter bw = new BufferedWriter(w);
        bw.append(code);
        bw.close();
        w.close();
        debug("Successfully generated GraphViz file [%s]", GRAPH_VIZ_OUTPUT);
        return fo.getName();
    }


    // ------------------------------------------------------ logging

    private void debug(String msg, Object... args) {
        if (processingEnv.getOptions().containsKey("debug")) {
            messager.printMessage(NOTE, String.format(msg, args));
        }
    }

    private void info(String msg, Object... args) {
        messager.printMessage(NOTE, String.format(msg, args));
    }

    private void error(String msg, Object... args) {
        messager.printMessage(ERROR, String.format(msg, args));
    }

    private void error(GenerationException generationException) {
        if (generationException.getElement() != null) {
            messager.printMessage(ERROR, generationException.getMessage(), generationException.getElement());
        } else {
            messager.printMessage(ERROR, generationException.getMessage());
        }
    }
}
