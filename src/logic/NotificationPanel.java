package logic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationPanel extends JPanel {
    private static final NotificationPanel INSTANCE = new NotificationPanel();
    private final JLabel messageLabel;

    private NotificationPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        setBackground(new Color(255, 50, 50)); // same as above, fully opaque
        add(messageLabel, BorderLayout.CENTER);
    }

    public static void show(JComponent parent, String message, int durationMillis) {
        INSTANCE.messageLabel.setText(message);

        Dimension size = INSTANCE.getPreferredSize();
        int width = 300;
        int height = 40;
        int x = parent.getWidth() - width - 20;
        int y = 20;

        INSTANCE.setBounds(x, y, width, height);
        INSTANCE.setVisible(true);

        parent.add(INSTANCE, JLayeredPane.POPUP_LAYER);
        parent.revalidate();
        parent.repaint();

        // Auto-hide timer
        new Timer().schedule(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    parent.remove(INSTANCE);
                    parent.revalidate();
                    parent.repaint();
                });
            }
        }, durationMillis);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape roundedRect = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20);
        g2.setColor(getBackground());
        g2.fill(roundedRect);
        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 40);
    }
}
