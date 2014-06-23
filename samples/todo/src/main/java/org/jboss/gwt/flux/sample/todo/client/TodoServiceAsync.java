package org.jboss.gwt.flux.sample.todo.client;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.gwt.flux.sample.todo.shared.Todo;

public interface TodoServiceAsync {

    void list(final AsyncCallback<Collection<Todo>> async);

    void save(Todo todo, final AsyncCallback<Void> async);

    void delete(Todo todo, final AsyncCallback<Void> async);
}
