<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="storeClassName" type="java.lang.String" -->
<#-- @ftlvariable name="storeDelegate" type="java.lang.String" -->
<#-- @ftlvariable name="changeSupport" type="java.lang.Boolean" -->
<#-- @ftlvariable name="processInfos" type="java.util.List<org.jboss.gwt.circuit.processor.ProcessInfo>" -->
package ${packageName};

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.PropagatesChange.Handler;
import org.jboss.gwt.circuit.StoreCallback;

/*
 * WARNING! This class is generated. Do not modify.
 */
@ApplicationScoped
@Generated("org.jboss.gwt.circuit.processor.StoreProcessor")
public class ${storeClassName} {

    private final ${storeDelegate} delegate;

    @Inject
    public ${storeClassName}(final ${storeDelegate} delegate, final Dispatcher dispatcher) {
        this.delegate = delegate;

        dispatcher.register(${storeDelegate}.class, new StoreCallback() {
            @Override
            public Agreement voteFor(final Action action) {
                <#list processInfos as processInfo>
                <#if processInfo_index == 0>
                if (action instanceof ${processInfo.actionType}) {
                <#else>
                else if (action instanceof ${processInfo.actionType}) {
                </#if>
                <#if processInfo.hasDependencies()>
                    return new Agreement(true, ${processInfo.dependencies});
                <#else>
                    return new Agreement(true);
                </#if>
                }
                </#list>
                else {
                    return Agreement.NONE;
                }
            }

            @Override
            public void complete(final Action action, final Dispatcher.Channel channel) {
                <#list processInfos as processInfo>
                <#if processInfo_index == 0>
                if (action instanceof ${processInfo.actionType}) {
                <#else>
                else if (action instanceof ${processInfo.actionType}) {
                </#if>
                    <#if processInfo.singleArg>
                    delegate.${processInfo.method}(channel);
                    <#else>
                    delegate.${processInfo.method}(<#list processInfo.getPayload() as payload>((${processInfo.actionType})action).${payload}()</#list>, channel);
                    </#if>
                }
                </#list>
                else {
                    System.out.println("WARN: Unmatched action " + action.getClass().getName() + " in store " + delegate.getClass());
                    channel.ack();
                }
            }

            @Override
            public void signalChange(final Action action) {
                <#if changeSupport>
                <#-- ChangeSupport.fireChange(Action) is protected on purpose, so we have to reimplement it here -->
                Iterable<Handler> handler = delegate.getActionHandler(action);
                for (Handler h : handler) {
                    h.onChange(action);
                }
                handler = delegate.getActionHandler(action.getClass());
                for (Handler h : handler) {
                    h.onChange(action);
                }
                handler = delegate.getActionHandler();
                for (Handler h : handler) {
                    h.onChange(action);
                }
                <#else>
                System.out.println("WARN: Cannot signal change event: " + ${storeDelegate}.class.getName() + " does not extend " + org.jboss.gwt.circuit.ChangeSupport.class.getName());
                </#if>
            }
        });
    }
}
