package org.jboss.gwt.circuit.sample.todo.client.views;

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.gwt.circuit.Action;
import org.jboss.gwt.circuit.dag.DAGDispatcher;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@SuppressWarnings("UnusedDeclaration")
public class DiagnosticsView implements DAGDispatcher.Diagnostics, IsWidget {

    private InlineLabel info;
    private int dispatched;
    private int executed;
    private int acked;
    private int nacked;
    private boolean locked;

    public DiagnosticsView() {
        this.info = new InlineLabel();
    }

    @Override
    public Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        layout.getElement().setAttribute("style", "width:100%;");
        layout.add(info);

        return layout;
    }

    public void refresh() {
        StringBuilder message = new StringBuilder().append("dispatched: ").append(dispatched).append(", executed: ")
                .append(executed).append(", acked: ").append(acked).append(", nacked: ").append(nacked);
        if (locked) {
            info.addStyleName("diagnostics-locked");
            info.removeStyleName("diagnostics");
            info.setText(message.toString());
        } else {
            info.removeStyleName("diagnostics-locked");
            info.addStyleName("diagnostics");
            info.setText(message.toString());
        }
    }

    @Override
    public void onDispatch(final Action a) {
        dispatched++;
        refresh();
    }

    @Override
    public void onLock() {
        locked = true;
        refresh();
    }

    @Override
    public void onExecute(final Class<?> s, final Action a) {
        executed++;
        refresh();
    }

    @Override
    public void onAck(final Class<?> s, final Action a) {
        acked++;
        refresh();
    }

    @Override
    public void onNack(final Class<?> s, final Action a, final Throwable t) {
        nacked++;
        refresh();
    }

    @Override
    public void onUnlock() {
        locked = false;
        refresh();
    }
}
