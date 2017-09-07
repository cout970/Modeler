package com.cout970.modeler.gui.dialogs;

import com.cout970.modeler.core.export.ImportFormat;
import com.cout970.modeler.core.export.ImportProperties;
import com.cout970.modeler.util.CollectionsKt;
import com.cout970.modeler.util.PopupsKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.swing.*;
import java.util.Arrays;

/**
 * Created by cout970 on 2017/01/02.
 */
public class ImportDialog {
    private JButton importButton;
    private JButton cancelButton;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JButton selectButton;
    private JPanel root;
    private JCheckBox flipUVCheckBox;

    private static final PointerBuffer extensions = CollectionsKt.toPointerBuffer(Arrays.asList("*.obj", "*.tcn", "*.json", "*.tbl"));

    @SuppressWarnings("unchecked")
    public static void show(Function1<ImportProperties, Unit> returnFun) {
        JDialog frame = new JDialog();
        ImportDialog dialog = new ImportDialog();
        dialog.comboBox1.addItem("Obj (*.obj)");
        dialog.comboBox1.addItem("Techne (*.tcn, *.zip)");
        dialog.comboBox1.addItem("Minecraft (*.json)");
        dialog.comboBox1.addItem("Tabula (*.tbl)");
        //buttons
        dialog.cancelButton.addActionListener(e -> {
            returnFun.invoke(null);
            frame.setVisible(false);
        });
        dialog.importButton.addActionListener(e -> {
            returnFun.invoke(new ImportProperties(dialog.textField1.getText(),
                    ImportFormat.values()[dialog.comboBox1.getSelectedIndex()],
                    dialog.flipUVCheckBox.isSelected()));

            frame.setVisible(false);
        });
        dialog.selectButton.addActionListener(e -> {
            frame.toBack();
            String file = TinyFileDialogs.tinyfd_openFileDialog("Import", "",
                    extensions, "Model Files (*.tcn, *.obj, *.json, *.tbl)",
                    false);
            if (file != null) {
                if (file.endsWith(".obj")) {
                    dialog.comboBox1.setSelectedIndex(0);
                } else if (file.endsWith(".zip") || file.endsWith(".tcn")) {
                    dialog.comboBox1.setSelectedIndex(1);
                } else if (file.endsWith(".json")) {
                    dialog.comboBox1.setSelectedIndex(2);
                } else if (file.endsWith(".tbl")) {
                    dialog.comboBox1.setSelectedIndex(3);
                }
            }
            dialog.textField1.setText(file);
            frame.toFront();
        });

        frame.setContentPane(dialog.root);
        frame.setIconImage(PopupsKt.getPopupImage());
        frame.pack();
        frame.setTitle("Import");
        PopupsKt.center(frame);
        frame.setVisible(true);
        frame.toFront();
    }
}
