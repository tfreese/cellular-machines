// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import de.freese.simulationen.model.ISimulation;
import de.freese.simulationen.model.ISimulationListener;

/**
 * Zeichenfläche für die Simulationen.
 *
 * @author Thomas Freese
 */
public class SimulationCanvas extends JComponent implements ISimulationListener
{
    /**
     *
     */
    private static final long serialVersionUID = 4896850562260701814L;

    /**
     * @param source {@link BufferedImage}
     * @return {@link BufferedImage}
     */
    public static BufferedImage copyImage(final BufferedImage source)
    {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();

        return b;
    }

    /**
     *
     */
    private Image image;

    /**
     *
     */
    private final boolean useVolatileImage;

    /**
     *
     */
    private VolatileImage volatileImage;

    /**
     * Erstellt ein neues {@link SimulationCanvas} Object.
     *
     * @param simulation {@link ISimulation}
     */
    public SimulationCanvas(final ISimulation simulation)
    {
        // Die Größe der Simulation auf die Angzeigegröße skalieren.
        this(simulation, (int) (800 * (double) simulation.getWidth()) / simulation.getHeight(), 800);
    }

    /**
     * Erstellt ein neues {@link SimulationCanvas} Object.
     *
     * @param simulation {@link ISimulation}
     * @param width int
     * @param height int
     */
    public SimulationCanvas(final ISimulation simulation, final int width, final int height)
    {
        super();

        setPreferredSize(new Dimension(width, height));

        this.useVolatileImage = Boolean.parseBoolean(SimulationGUI.PROPERTIES.getProperty("simulation.use.volatileImage", "false"));

        completed(simulation);

        simulation.addWorldListener(this);
        // setDoubleBuffered(true);
    }

    /**
     * @see de.freese.simulationen.model.ISimulationListener#completed(de.freese.simulationen.model.ISimulation)
     */
    @Override
    public void completed(final ISimulation simulation)
    {
        this.image = simulation.getImage();

        if (SwingUtilities.isEventDispatchThread())
        {
            repaint();
        }
        else
        {
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    /**
     * BackBuffer, erzeugt lazy das {@link VolatileImage} wenn noetig.
     *
     * @return {@link VolatileImage}
     */
    private VolatileImage createVolatileImage()
    {
        VolatileImage vi = null;

        // GraphicsConfiguration gc = getGraphicsConfiguration();
        // vi = gc.createCompatibleVolatileImage(getWidth(), getHeight());

        vi = createVolatileImage(getPreferredSize().width, getPreferredSize().height);

        return vi;
    }

    /**
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        int x = 0;
        int y = 0;

        if (!this.useVolatileImage)
        {
            g.drawImage(this.image, x, y, getWidth(), getHeight(), null); // this

            return;
        }

        // Main rendering loop. Volatile images may lose their contents.
        // This loop will continually render to (and produce if neccessary) volatile images
        // until the rendering was completed successfully.
        if (this.volatileImage == null)
        {
            this.volatileImage = createVolatileImage();
        }

        do
        {
            // Validate the volatile image for the graphics configuration of this
            // component. If the volatile image doesn't apply for this graphics configuration
            // (in other words, the hardware acceleration doesn't apply for the new device)
            // then we need to re-create it.
            GraphicsConfiguration gc = getGraphicsConfiguration();

            // This means the device doesn't match up to this hardware accelerated image.
            if (this.volatileImage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE)
            {
                this.volatileImage = null;
                // createBackBuffer(); // recreate the hardware accelerated image.

                g.drawImage(this.image, x, y, getWidth(), getHeight(), null);

                return;
            }

            Graphics offscreenGraphics = this.volatileImage.getGraphics();
            offscreenGraphics.drawImage(this.image, x, y, getWidth(), getHeight(), null);

            // paint back buffer to main graphics
            g.drawImage(this.volatileImage, x, y, getWidth(), getHeight(), this);
            // g.dispose();
        }
        while (this.volatileImage.contentsLost()); // Test if content is lost

        g.dispose();
    }
}
