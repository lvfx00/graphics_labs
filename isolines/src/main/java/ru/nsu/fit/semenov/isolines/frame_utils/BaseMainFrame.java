package ru.nsu.fit.semenov.isolines.frame_utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;

public class BaseMainFrame extends BaseFrame {
    protected int iconSize = 24;
    protected JMenuBar menuBar;
    protected JToolBar toolBar;

    /**
     * Constructor where you can specify window size and title
     *
     * @param width  - horizontal size of newly created window
     * @param height - vertical size of newly created window
     * @param title  - window title
     */
    public BaseMainFrame(int width, int height, String title) {
        super(width, height, title);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Main toolbar");
        toolBar.setRollover(true);
        add(toolBar, BorderLayout.PAGE_START);
    }

    /**
     * Shortcut method to create menu item
     * Note that you have to insert it into proper place by yourself
     *
     * @param title    - menu item title
     * @param tooltip  - floating tooltip describing menu item
     * @param mnemonic - mnemonic key to activate item via keyboard
     * @param icon     - file name containing icon (must be located in 'resources' subpackage relative to your implementation of BaseMainFrame), can be null
     * @param runnable - Method which will be called when menu item is activated (method should not take any parameters)
     * @return created menu item
     */
    private JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon, Runnable runnable) {
        JMenuItem item = new JMenuItem(title);
        setupMenuItem(item, tooltip, mnemonic, icon, runnable, title);
        return item;
    }

    // same but with check box
    private JCheckBoxMenuItem createCheckBoxMenuItem(String title, String tooltip, int mnemonic, String icon, Runnable runnable) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(title);
        setupMenuItem(item, tooltip, mnemonic, icon, runnable, title);
        return item;
    }

    private void setupMenuItem(JMenuItem item, String tooltip, int mnemonic, String icon, Runnable runnable, String title) {
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);

        if (icon != null) {
            ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemClassLoader().getResource(icon), title);
            Image img = imageIcon.getImage();
            Image scaledImg = img.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            imageIcon.setImage(scaledImg);
            item.setIcon(imageIcon);
        }

        item.addActionListener(evt -> runnable.run());
    }

    /**
     * Creates submenu and returns it
     *
     * @param title    - submenu title
     * @param mnemonic - mnemonic key to activate submenu via keyboard
     * @return created submenu
     */
    public JMenu createSubMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    /**
     * Creates submenu and inserts it to the specified location
     *
     * @param title    - submenu title with full path (just submenu title for top-level submenus)
     *                 example: "File/New" - will create submenu "New" under menu "File" (provided that menu "File" was previously created)
     * @param mnemonic - mnemonic key to activate submenu via keyboard
     */
    public void addSubMenu(String title, int mnemonic) {
        MenuElement element = getParentMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        JMenu subMenu = createSubMenu(getMenuPathName(title), mnemonic);
        if (element instanceof JMenuBar)
            ((JMenuBar) element).add(subMenu);
        else if (element instanceof JMenu)
            ((JMenu) element).add(subMenu);
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).add(subMenu);
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }

    /**
     * Creates menu item and adds it to the specified menu location
     *
     * @param title    - menu item title with full path
     * @param tooltip  - floating tooltip describing menu item
     * @param mnemonic - mnemonic key to activate item via keyboard
     * @param icon     - file name containing icon (must be located in 'resources' subpackage relative to your implementation of BaseMainFrame), can be null
     * @param runnable - Method which will be called when menu item is activated (method should not take any parameters)
     * @throws InvalidParameterException - when specified menu location not found
     */
    public JMenuItem addMenuItem(String title, String tooltip, int mnemonic, String icon, Runnable runnable) {
        JMenuItem item = createMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, runnable);
        addMenuItem(item, tooltip, mnemonic, icon, runnable, title);
        return item;
    }

    // same but with checkbox
    public JCheckBoxMenuItem addCheckBoxMenuItem(String title, String tooltip, int mnemonic, String icon, Runnable runnable) {
        JCheckBoxMenuItem item = createCheckBoxMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, runnable);
        addMenuItem(item, tooltip, mnemonic, icon, runnable, title);
        return item;
    }

    private void addMenuItem(JMenuItem menuItem, String tooltip, int mnemonic, String icon, Runnable runnable, String title) {
        MenuElement element = getParentMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        if (element instanceof JMenu)
            ((JMenu) element).add(menuItem);
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).add(menuItem);
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }

    /**
     * Adds menu separator in specified menu location
     *
     * @param title - menu location
     * @throws InvalidParameterException - when specified menu location not found
     */
    public void addMenuSeparator(String title) {
        MenuElement element = getMenuElement(title);
        if (element == null)
            throw new InvalidParameterException("Menu path not found: " + title);
        if (element instanceof JMenu)
            ((JMenu) element).addSeparator();
        else if (element instanceof JPopupMenu)
            ((JPopupMenu) element).addSeparator();
        else
            throw new InvalidParameterException("Invalid menu path: " + title);
    }

    private String getMenuPathName(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0)
            return menuPath.substring(pos + 1);
        else
            return menuPath;
    }

    /**
     * Looks for menu element by menu path ignoring last path component
     *
     * @param menuPath - '/'-separated path to menu item (example: "Help/About...")
     * @return found menu item or null if no such item found
     */
    private MenuElement getParentMenuElement(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if (pos > 0)
            return getMenuElement(menuPath.substring(0, pos));
        else
            return menuBar;
    }

    /**
     * Looks for menu element by menu path
     *
     * @param menuPath - '/'-separated path to menu item (example: "Help/About...")
     * @return found menu item or null if no such item found
     */
    public MenuElement getMenuElement(String menuPath) {
        MenuElement element = menuBar;
        for (String pathElement : menuPath.split("/")) {
            MenuElement newElement = null;
            for (MenuElement subElement : element.getSubElements()) {
                if ((subElement instanceof JMenu && ((JMenu) subElement).getText().equals(pathElement))
                        || (subElement instanceof JMenuItem && ((JMenuItem) subElement).getText().equals(pathElement))) {
                    if (subElement.getSubElements().length == 1 && subElement.getSubElements()[0] instanceof JPopupMenu)
                        newElement = subElement.getSubElements()[0];
                    else
                        newElement = subElement;
                    break;
                }
            }
            if (newElement == null) return null;
            element = newElement;
        }
        return element;
    }

    /**
     * Creates toolbar button which will behave exactly like specified menuitem
     *
     * @param item - menuitem to create toolbar button from
     * @return created toolbar button
     */
    public JButton createToolBarButton(JMenuItem item) {
        JButton button = new JButton(item.getIcon());
        for (ActionListener listener : item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    // same but with toggle button
    public JToggleButton createToolBarToggleButton(JMenuItem item) {
        JToggleButton button = new JToggleButton(item.getIcon());
        for (ActionListener listener : item.getActionListeners())
            button.addActionListener(listener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    /**
     * Creates toolbar button which will behave exactly like specified menuitem
     *
     * @param menuPath - path to menu item to create toolbar button from
     * @return created toolbar button
     */
    public JButton createToolBarButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null)
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        return createToolBarButton(item);
    }

    // same but with toggle button
    public JToggleButton createToolBarToggleButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null)
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        return createToolBarToggleButton(item);

    }

    /**
     * Creates toolbar button which will behave exactly like specified menuitem and adds it to the toolbar
     *
     * @param menuPath - path to menu item to create toolbar button from
     */
    public JButton addToolBarButton(String menuPath) {
        JButton b = createToolBarButton(menuPath);
        toolBar.add(b);
        return b;
    }

    // same but with toggle button
    public JToggleButton addToolBarToggleButton(String menuPath) {
        JToggleButton b = createToolBarToggleButton(menuPath);
        toolBar.add(b);
        return b;
    }

    /**
     * Adds separator to the toolbar
     */
    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }
}
