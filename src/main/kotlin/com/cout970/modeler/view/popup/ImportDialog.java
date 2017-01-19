package com.cout970.modeler.view.popup;

import com.cout970.modeler.export.ImportFormat;
import com.cout970.modeler.export.ImportProperties;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.swing.*;

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


    @SuppressWarnings("unchecked")
    public static void show(Function1<ImportProperties, Unit> returnFun) {
        JDialog frame = new JDialog();
        ImportDialog dialog = new ImportDialog();
        dialog.comboBox1.addItem("Obj (*.obj)");
        dialog.comboBox1.addItem("Techne (*.tcn, *.zip)");
        dialog.comboBox1.addItem("Minecraft (*.json)");
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
            String file = TinyFileDialogs.tinyfd_openFileDialog("Import", "",
                    PopupsKt.getImportFileExtensions(), "Model Files (*.tcn, *.obj, *.json)",
                    false);
            if (file.endsWith(".obj")) {
                dialog.comboBox1.setSelectedIndex(0);
            } else if (file.endsWith(".zip") || file.endsWith(".tcn")) {
                dialog.comboBox1.setSelectedIndex(1);
            } else if (file.endsWith(".json")) {
                dialog.comboBox1.setSelectedIndex(2);
            }
            dialog.textField1.setText(file);
        });

        frame.setContentPane(dialog.root);
        frame.setIconImage(PopupsKt.getPopupImage());
        frame.pack();
        PopupsKt.center(frame);
        frame.setVisible(true);
    }
}
