<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="storeClassName" type="java.lang.String" -->
<#-- @ftlvariable name="storeDelegate" type="java.lang.String" -->
<#-- @ftlvariable name="changeSupport" type="java.lang.Boolean" -->
<#-- @ftlvariable name="processInfos" type="java.util.Collection<org.jboss.gwt.circuit.processor.ProcessInfo>" -->
package ${packageName};

import java.util.logging.Logger;
import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.PropagatesChange.ChangeHandler;
import org.jboss.gwt.circuit.PropagatesError.ErrorHandler;
import org.jboss.gwt.circuit.StoreCallback;

/*
 * WARNING! This class is generated. Do not modify.
 */
@ApplicationScoped
@Generated("org.jboss.gwt.circuit.processor.StoreProcessor")
public class ${storeClassName} {

    <#if !changeSupport>
    private final static Logger LOG = Logger.getLogger("org.jboss.gwt.circuit");
    </#if>
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
                    delegate.${processInfo.method}((${processInfo.actionType})action, channel);
                    </#if>
                }
                </#list>
                else {
                    channel.nack(new RuntimeException("Unmatched action type " + action.getClass().getName() + " in store " + delegate.getClass()));
                }
            }

            @Override
            public void signalChange(final Action action) {
                <#if changeSupport>
                <#-- ChangeSupport.fireChange(Action) is protected on purpose, so we have to reimplement it here -->
                for (ChangeHandler handler : delegate.getChangeHandler(action)) {
                    handler.onChange(action);
                }
                for (ChangeHandler handler : delegate.getChangeHandler(action.getClass())) {
                    handler.onChange(action);
                }
                for (ChangeHandler handler : delegate.getChangeHandler()) {
                    handler.onChange(action);
                }
                <#else>
                LOG.warning("Cannot signal change event: " + ${storeDelegate}.class.getName() + " does not extend " + org.jboss.gwt.circuit.ChangeSupport.class.getName());
                </#if>
            }

            @Override
            public void signalError(final Action action, final Throwable throwable) {
                <#if changeSupport>
                <#-- ChangeSupport.fireError(Action, String) is protected on purpose, so we have to reimplement it here -->
                for (ErrorHandler handler : delegate.getErrorHandler(action)) {
                    handler.onError(action, throwable);
                }
                for (ErrorHandler handler : delegate.getErrorHandler(action.getClass())) {
                    handler.onError(action, throwable);
                }
                for (ErrorHandler handler : delegate.getErrorHandler()) {
                    handler.onError(action, throwable);
                }
                <#else>
                LOG.warning("Cannot signal throwable event: " + ${storeDelegate}.class.getName() + " does not extend " + org.jboss.gwt.circuit.ChangeSupport.class.getName());
                </#if>
            }
        });
    }
}
