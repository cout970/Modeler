package com.cout970.modeler.view.popup;

import com.cout970.modeler.export.ExportFormat;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.swing.*;

/**
 * Created by cout970 on 2017/01/02.
 */
public class ExportDialog {
    private JButton exportButton;
    private JButton cancelButton;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JButton selectButton;
    private JPanel root;

    @SuppressWarnings("unchecked")
    public static void show(Function1<Pair<String, ExportFormat>, Unit> returnFun) {
        JDialog frame = new JDialog();
        ExportDialog dialog = new ExportDialog();
        dialog.comboBox1.addItem("Obj (*.obj)");
        //buttons
        dialog.cancelButton.addActionListener(e -> {
            returnFun.invoke(new Pair(null, ExportFormat.OBJ));
            frame.setVisible(false);
        });
        dialog.exportButton.addActionListener(e -> {
            returnFun.invoke(new Pair(dialog.textField1.getText(),
                    ExportFormat.values()[dialog.comboBox1.getSelectedIndex()]));
            frame.setVisible(false);
        });
        dialog.selectButton.addActionListener(e -> {
            dialog.textField1.setText(TinyFileDialogs.tinyfd_saveFileDialog("Export", "",
                    PopupsKt.getExportFileExtensions(ExportFormat.values()[dialog.comboBox1.getSelectedIndex()]),
                    (String) dialog.comboBox1.getSelectedItem()));
        });

        frame.setContentPane(dialog.root);
        frame.setIconImage(PopupsKt.getPopupImage());
        frame.pack();
        PopupsKt.center(frame);
        frame.setVisible(true);
    }
}
