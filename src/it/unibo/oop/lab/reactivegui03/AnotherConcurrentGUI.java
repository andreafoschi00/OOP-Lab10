package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final long TIME_TO_WAIT = TimeUnit.SECONDS.toSeconds(10);
    private final JLabel lbl = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");
    private final CounterAgent cAgent = new CounterAgent();
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(lbl);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        up.addActionListener(e -> cAgent.countUp());
        down.addActionListener(e -> cAgent.countDown());
        stop.addActionListener(e -> {
            cAgent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
        new Thread(cAgent).start();
        new Thread(() -> {
            try {
                Thread.sleep(TIME_TO_WAIT);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            this.stopCounting();
        }).start();
    }
    private void stopCounting() {
        cAgent.stopCounting();
        SwingUtilities.invokeLater(() -> {
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
    }
    private class CounterAgent implements Runnable {
        private volatile boolean stop;
        private volatile boolean up = true;
        private volatile int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(() -> lbl.setText(Integer.toString(counter)));
                    counter += up ? 1 : -1;
                    Thread.sleep(100);
                } catch (InterruptedException | InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }
        public void stopCounting() {
            this.stop = true;
        }
        public void countUp() {
            this.up = true;
        }
        public void countDown() {
            this.up = false;
        }
    }
}
