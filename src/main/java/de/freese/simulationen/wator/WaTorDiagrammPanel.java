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
import de.freese.simulationen.model.Simulation;
import de.freese.simulationen.model.SimulationListener;
import de.freese.simulationen.wator3.WatorRasterSimulation;

/**
 * DiagrammPanel der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class WaTorDiagrammPanel extends JPanel implements SimulationListener
{
    /**
     *
     */
    private static final long serialVersionUID = -7891438395009637657L;

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
        dataset.addSeries(this.timeSeriesFische);
        dataset.addSeries(this.timeSeriesHaie);

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
     * @see de.freese.simulationen.model.SimulationListener#completed(de.freese.simulationen.model.Simulation)
     */
    @Override
    public void completed(final Simulation simulation)
    {
        Runnable runnable = () -> {
            WatorRasterSimulation watorRasterSimulation = (WatorRasterSimulation) simulation;

            int[] fischeUndHaie = watorRasterSimulation.countFishesAndSharks();

            update(fischeUndHaie[0], fischeUndHaie[1]);
        };

        getScheduledExecutorService().execute(runnable);
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    private ScheduledExecutorService getScheduledExecutorService()
    {
        return SimulationEnvironment.getInstance().getScheduledExecutorService();
    }

    /**
     * Aktualisiert das Diagramm.
     *
     * @param fishes int
     * @param sharks int
     */
    protected void update(final int fishes, final int sharks)
    {
        Runnable runnable = () -> {
            RegularTimePeriod timePeriod = new FixedMillisecond();

            this.timeSeriesFische.addOrUpdate(timePeriod, fishes);
            this.timeSeriesHaie.addOrUpdate(timePeriod, sharks);

            // this.timeSeriesFische.setDescription("" + fishes);
            // this.timeSeriesHaie.setDescription("" + sharks);
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
}
