<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="dispatcherClassName" type="java.lang.String" -->
<#-- @ftlvariable name="actionInfos" type="java.util.Set<org.jboss.gwt.flux.processor.ActionInfo>" -->
<#-- @ftlvariable name="cdi" type="java.lang.Boolean" -->
package ${packageName};

import javax.annotation.Generated;
<#if cdi>
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
</#if>

import org.jboss.gwt.flux.Dispatcher;
import org.jboss.gwt.flux.Dispatcher.Diagnostics;

/*
* WARNING! This class is generated. Do not modify.
*/
<#if cdi>
@ApplicationScoped
</#if>
@Generated("org.jboss.gwt.flux.processor.ActionProcessor")
public class ${dispatcherClassName} {

    private final Dispatcher dispatcher;

    <#if cdi>
    @Inject
    </#if>
    public ${dispatcherClassName}(final Dispatcher dispatcher) {this.dispatcher = dispatcher;}

    <#list actionInfos as actionInfo>
    public void dispatch(${actionInfo.payload} action) {
        dispatcher.dispatch(new ${actionInfo.action}(action));
    }
    </#list>

    public void addDiagnostics(Diagnostics diagnostics) {
        dispatcher.addDiagnostics(diagnostics);
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }
}
