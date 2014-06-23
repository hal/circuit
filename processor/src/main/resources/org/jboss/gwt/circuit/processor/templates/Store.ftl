<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="storeClassName" type="java.lang.String" -->
<#-- @ftlvariable name="storeDelegate" type="java.lang.String" -->
<#-- @ftlvariable name="receiveInfos" type="java.util.List<org.jboss.gwt.circuit.processor.ReceiveInfo>" -->
<#-- @ftlvariable name="cdi" type="java.lang.Boolean" -->
package ${packageName};

import javax.annotation.Generated;
<#if cdi>
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
</#if>

import org.jboss.gwt.circuit.AbstractStore;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;

/*
 * WARNING! This class is generated. Do not modify.
 */
<#if cdi>
@ApplicationScoped
</#if>
@Generated("org.jboss.gwt.circuit.processor.StoreProcessor")
public class ${storeClassName} extends AbstractStore {

    private final ${storeDelegate} delegate;

    <#if cdi>
    @Inject
    public ${storeClassName}(final ${storeDelegate} delegate, final Dispatcher dispatcher) {
        this.delegate = delegate;
    <#else>
    public ${storeClassName}(final Dispatcher dispatcher) {
        this.delegate = new ${storeDelegate}();
    </#if>

        dispatcher.register(${storeClassName}.class, new Callback() {
            @Override
            public Agreement voteFor(final Action action) {
                Agreement agreement = Agreement.NONE;
                <#list receiveInfos as receiveInfo>
                if (action instanceof ${receiveInfo.action}) {
                <#if receiveInfo.hasDependencies()>
                    agreement = new Agreement(true, ${receiveInfo.dependencies});
                <#else>
                    agreement = new Agreement(true);
                </#if>
                }
                </#list>
                return agreement;
            }

            @Override
            public void execute(final Action action, final Dispatcher.Channel channel) {
                <#list receiveInfos as receiveInfo>
                if (action instanceof ${receiveInfo.action}) {
                    ${receiveInfo.action} concreteAction = (${receiveInfo.action}) action;
                    delegate.${receiveInfo.method}(concreteAction.getPayload(), channel);
                }
                </#list>
            }
        });
    }

    public ${storeDelegate} getDelegate() {
        return delegate;
    }
}
