package iesd.webservicejaxrs.views.masterdetail;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import iesd.asyncwebservices.models.QcData;
import iesd.asyncwebservices.models.QcDataList;
import iesd.jaxrs.proxy.QcDataRsProxy;
import iesd.webservicejaxrs.views.main.MainView;

@Route(value = "qcdata", layout = MainView.class)
@PageTitle("QcData")
@CssImport("styles/views/masterdetail/master-detail-view.css")
public class QcDataMasterDetailView extends Div implements AfterNavigationObserver {

    private QcDataRsProxy service;

    private Grid<QcData> employees;

    private TextField clientId = new TextField();
    private NumberField balance = new NumberField(); //NumberField is double
    private TextField tag = new TextField();
    private TextField email = new TextField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<QcData> binder;

    public QcDataMasterDetailView() {
        service = new QcDataRsProxy();
        setId("master-detail-view");
        // Configure Grid
        employees = new Grid<>();
        employees.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        employees.setHeightFull();
        employees.addColumn(QcData::getClientId).setHeader("ClientId");
        employees.addColumn(QcData::getEmail).setHeader("Email");
        employees.addColumn(QcData::getBalance).setHeader("Balance");
        employees.addColumn(QcData::getTag).setHeader("Tag");

        //when a row is selected or deselected, populate form
        employees.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        binder = new Binder<>(QcData.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(clientId)
                .withConverter(new StringToIntegerConverter(""))
                .bind(QcData::getClientId, QcData::setClientId);
        
        
        binder.forField(tag)
                .withConverter(new StringToIntegerConverter(""))
                .bind(QcData::getTag, QcData::setTag);
        
        binder.bindInstanceFields(this);

        // the grid valueChangeEvent will clear the form too
        cancel.addClickListener(e -> employees.asSingleSelect().clear());

        save.addClickListener(e -> {
            Notification.show("Not implemented");
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, clientId, "Client");
        addFormItem(editorDiv, formLayout, email, "Email");
        addFormItem(editorDiv, formLayout, balance, "Balance");
        addFormItem(editorDiv, formLayout, tag, "Tag");
        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, save);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(employees);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout,
            AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        QcDataList qcDataList = service.listAllQcData();
        
        employees.setItems(qcDataList.getQcDataList());
    }

    private void populateForm(QcData value) {
        // Value can be null as well, that clears the form
        binder.readBean(value);

        // The password field isn't bound through the binder, so handle that
        clientId.setValue("");
    }
}
