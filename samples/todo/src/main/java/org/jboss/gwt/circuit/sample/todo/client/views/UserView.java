package org.jboss.gwt.circuit.sample.todo.client.views;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.PropagatesChange;
import org.jboss.gwt.circuit.sample.todo.client.actions.AddUser;
import org.jboss.gwt.circuit.sample.todo.client.actions.RemoveUser;
import org.jboss.gwt.circuit.sample.todo.client.actions.SelectUser;
import org.jboss.gwt.circuit.sample.todo.client.stores.UserStore;

@ApplicationScoped
public class UserView extends Composite {

    private final ListDataProvider<String> dataProvider;
    private final CellTable<String> table;
    private final Button removeButton;

    @Inject
    UserStore userStore;

    @Inject
    Dispatcher dispatcher;

    public UserView() {

        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "padding:20px;width:100%");

        table = new CellTable<>();
        table.getElement().setAttribute("style", "width:90%");
        table.setEmptyTableWidget(new HTML("No Todo items found!"));

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(table);

        TextColumn<String> userColumn = new TextColumn<String>() {
            @Override
            public String getValue(String s) {
                return s;
            }
        };
        table.addColumn(userColumn, "User");
        final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                dispatcher.dispatch(new SelectUser(selectionModel.getSelectedObject()));
            }
        });
        table.setSelectionModel(selectionModel);

        layout.add(table);

        Button addButton = new Button("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                Dialog.askFor("What's the name of the user?", new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(String s) {
                        dispatcher.dispatch(new AddUser(s));
                    }
                });
            }
        });

        removeButton = new Button("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dispatcher.dispatch(new RemoveUser(selectionModel.getSelectedObject()));
            }
        });
        removeButton.setEnabled(false);

        HorizontalPanel tools = new HorizontalPanel();
        tools.add(addButton);
        tools.add(removeButton);
        layout.add(tools);

        initWidget(layout);
    }
    
    @PostConstruct
    public void init() {
              
        userStore.addChangeHandler(
                new PropagatesChange.Handler() {
                    @Override
                    public void onChange(final Class<?> source, final Class<?> actionType) {
                        updateUserList();
                        removeButton.setEnabled(userStore.getSelectedUser()!=null);
                    }
                }
        );
    }

    private void updateUserList() {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(userStore.getUsers());
    }
}
