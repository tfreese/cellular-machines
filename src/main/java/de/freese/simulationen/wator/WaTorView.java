// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.wator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import de.freese.simulationen.AbstractSimulationView;
import de.freese.simulationen.SimulationCanvas;

/**
 * View fuer die WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class WaTorView extends AbstractSimulationView<WaTorWorld>
{
    /**
     *
     */
    private final WaTorDiagrammPanel diagrammPanel;

    /**
     * Erstellt ein neues {@link WaTorView} Object.
     */
    public WaTorView()
    {
        this(null);
    }

    /**
     * Erstellt ein neues {@link WaTorView} Object.
     *
     * @param diagrammPanel {@link WaTorDiagrammPanel}
     */
    public WaTorView(final WaTorDiagrammPanel diagrammPanel)
    {
        super();

        this.diagrammPanel = diagrammPanel;
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#createModel(int, int)
     */
    @Override
    protected WaTorWorld createModel(final int fieldWidth, final int fieldHeight)
    {
        return new WaTorWorld(fieldWidth, fieldHeight);
    }

    /**
     * @param title String
     * @param value int
     * @param titleColor {@link Color}
     * @return {@link JSlider}
     */
    private JSlider createSlider(final String title, final int value, final Color titleColor)
    {
        JSlider slider = new JSlider(SwingConstants.VERTICAL, 0, 20, value);
        TitledBorder border = new TitledBorder(title);
        border.setTitleColor(titleColor);
        slider.setBorder(border);
        slider.setPaintLabels(true);
        slider.setPaintTrack(true);
        slider.setPaintTicks(true);
        // slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);

        return slider;
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#initialize(int, int)
     */
    @Override
    public void initialize(final int fieldWidth, final int fieldHeight)
    {
        super.initialize(fieldWidth, fieldHeight);

        SimulationCanvas canvas = new SimulationCanvas(getSimulation());
        getMainPanel().add(canvas, BorderLayout.CENTER);

        // Slider für Settings
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridLayout(3, 1));

        // Startenergie
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.setBorder(new TitledBorder("Startenergie -> Reset"));

        JSlider slider = createSlider("Fische", getSimulation().getFishStartEnergy(), Color.GREEN);
        slider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setFishStartEnergy(value);
            }
        });
        panel.add(slider);

        slider = createSlider("Haie", getSimulation().getSharkStartEnergy(), Color.BLUE);
        slider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setSharkStartEnergy(value);
            }
        });
        panel.add(slider);

        sliderPanel.add(panel);

        // Brutenergie
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.setBorder(new TitledBorder("Brutenergie"));

        slider = createSlider("Fische", getSimulation().getFishBreedEnergy(), Color.GREEN);
        slider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setFishBreedEnergy(value);
            }
        });
        panel.add(slider);

        slider = createSlider("Haie", getSimulation().getSharkBreedEnergy(), Color.BLUE);
        slider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setSharkBreedEnergy(value);
            }
        });
        panel.add(slider);

        sliderPanel.add(panel);

        // Sterbenergie
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.setBorder(new TitledBorder("Sterbenergie"));

        panel.add(Box.createGlue());

        slider = createSlider("Haie", getSimulation().getSharkStarveEnergy(), Color.BLUE);
        slider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();

            if (!source.getValueIsAdjusting())
            {
                int value = source.getValue();

                getSimulation().setSharkStarveEnergy(value);
            }
        });
        panel.add(slider);

        sliderPanel.add(panel);

        getControlPanel().add(sliderPanel, BorderLayout.CENTER);
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#start()
     */
    @Override
    protected void start()
    {
        super.start();

        if (this.diagrammPanel != null)
        {
            this.diagrammPanel.start();
        }
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#step()
     */
    @Override
    protected void step()
    {
        super.step();

        if (this.diagrammPanel != null)
        {
            this.diagrammPanel.step();
        }
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#stop()
     */
    @Override
    protected void stop()
    {
        super.stop();

        if (this.diagrammPanel != null)
        {
            this.diagrammPanel.stop();
        }
    }
}
