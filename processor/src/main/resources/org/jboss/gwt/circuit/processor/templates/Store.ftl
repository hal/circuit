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
                    delegate.${processInfo.method}(((${processInfo.actionType})action).getPayload(), channel);
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
                Class<? extends Action> actionType = action.getClass();
                Iterable<Handler> actionHandlers = delegate.getActionHandler(action);
                if (actionHandlers.iterator().hasNext()) {
                    for (Handler actionHandler : actionHandlers) {
                        actionHandler.onChanged(actionType);
                    }
                } else {
                    Iterable<Handler> storeHandlers = delegate.getHandler();
                    for (Handler storeHandler : storeHandlers) {
                        storeHandler.onChanged(actionType);
                    }
                }
                <#else>
                System.out.println("WARN: Cannot signal change event: " + ${storeDelegate}.class.getName() + " does not extend " + org.jboss.gwt.circuit.ChangeSupport.class.getName());
                </#if>
            }
        });
    }
}
