<#-- @ftlvariable name="graphVizInfos" type="java.util.Set<org.jboss.gwt.circuit.processor.GraphVizInfo>" -->
digraph store_dependencies {

    graph [fontname="Helvetica"];
    node [fontname="Helvetica"];
    edge [fontname="Helvetica"];

    label="Store Dependencies";
    labelloc=top;

    <#list graphVizInfos as graphVizInfo>
        subgraph cluster_${graphVizInfo.payload} {
            label="Action '${graphVizInfo.payload}'";
            <#list graphVizInfo.dependencies as dependency>
                ${graphVizInfo.payload}_${dependency[0]} -> ${graphVizInfo.payload}_${dependency[1]};
            </#list>

            <#list graphVizInfo.stores as store>
                ${graphVizInfo.payload}_${store} [label="${store}"];
            </#list>
        }
    </#list>
}