/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.gwt.circuit.sample.todo.client.views;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.gwt.circuit.Dispatcher;
import org.jboss.gwt.circuit.PropagatesChange;
import org.jboss.gwt.circuit.sample.todo.client.actions.SaveTodo;
import org.jboss.gwt.circuit.sample.todo.client.actions.SelectUser;
import org.jboss.gwt.circuit.sample.todo.client.stores.TodoStore;
import org.jboss.gwt.circuit.sample.todo.client.stores.UserStore;
import org.jboss.gwt.circuit.sample.todo.shared.Todo;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class TodoView extends Composite {

    @Inject
    TodoStore todoStore;

    @Inject
    UserStore userStore;

    @Inject
    TodoStore store;

    @Inject
    Dispatcher dispatcher;

    // --------------------------------------

    private ListBox users;

    private String selectedUser;

    private final CellTable<Todo> table;

    private final ListDataProvider<Todo> dataProvider;

    public TodoView() {

        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "padding:20px;width:100%");

        users = new ListBox();
        users.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                dispatcher.dispatch(
                        new SelectUser(
                                users.getValue(users.getSelectedIndex())
                        )
                );
            }
        });

        layout.add(users);

        table = new CellTable<Todo>();
        table.getElement().setAttribute("style", "width:90%");
        table.setEmptyTableWidget(new HTML("No Todo items found!"));

        dataProvider = new ListDataProvider<Todo>();
        dataProvider.addDataDisplay(table);

        TextColumn<Todo> nameColumn = new TextColumn<Todo>() {
            @Override
            public String getValue(Todo object) {
                return object.getName();
            }
        };
        table.addColumn(nameColumn, "Todo");

        TextColumn<Todo> userColumn = new TextColumn<Todo>() {
            @Override
            public String getValue(Todo object) {
                return object.getUser();
            }
        };
        table.addColumn(userColumn, "User");

        layout.add(table);

        Button addButton = new Button("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dispatcher.dispatch(
                        new SaveTodo(
                                new Todo("New todo @ " + System.currentTimeMillis(),
                                        Todo.USER_ANY)
                        )
                );
            }
        });

        Button removeButton = new Button("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

            }
        });

        HorizontalPanel tools = new HorizontalPanel();
        tools.add(addButton);
        tools.add(removeButton);

        layout.add(tools);

        initWidget(layout);
    }


    @PostConstruct
    public void init() {

        todoStore.addChangeHandler(
                new PropagatesChange.Handler() {
                    @Override
                    public void onChange(Class<?> source) {
                        showTodos(todoStore.getTodos());
                    }
                }
        );

        userStore.addChangeHandler(
                new PropagatesChange.Handler() {
                    @Override
                    public void onChange(Class<?> source) {
                        updateUserList();
                    }
                }
        );

    }

    private void updateUserList() {
        users.clear();

        List<String> model = userStore.getUsers();
        String selection = userStore.getSelectedUser();

        int idx = -1;
        for(String user : model) {
            users.addItem(user);
            if(user.equals(selection))
                idx=users.getItemCount()-1;
        }

        if(idx!=-1)
            users.setSelectedIndex(idx);
    }

    void showTodos(final List<Todo> todos) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(todos);
        dataProvider.flush();
    }

}
