<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="actionClassName" type="java.lang.String" -->
<#-- @ftlvariable name="payloadClassName" type="java.lang.String" -->
package ${packageName};

import javax.annotation.Generated;

import org.jboss.gwt.flux.Action;

/*
 * WARNING! This class is generated. Do not modify.
 */
@Generated("org.jboss.gwt.flux.processor.ActionProcessor")
public class ${actionClassName} implements Action<${payloadClassName}> {

    private final ${payloadClassName} payload;

    public ${actionClassName}(final ${payloadClassName} payload) {this.payload = payload;}

    @Override
    public ${payloadClassName} getPayload() {
        return payload;
    }
}
