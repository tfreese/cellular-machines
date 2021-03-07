// Created: 12.10.2016
package de.freese.simulationen.balls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import de.freese.simulationen.model.AbstractSimulation;

/**
 * Simulation springender Bälle.
 *
 * @author Thomas Freese
 */
public class BallSimulation extends AbstractSimulation
{
    /**
     * [%] = 10 %
     */
    private static final double DAEMPFUNG = 0.1D;

    /**
     * [m]
     */
    private static final int DURCHMESSER = 30;

    /**
    *
    */
    private final List<Ball> balls = new ArrayList<>();

    /**
    *
    */
    private final BufferedImage bufferedImage;

    /**
     * [ms]
     */
    private final int delay;

    /**
    *
    */
    private final int numberOfBalls;

    /**
     * Erzeugt eine neue Instanz von {@link BallSimulation}
     *
     * @param width int
     * @param height int
     * @param delay int [ms]
     */
    public BallSimulation(final int width, final int height, final int delay)
    {
        this(width, height, delay, 3);
    }

    /**
     * Erzeugt eine neue Instanz von {@link BallSimulation}
     *
     * @param width int
     * @param height int
     * @param delay int [ms]
     * @param numberOfBalls int
     */
    public BallSimulation(final int width, final int height, final int delay, final int numberOfBalls)
    {
        super(width, height);

        this.delay = delay;

        if (numberOfBalls < 1)
        {
            throw new IllegalArgumentException("numberOfBalls < 1");
        }

        this.numberOfBalls = numberOfBalls;

        this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        reset();
    }

    /**
     * Hinzufügen eines neuen Balls.
     *
     * @param x Aktuelle X-Koordinate [m].
     * @param y Aktuelle Y-Koordinate [m].
     * @param vx Horizontale Geschwindigkeit [m/s].
     * @param vy Vertikale Geschwindigkeit [m/s].
     */
    private void addBall(final double x, final double y, final double vx, final double vy)
    {
        Ball ball = new Ball(getWidth(), getHeight(), x, y, vx, vy, DURCHMESSER, DAEMPFUNG);

        if (!this.balls.contains(ball))
        {
            this.balls.add(ball);
        }
    }

    /**
     * @see de.freese.simulationen.model.Simulation#getImage()
     */
    @Override
    public BufferedImage getImage()
    {
        return this.bufferedImage;
    }

    /**
     * @param g {@link Graphics}
     */
    private void gitter(final Graphics g)
    {
        g.setColor(Color.BLACK);

        int stepX = getWidth() / 10;
        int stepY = getHeight() / 5;

        for (int i = stepX; i <= getWidth(); i += stepX)
        {
            g.drawLine(i, 0, i, getHeight());
        }

        for (int i = stepY; i <= getHeight(); i += stepY)
        {
            g.drawLine(0, i, getWidth(), i);
        }
    }

    /**
     * Liefert true, wenn alle Bälle zum Stillstand gekommen sind.
     *
     * @return boolean
     */
    public boolean isFinished()
    {
        return this.balls.stream().filter(b -> !b.isFinished()).count() == 0;
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // Bälle bewegen.
        this.balls.forEach(b -> b.move(this.delay));

        fireCompleted();

        // System.out.println("BallSimulation.nextGeneration()");
    }

    /**
     * @param g {@link Graphics}
     * @param ball {@link Ball}
     * @param color {@link Color}
     */
    private void paint(final Graphics g, final Ball ball, final Color color)
    {
        g.setColor(color);
        // g.translate(0, 0);

        // Koordinate umrechnen: 0,0 ist oben links.
        double x = ball.getX() - ball.getRadius();
        double y = ball.getMaxY() - (ball.getY() + ball.getRadius());

        int durchmesser = (int) ball.getDurchmesser();

        g.fillOval((int) x, (int) y, durchmesser, durchmesser);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#reset()
     */
    @Override
    public void reset()
    {
        this.balls.clear();

        for (int i = 0; i < this.numberOfBalls; i++)
        {
            int x = getRandom().nextInt(getWidth() - DURCHMESSER) + DURCHMESSER;
            int y = getRandom().nextInt(getHeight() - DURCHMESSER) + DURCHMESSER;
            int vx = getRandom().nextInt(160) + 20;
            int vy = getRandom().nextInt(160) + 20;

            if (getRandom().nextBoolean())
            {
                vx *= -1;
            }

            if (getRandom().nextBoolean())
            {
                vy *= -1;
            }

            addBall(x, y, vx, vy);
        }

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.model.AbstractSimulation#updateImage()
     */
    @Override
    protected void updateImage()
    {
        Graphics g = getImage().getGraphics();

        if (g instanceof Graphics2D)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            // g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }

        // g.setBackground(Color.WHITE);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        gitter(g);

        // An neuer Stelle malen.
        this.balls.forEach(b -> paint(g, b, Color.BLUE));
    }
}