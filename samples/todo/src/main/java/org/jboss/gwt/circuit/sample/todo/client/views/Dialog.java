package org.jboss.gwt.circuit.sample.todo.client.views;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Dialog {

    public static void askFor(String message, final AsyncCallback<String> callback) {

        final DialogBox dialog = new DialogBox();
        dialog.setText(message);
        dialog.setPixelSize(320, 240);
        dialog.setModal(true);
        dialog.setAutoHideEnabled(true);

        VerticalPanel panel = new VerticalPanel();
        final TextArea text = new TextArea();
        panel.add(text);
        panel.add(new Button("Done", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dialog.hide();
            }
        }));

        dialog.setWidget(panel);

        dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                callback.onSuccess(text.getText());
            }
        });

        dialog.center();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                text.setFocus(true);
            }
        });
    }
}
