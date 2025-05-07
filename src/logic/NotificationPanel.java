package logic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationPanel extends JPanel {

    public NotificationPanel(String message,String colour) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        if(colour.equals("red")) setBackground(new Color(255, 50, 50));
        if(colour.equals("green")) setBackground(new Color(0, 119, 0));
        add(messageLabel, BorderLayout.CENTER);
    }

    public void showOn(JLayeredPane parent, int durationMillis) {
        int width = 300;
        int height = 40;
        int x = parent.getWidth() - width - 20;
        int y = 20;

        setBounds(x, y, width, height);
        setVisible(true);

        parent.add(this, JLayeredPane.POPUP_LAYER);
        parent.revalidate();
        parent.repaint();

        // Auto-hide
        new Timer().schedule(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    parent.remove(NotificationPanel.this);
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

    // 🔥 Cleaner API usage
    public static void show(JComponent parent, String message, int durationMillis, String colour) {
        NotificationPanel panel = new NotificationPanel(message,colour);
        panel.showOn((JLayeredPane) parent, durationMillis);
    }
}
