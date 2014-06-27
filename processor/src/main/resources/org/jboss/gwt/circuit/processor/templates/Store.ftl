<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="storeClassName" type="java.lang.String" -->
<#-- @ftlvariable name="storeDelegate" type="java.lang.String" -->
<#-- @ftlvariable name="receiveInfos" type="java.util.List<org.jboss.gwt.circuit.processor.ReceiveInfo>" -->
<#-- @ftlvariable name="cdi" type="java.lang.Boolean" -->
package ${packageName};

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.Agreement;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.StoreCallback;

/*
 * WARNING! This class is generated. Do not modify.
 */
@ApplicationScoped
@Generated("org.jboss.gwt.circuit.processor.StoreProcessor")
public class ${storeClassName} {

    @Inject
    public ${storeClassName}(final ${storeDelegate} delegate, final Dispatcher dispatcher) {
        dispatcher.register(${storeDelegate}.class, new StoreCallback() {
            @Override
            public Agreement voteFor(final Action action) {
                Agreement agreement = Agreement.NONE;
                <#list receiveInfos as receiveInfo>
                if (action instanceof ${receiveInfo.actionType}) {
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
            public void complete(final Action action, final Dispatcher.Channel channel) {
                boolean matched = false;

                <#list receiveInfos as receiveInfo>
                if (action instanceof ${receiveInfo.actionType}) {
                    <#if receiveInfo.isSingleArg()>
                        delegate.${receiveInfo.method}(channel);
                    <#else>
                        delegate.${receiveInfo.method}(((${receiveInfo.actionType})action).getPayload(), channel);
                    </#if>
                    matched = true;
                }
                </#list>

                if (!matched) {
                    System.out.println("WARN: Unmatched action " + action.getClass().getName() + " in store " + delegate.getClass());
                    channel.ack();
                }
            }
        });
    }
}
