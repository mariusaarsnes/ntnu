package round_robin.graphics;

import round_robin.Process;

import javax.swing.*;
import java.awt.*;
import java.util.*;

class QueuePanel extends JPanel {
    public enum Direction {
        LEFT, RIGHT
    }

    private static Font font = new Font("Arial", Font.PLAIN, 12);

    private java.util.Queue<Process> data;
    /** The name of the queue */
    private String name;
    /** The maximum number of elements of the queue that will be visible in the GUI */
    private int maxVisibleLength;

    /** The direction in which the queue is drawn. */
    private Direction direction;

    QueuePanel(Queue<Process> data, String name, int maxVisibleLength, Direction direction) {
        this.data = data;
        this.name = name;
        this.maxVisibleLength = maxVisibleLength;
        this.direction = direction;
        setBackground(Color.white);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth() - 1;
        int h = getHeight() - 1;

        // Draw heading
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        String heading = "Items in " + name + ": " + data.size();
        g.setColor(Color.black);
        g.drawString(heading, w / 2 - fm.stringWidth(heading) / 2, 15);

        // Draw elements
        Iterator<Process> iter = data.iterator();
        for (int i = 0; i < maxVisibleLength; i++) {
            int x;
            int blockWidth = w / (maxVisibleLength + 1);
            if (direction == Direction.RIGHT) { x = w - (i + 1) * blockWidth; }
            else { x = i * blockWidth; }

            if (iter.hasNext()) {
                SimulationGui.drawProcess(iter.next(), g, x, 20, blockWidth, h - 20);
            } else {
                g.setColor(Color.black);
                g.drawRect(x, 20, blockWidth, h - 20);
            }
        }

        // Draw queue border.
        g.setColor(Color.red);
        if (direction == Direction.RIGHT) {
            g.drawLine(w, 20, w, h);
        } else {
            g.drawLine(0, 20, 0, h);
        }
        g.drawLine(0, 20, w, 20);
        g.drawLine(0, h, w, h);
    }
}