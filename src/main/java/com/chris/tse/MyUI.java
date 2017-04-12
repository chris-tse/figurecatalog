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


        final Label title = new Label("Welcome!");
        title.setWidth(null);
        title.setPrimaryStyleName("main-title");

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

        TextField name = new TextField("Figure Name");
        name.setRequiredIndicatorVisible(true);


        TextField price = new TextField("Price (¥)");
        price.setRequiredIndicatorVisible(true);


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
            String itemPrice = price.getValue();
            FigureSize itemSize = FigureSize.valueOf(size.getValue().toUpperCase());
            boolean p = (priv.getValue().equals("Public") ? true : false);

            System.out.println("The item is called: " + itemName);
            System.out.println("The size is: " + itemSize);
            System.out.println("The price is: " + itemPrice);
            if (p) {
                db.add(new Figure(itemName, itemSize, itemPrice));
            } else {
                pdb.add(new Figure(itemName, itemSize, itemPrice));
            }
            new Notification ("Item added", "" + itemName + " with price of " + itemPrice + " of size " + size.getValue(), Notification.Type.TRAY_NOTIFICATION).show(Page.getCurrent());
        });

        back.addClickListener( e -> setContent(mainMenuHolder));


        addForm.addComponents(name, price, size, priv, add);
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


        final ArrayList<Figure> total = new ArrayList<Figure>(db);
        total.addAll(pdb);
        figures.setItems(total);

        //Grid.Column<Figure, String> nameColumn = figures.addColumn(Figure::getName).setCaption("Name");


        TextField nameEditor = new TextField();
        ComboBox<String> sizeEditor = new ComboBox<>();
        TextField priceEditor = new TextField();

        sizeEditor.setItems(sizes);

        Binder<Figure> binder = figures.getEditor().getBinder();

        Binding<Figure, String> nameBinding = binder.bind(
                nameEditor, Figure::getName, Figure::setName);

        Binding<Figure, String> sizeBinding = binder.bind(
                sizeEditor, Figure::getSize, Figure::setSize);

        Binding<Figure, String> priceBinding = binder.bind(
                priceEditor, Figure::getPrice, Figure::setPrice);

        figures.addColumn(Figure::getName).setEditorComponent(
                nameEditor, Figure::setName).setExpandRatio(1).setCaption("Name").setWidth(400).setEditorBinding(nameBinding);

        figures.addColumn(Figure::getPrice).setEditorComponent(
                priceEditor, Figure::setPrice).setExpandRatio(1).setCaption("Price (¥)").setWidth(200).setEditorBinding(priceBinding);

        figures.addColumn(Figure::getSize).setEditorComponent(
                sizeEditor, Figure::setSize).setExpandRatio(1).setCaption("Size").setWidth(200).setEditorBinding(sizeBinding);

        figures.addColumn(fig -> "Delete",
                new ButtonRenderer<>(clickEvent -> {
                    total.remove(clickEvent.getItem());
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
        addFormHolder.setHeight("100%");
        addFormHolder.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        final FormLayout searchForm = new FormLayout();
        addForm.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        addForm.setWidth(null);

        TextField searchName = new TextField("Name");
        searchName.setRequiredIndicatorVisible(false);


        ComboBox<String> searchPrice = new ComboBox("Price Range (¥)");
        List<String> searchPrices = Arrays.asList("Any", "0 - 2999", "3000 - 7999", "8000 - 19999", "20000 - 99999");
        searchPrice.setItems(searchPrices);
        searchPrice.setRequiredIndicatorVisible(false);
        searchPrice.setSelectedItem("Any");


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

        searchForm.addComponents(searchName, searchPrice, searchSize, searchPriv, searchBtn);
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
            db.add(new Figure("Sample Item 1", FigureSize.MEDIUM, "4999"));
            db.add(new Figure("Sample Item 2", FigureSize.LARGE, "2549"));
            db.add(new Figure("Sample Item 3", FigureSize.SMALL, "8999"));
        });

        searchBtn.addClickListener( e -> {
            res.clear();
            total.clear();
            total.addAll(db);
            total.addAll(pdb);



            String nameParam = searchName.getValue();
            String priceParam = searchPrice.getValue();
            String sizeParam = searchSize.getValue();
            String pubParam = searchPriv.getValue();


            int fsize = total.size();
            res.addAll(total);

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
                        i = 0;
                        fsize = res.size();
                    }
                }
            }

            if (!nameParam.isEmpty()) {
                for (int i = 0; i < fsize; i++) {
                    if (!res.get(i).getName().contains(nameParam)) {
                        res.remove(i);
                        i = 0;
                        fsize = res.size();
                    }
                }
            }

            if (!priceParam.equals("Any")) {
                String[] priceParams = searchPrice.getValue().split(" - ");
                int low = Integer.parseInt(priceParams[0]);
                int high = Integer.parseInt(priceParams[1]);
                for (int i = 0; i < fsize; i++) {
                    int currPrice = Integer.parseInt(res.get(i).getPrice());
                    if (!(low <= currPrice && currPrice <= high)) {
                        res.remove(i);
                        i = 0;
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
