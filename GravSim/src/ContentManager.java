import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

class ContentManager {

    private static File[] selectedFiles;
    private static JFrame frame;

    static void initialize(JFrame frame) {
        ContentManager.frame = frame;
    }

     static void registerDeleteFileAction(JFileChooser fileChooser) {
        AbstractAction a = new AbstractAction() {

            public void actionPerformed(ActionEvent ae) {
                try {
                    selectedFiles = fileChooser.getSelectedFiles();

                    // If some file is selected
                    if (selectedFiles != null) {
                        // If user confirms to delete
                        if (askConfirm() == JOptionPane.YES_OPTION) {
                            for (File f : selectedFiles) {
                                java.nio.file.Files.delete(f.toPath());
                            }
                            // Rescan the directory after deletion
                            fileChooser.rescanCurrentDirectory();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };

        // Get action map and map, "delAction" with a
        fileChooser.getActionMap().put("delAction", a);

        // Get input map when jf is in focused window and put a keystroke DELETE
        // associate the key stroke (DELETE) (here) with "delAction"
        fileChooser.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "delAction");
    }

    private static int askConfirm() {
        return JOptionPane.showConfirmDialog(frame, "Are you sure want to delete this file?", "Confirm", JOptionPane.YES_NO_OPTION);
    }
}
