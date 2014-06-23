<#-- @ftlvariable name="packageName" type="java.lang.String" -->
<#-- @ftlvariable name="actionClassName" type="java.lang.String" -->
<#-- @ftlvariable name="payloadClassName" type="java.lang.String" -->
package ${packageName};

import javax.annotation.Generated;

import org.jboss.gwt.circuit.Action;

/*
 * WARNING! This class is generated. Do not modify.
 */
@Generated("org.jboss.gwt.circuit.processor.ActionProcessor")
public class ${actionClassName} implements Action<${payloadClassName}> {

    private final ${payloadClassName} payload;

    public ${actionClassName}(final ${payloadClassName} payload) {this.payload = payload;}

    @Override
    public ${payloadClassName} getPayload() {
        return payload;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof ${actionClassName})) { return false; }

        ${actionClassName} that = (${actionClassName}) o;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) { return false; }
        return true;
    }

    @Override
    public int hashCode() {
        return payload != null ? payload.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Action<${payloadClassName}>(" + payload + ")";
    }
}
