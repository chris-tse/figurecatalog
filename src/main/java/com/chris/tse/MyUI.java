package com.chris.tse;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ButtonRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    public static ArrayList<Figure> db = new ArrayList<>();
    public static int itemID = 0;

    @Override
    protected void init(VaadinRequest vaadinRequest) {


        ArrayList<Figure> pdb = new ArrayList<>();
        ArrayList<Figure> res = new ArrayList<>();
        List<String> privs = Arrays.asList("Public", "Private");


        // **************************
        // Main Menu
        // **************************
        final VerticalLayout mainMenuHolder = new VerticalLayout();
        mainMenuHolder.setHeight("100%");
        mainMenuHolder.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        VerticalLayout mainMenu = new VerticalLayout();
        mainMenu.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


        final Label title = new Label("<h1 style='font-size:50px;margin-bottom:10px'>Welcome!</h1>");
        title.setContentMode(ContentMode.HTML);
        title.setWidth(null);

        final Button goCatalog = new Button("See Full Catalog");
        final Button goSearch = new Button("Search Catalog");
        final Button goAdd = new Button("Add an Item");
        final Button addSample = new Button("Add Sample Items");


        mainMenu.addComponents(title, goCatalog, goSearch, goAdd, addSample);
        mainMenuHolder.addComponents(mainMenu);


        // **************************
        // Add Form
        // **************************
        final VerticalLayout addFormHolder = new VerticalLayout();
        addFormHolder.setHeight("100%");
        addFormHolder.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        final FormLayout addForm = new FormLayout();
        addForm.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        addForm.setWidth(null);

        Label addLabel = new Label("<h2 style='font-size:30px;margin-bottom:10px'>Add an Item</h2>");
        addLabel.setContentMode(ContentMode.HTML);

        TextField name = new TextField("Figure Name");
        name.setRequiredIndicatorVisible(true);


        TextField manufacturer = new TextField("Manufacturer");
        manufacturer.setRequiredIndicatorVisible(true);


        ComboBox<String> size = new ComboBox<>("Select a size");
        List<String> sizes = Arrays.asList("Small", "Medium", "Large");
        size.setItems(sizes);
        size.setRequiredIndicatorVisible(true);

        ComboBox<String> priv = new ComboBox<>("Public or Private");
        priv.setItems(privs);
        priv.setRequiredIndicatorVisible(false);


        Button add = new Button("Add Item");
        Button back = new Button("Back to Menu");

        add.addClickListener( e -> {
            String itemName = name.getValue();
            String itemMan = manufacturer.getValue();
            FigureSize itemSize = FigureSize.valueOf(size.getValue().toUpperCase());
            boolean p = (priv.getValue().equals("Public") ? true : false);

            System.out.println("The item is called: " + itemName);
            System.out.println("The size is: " + itemSize);
            System.out.println("The manufacturer is: " + itemMan);
            if (p) {
                db.add(new Figure(itemName, itemSize, itemMan, p));
            } else {
                pdb.add(new Figure(itemName, itemSize, itemMan, p));
            }
            new Notification ("Item added", "" + itemName + " made by " + itemMan + " of size " + size.getValue(), Notification.Type.TRAY_NOTIFICATION).show(Page.getCurrent());
        });

        back.addClickListener( e -> setContent(mainMenuHolder));


        addForm.addComponents(addLabel, name, manufacturer, size, priv, add);
        addFormHolder.addComponent(addForm);



        // **************************
        // Catalog Screen
        // **************************
        final VerticalLayout tableHolder = new VerticalLayout();
        tableHolder.setHeight("100%");
        tableHolder.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        HorizontalLayout tabletop = new HorizontalLayout();
        tabletop.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);



        ComboBox<String> pselect = new ComboBox<>();
        pselect.setItems(privs);


        Grid<Figure> figures = new Grid<>();
        figures.setWidth("1000px");


        final ArrayList<Figure> total = new ArrayList<>(db);
        total.addAll(pdb);
        figures.setItems(total);

        TextField nameEditor = new TextField();
        ComboBox<String> sizeEditor = new ComboBox<>();
        TextField manEditor = new TextField();

        sizeEditor.setItems(sizes);

        Binder<Figure> binder = figures.getEditor().getBinder();

        Binding<Figure, String> nameBinding = binder.bind(
                nameEditor, Figure::getName, Figure::setName);

        Binding<Figure, String> sizeBinding = binder.bind(
                sizeEditor, Figure::getSize, Figure::setSize);

        Binding<Figure, String> manBinding = binder.bind(
                manEditor, Figure::getManufacturer, Figure::setManufacturer);

        figures.addColumn(Figure::getName).setEditorComponent(
                nameEditor, Figure::setName).setExpandRatio(1).setCaption("Name").setWidth(400).setEditorBinding(nameBinding);

        figures.addColumn(Figure::getManufacturer).setEditorComponent(
                manEditor, Figure::setManufacturer).setExpandRatio(1).setCaption("Manufacturer").setWidth(200).setEditorBinding(manBinding);

        figures.addColumn(Figure::getSize).setEditorComponent(
                sizeEditor, Figure::setSize).setExpandRatio(1).setCaption("Size").setWidth(200).setEditorBinding(sizeBinding);

        figures.addColumn(fig -> "Delete",
                new ButtonRenderer<>(clickEvent -> {
                    if (clickEvent.getItem().isPub()) {
                        db.remove(clickEvent.getItem());
                    } else {
                        pdb.remove(clickEvent.getItem());
                    }
                    total.clear();
                    total.addAll(db);
                    total.addAll(pdb);
                    figures.setItems(total);
                })).setWidth(200);


        TextField filterField = new TextField();
        filterField.setCaption("Filter");
        filterField.setWidth("100px");

        figures.getEditor().setEnabled(true);
        tableHolder.addComponents(figures);


        // **************************
        // Search Form
        // **************************
        final VerticalLayout searchFormHolder = new VerticalLayout();
        searchFormHolder.setHeight("100%");
        searchFormHolder.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        final FormLayout searchForm = new FormLayout();
        searchForm.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        searchForm.setWidth(null);

        Label searchLabel = new Label("<h2 style='font-size:30px;margin-bottom:10px'>Search Catalog</h2>");
        searchLabel.setContentMode(ContentMode.HTML);

        TextField searchName = new TextField("Name");
        searchName.setRequiredIndicatorVisible(false);


        TextField searchMan = new TextField("Manufacturer");
        searchName.setRequiredIndicatorVisible(false);


        ComboBox<String> searchSize = new ComboBox<>("Select a size");
        List<String> searchSizes = Arrays.asList("Any", "Small", "Medium", "Large");
        searchSize.setItems(searchSizes);
        searchSize.setEmptySelectionAllowed(false);
        searchSize.setSelectedItem("Any");

        ComboBox<String> searchPriv = new ComboBox<>("Public or Private");
        List<String> searchPrivs = Arrays.asList("Any", "Public", "Private");
        searchPriv.setItems(searchPrivs);
        searchPriv.setEmptySelectionAllowed(false);
        searchPriv.setSelectedItem("Any");

        Button searchBtn = new Button("Search");

        searchForm.addComponents(searchLabel, searchName, searchMan, searchSize, searchPriv, searchBtn);
        searchFormHolder.addComponent(searchForm);


        // *********************
        // Main menu listener
        // *********************
        goCatalog.addClickListener( e -> {
            addForm.removeComponent(back);
            searchForm.removeComponent(back);
            tableHolder.addComponent(back);
            figures.getEditor().setEnabled(true);
            total.clear();
            total.addAll(db);
            total.addAll(pdb);
            figures.setItems(total);
            setContent(tableHolder);

        });

        goSearch.addClickListener( e -> {
            addForm.removeComponent(back);
            tableHolder.removeComponent(back);
            searchForm.addComponent(back);
            setContent(searchFormHolder);
        });

        goAdd.addClickListener( e -> {
            tableHolder.removeComponent(back);
            searchForm.removeComponent(back);
            addForm.addComponent(back);
            setContent(addFormHolder);
        });

        addSample.addClickListener( e -> {
            db.add(new Figure("Sample Item 1", FigureSize.MEDIUM, "Good Smile Company", true));
            db.add(new Figure("Sample Item 2", FigureSize.LARGE, "Banpresto", true));
            db.add(new Figure("Sample Item 3", FigureSize.SMALL, "Kotobukiya", true));
        });

        searchBtn.addClickListener( e -> {
            res.clear();
            total.clear();
            total.addAll(db);
            total.addAll(pdb);



            String nameParam = searchName.getValue();
            String manParam = searchMan.getValue();
            String sizeParam = searchSize.getValue();
            String pubParam = searchPriv.getValue();
            res.addAll(total);
            int fsize = res.size();

            if (pubParam.equals("Public")) {
                res.clear();
                res.addAll(db);
                fsize = db.size();
            }

            if (pubParam.equals("Private")){
                res.clear();
                res.addAll(pdb);
                fsize = pdb.size();
            }

            if (!sizeParam.equals("Any")) {
                for (int i = 0; i < fsize; i++) {
                    String currSize = res.get(i).getSize().toUpperCase();
                    if (!currSize.equals(sizeParam.toUpperCase())) {
                        res.remove(i);
                        i = -1;
                        fsize = res.size();
                    }
                }
            }

            if (!nameParam.isEmpty()) {
                for (int i = 0; i < fsize; i++) {
                    String currName = res.get(i).getName().toLowerCase();
                    if (!currName.contains(nameParam.toLowerCase())) {
                        res.remove(i);
                        i = -1;
                        fsize = res.size();
                    }
                }
            }

            if (!manParam.isEmpty()) {
                for (int i = 0; i < fsize; i++) {
                    String currMan = res.get(i).getManufacturer().toLowerCase();
                    if (!currMan.contains(manParam.toLowerCase())) {
                        res.remove(i);
                        i = -1;
                        fsize = res.size();
                    }
                }
            }



            figures.setItems(res);
            figures.getEditor().setEnabled(false);
            addForm.removeComponent(back);
            searchForm.removeComponent(back);
            tableHolder.addComponent(back);

            setContent(tableHolder);

        });

        setContent(mainMenuHolder);

    }



    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
