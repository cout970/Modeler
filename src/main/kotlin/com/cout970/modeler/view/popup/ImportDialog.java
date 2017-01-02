package com.cout970.modeler.view.popup;

import com.cout970.modeler.export.ImportFormat;
import kotlin.Pair;
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


    @SuppressWarnings("unchecked")
    public static void show(Function1<Pair<String, ImportFormat>, Unit> returnFun) {
        JDialog frame = new JDialog();
        ImportDialog dialog = new ImportDialog();
        dialog.comboBox1.addItem("Obj (*.obj)");
        dialog.comboBox1.addItem("Techne (*.tcn, *.zip)");
        dialog.comboBox1.addItem("Minecraft (*.json)");
        //buttons
        dialog.cancelButton.addActionListener(e -> {
            returnFun.invoke(new Pair(null, ImportFormat.OBJ));
            frame.setVisible(false);
        });
        dialog.importButton.addActionListener(e -> {
            returnFun.invoke(new Pair(dialog.textField1.getText(),
                    ImportFormat.values()[dialog.comboBox1.getSelectedIndex()]));
            frame.setVisible(false);
        });
        dialog.selectButton.addActionListener(e -> {
            dialog.textField1.setText(TinyFileDialogs.tinyfd_openFileDialog("Import", "",
                    PopupsKt.getImportFileExtensions(), "Model Files (*.tcn, *.obj, *.json)",
                    false));
        });

        frame.setContentPane(dialog.root);
        frame.setIconImage(PopupsKt.getPopupImage());
        frame.pack();
        PopupsKt.center(frame);
        frame.setVisible(true);
    }
}
