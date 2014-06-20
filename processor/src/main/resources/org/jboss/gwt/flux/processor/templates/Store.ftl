<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="storeClassName" type="java.lang.String" -->
<#-- @ftlvariable name="storeDelegate" type="java.lang.String" -->
<#-- @ftlvariable name="receiveInfos" type="java.util.List<org.jboss.gwt.flux.processor.ReceiveInfo>" -->
package ${packageName};

import javax.annotation.Generated;

import org.jboss.gwt.flux.AbstractStore;
import org.jboss.gwt.flux.Action;
import org.jboss.gwt.flux.Agreement;
import org.jboss.gwt.flux.Dispatcher;

/*
 * WARNING! This class is generated. Do not modify.
 */
@Generated("org.jboss.gwt.flux.processor.StoreProcessor")
public class ${storeClassName} extends AbstractStore {

    private final ${storeDelegate} delegate;

    public ${storeClassName}(Dispatcher dispatcher) {
        delegate = new ${storeDelegate}();

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
}
