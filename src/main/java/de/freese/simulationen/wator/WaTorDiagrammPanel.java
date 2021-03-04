// Created: 09.10.2009
/**
 * 09.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import de.freese.simulationen.SimulationEnvironment;
import de.freese.simulationen.model.ISimulation;
import de.freese.simulationen.model.ISimulationListener;

/**
 * DiagrammPanel der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class WaTorDiagrammPanel extends JPanel implements ISimulationListener
{
    /**
     *
     */
    private static final long serialVersionUID = -7891438395009637657L;

    /**
        *
        */
    private int fishes;

    /**
    *
    */
    private ScheduledFuture<?> scheduledFuture;

    /**
        *
        */
    private int sharks;

    /**
     *
     */
    private TimeSeries timeSeriesFische = new TimeSeries("Fische");

    /**
    *
    */
    private TimeSeries timeSeriesHaie = new TimeSeries("Haie");

    /**
     * Erstellt ein neues {@link WaTorDiagrammPanel} Object.
     */
    public WaTorDiagrammPanel()
    {
        super();

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(getTimeSeriesFische());
        dataset.addSeries(getTimeSeriesHaie());

        Font font = new Font("Arial", Font.BOLD, 12);

        ValueAxis timeAxis = new DateAxis("Zeitachse");
        timeAxis.setLowerMargin(0.02D);
        timeAxis.setUpperMargin(0.02D);
        timeAxis.setAutoRange(true);
        timeAxis.setFixedAutoRange(60000D);
        timeAxis.setTickLabelsVisible(true);
        timeAxis.setTickLabelFont(font);
        timeAxis.setLabelFont(font);

        NumberAxis valueAxis = new NumberAxis("Anzahl");
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setTickLabelFont(font);
        valueAxis.setLabelFont(font);
        // valueAxis.setAutoRange(true);
        // valueAxis.setFixedAutoRange(10000D);
        // valueAxis.setAutoTickUnitSelection(true);
        // valueAxis.setRange(0.0D, 20000D);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(2.5F));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.5F));

        XYPlot xyplot = new XYPlot(dataset, timeAxis, valueAxis, renderer);

        JFreeChart chart = new JFreeChart(null, null, xyplot, true);
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(font);

        setLayout(new BorderLayout());
        add(new ChartPanel(chart), BorderLayout.CENTER);
    }

    /**
     * @see de.freese.simulationen.model.ISimulationListener#completed(de.freese.simulationen.model.ISimulation)
     */
    @Override
    public void completed(final ISimulation simulation)
    {
        WaTorWorld waTorWorld = (WaTorWorld) simulation;

        this.fishes = waTorWorld.getFishCounter();
        this.sharks = waTorWorld.getSharkCounter();
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    protected ScheduledExecutorService getScheduledExecutorService()
    {
        return SimulationEnvironment.getInstance().getScheduledExecutorService();
    }

    /**
     * @return {@link TimeSeries}
     */
    private TimeSeries getTimeSeriesFische()
    {
        return this.timeSeriesFische;
    }

    /**
     * @return {@link TimeSeries}
     */
    private TimeSeries getTimeSeriesHaie()
    {
        return this.timeSeriesHaie;
    }

    /**
     * Startet die Simulation.
     */
    protected void start()
    {
        this.scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(this::step, 0, 250, TimeUnit.MILLISECONDS);
    }

    /**
     * Aktualisiert die Simulation.
     */
    protected void step()
    {
        Runnable runnable = () -> {
            RegularTimePeriod timePeriod = new FixedMillisecond();

            getTimeSeriesFische().addOrUpdate(timePeriod, this.fishes);
            getTimeSeriesHaie().addOrUpdate(timePeriod, this.sharks);

            // getTimeSeriesFische().setDescription("" + fishes);
            // getTimeSeriesHaie().setDescription("" + sharks);
        };

        if (SwingUtilities.isEventDispatchThread())
        {
            runnable.run();
        }
        else
        {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Stoppt die Simulation.
     */
    protected void stop()
    {
        if ((this.scheduledFuture != null))
        {
            this.scheduledFuture.cancel(true);
            this.scheduledFuture = null;
        }
    }
}
