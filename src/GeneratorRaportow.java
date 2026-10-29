import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class GeneratorRaportow extends JFrame {

    private JTextArea poleRaportu;
    private JButton przyciskGeneruj;
    private JProgressBar pasekPostepu;

    public GeneratorRaportow() {
        setTitle("INF.04 - Generator Raportów");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        poleRaportu = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(poleRaportu);
        add(scrollPane, BorderLayout.CENTER);

        przyciskGeneruj = new JButton("Generuj Raport w Tle");
        pasekPostepu = new JProgressBar(0, 100);
        pasekPostepu.setValue(0);
        pasekPostepu.setStringPainted(true);
        pasekPostepu.setVisible(false);

        JPanel panelDolny = new JPanel(new BorderLayout());
        panelDolny.add(przyciskGeneruj, BorderLayout.NORTH);
        panelDolny.add(pasekPostepu, BorderLayout.SOUTH);
        add(panelDolny, BorderLayout.SOUTH);

        JMenuBar pasekMenu = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");

        JMenuItem itemZapisz = new JMenuItem("Zapisz Raport");
        itemZapisz.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
        ));
        itemZapisz.addActionListener(_ -> zapiszRaport());

        JMenuItem itemWyjscie = new JMenuItem("Wyjście");
        itemWyjscie.addActionListener(_ -> System.exit(0));

        menuPlik.add(itemZapisz);
        menuPlik.addSeparator();
        menuPlik.add(itemWyjscie);
        pasekMenu.add(menuPlik);
        setJMenuBar(pasekMenu);

        przyciskGeneruj.addActionListener(_ -> generujRaport());
    }

    private void generujRaport() {
        przyciskGeneruj.setEnabled(false);
        String raport = poleRaportu.getText();
        poleRaportu.setText("");
        pasekPostepu.setValue(0);
        pasekPostepu.setVisible(true);

        SwingWorker<Void, String> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(300);
                    publish("Wygenerowano wiersz " + i + "\n");
                    setProgress(i * 10);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String wiersz : chunks) {
                    poleRaportu.append(wiersz);
                }
            }

            @Override
            protected void done() {
                pasekPostepu.setValue(100);
                przyciskGeneruj.setEnabled(true);
                poleRaportu.setText(raport);
                JOptionPane.showMessageDialog(
                        GeneratorRaportow.this,
                        "Raport został wygenerowany!"
                );
            }
        };

        worker.addPropertyChangeListener(e -> {
            if ("progress".equals(e.getPropertyName())) {
                pasekPostepu.setValue((Integer) e.getNewValue());
            }
        });

        worker.execute();
    }

    private void zapiszRaport() {
        if (poleRaportu.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Brak tekstu do zapisania!",
                    "Ostrzeżenie",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz Raport");
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt")
        );

        int wybor = fileChooser.showSaveDialog(this);

        if (wybor == JFileChooser.APPROVE_OPTION) {
            File plik = fileChooser.getSelectedFile();

            if (!plik.getName().toLowerCase().endsWith(".txt")) {
                plik = new File(plik.getAbsolutePath() + ".txt");
            }

            try {
                Files.writeString(plik.toPath(), poleRaportu.getText());
                JOptionPane.showMessageDialog(this, "Raport zapisany!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Błąd zapisu pliku: " + e.getMessage(),
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GeneratorRaportow okno = new GeneratorRaportow();
            okno.setVisible(true);
        });
    }
}
