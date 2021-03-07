// Created: 07.03.2021
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.List;
import java.util.function.BiConsumer;
import de.freese.simulationen.model.AbstractCell;

/**
 * Zelle der WaTor-Simulation mit neuem Algorithmus.
 *
 * @author Thomas Freese
 */
public abstract class AbstractWatorCellNew extends AbstractCell<WaTorWorld> implements WatorCell
{
    /**
    *
    */
    private boolean edited;

    /**
    *
    */
    private int energy;

    /**
     * Erstellt ein neues {@link AbstractWatorCellNew} Object.
     *
     * @param world {@link WaTorWorld}
     * @param color {@link Color}
     */
    protected AbstractWatorCellNew(final WaTorWorld world, final Color color)
    {
        super(world, color);
    }

    /**
     * Erniedrigt den Energiewert um 1.
     */
    void decrementEnergy()
    {
        this.energy--;
    }

    /**
     * Ermittelt die Nachbarn dieser Zelle.<br>
     */
    public abstract void ermittleNachbarn();

    /**
     * @see de.freese.simulationen.wator.WatorCell#getEnergy()
     */
    @Override
    public int getEnergy()
    {
        return this.energy;
    }

    /**
     * @param freie {@link List}
     * @return Object
     */
    protected int[] getFreeCell(final List<int[]> freie)
    {
        // return freie.get(getWorld().getRandom().nextInt(freie.size()));

        int[] frei = null;

        while (true)
        {
            if (freie.size() == 1)
            {
                frei = freie.remove(0);
            }
            else if (freie.size() > 1)
            {
                int index = getWorld().getRandom().nextInt(freie.size());
                frei = freie.remove(index);
            }
            else
            {
                return null;
            }

            if (frei == null)
            {
                continue;
            }

            // Ist der neue Platz noch frei ?
            int freiX = frei[0];
            int freiY = frei[1];

            WatorCell watorCell = (WatorCell) getWorld().getCell(freiX, freiY);

            // if ((watorCell != null) && watorCell.isEdited())
            // {
            // continue;
            // }
            if ((watorCell != null))
            {
                continue;
            }

            return frei;
        }
    }

    /**
     * Erhoeht den Energiewert um 1.
     */
    void incrementEnergy()
    {
        this.energy++;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#isEdited()
     */
    @Override
    public boolean isEdited()
    {
        return this.edited;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#setEdited(boolean)
     */
    @Override
    public void setEdited(final boolean edited)
    {
        this.edited = edited;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#setEnergy(int)
     */
    @Override
    public void setEnergy(final int energy)
    {
        this.energy = energy;
    }

    /**
     * @param biConsumer {@link BiConsumer}
     */
    protected void visitNeighbours(final BiConsumer<Integer, Integer> biConsumer)
    {
        int xWest = getWorld().getXTorusKoord(getX(), -1);
        int xOst = getWorld().getXTorusKoord(getX(), +1);
        int ySued = getWorld().getYTorusKoord(getY(), -1);
        int yNord = getWorld().getYTorusKoord(getY(), +1);

        // Nord
        biConsumer.accept(getX(), yNord);

        // Nord-Ost
        biConsumer.accept(xOst, yNord);

        // Ost
        biConsumer.accept(xOst, getY());

        // Süd-Ost
        biConsumer.accept(xOst, ySued);

        // Süd
        biConsumer.accept(getX(), ySued);

        // Süd-West
        biConsumer.accept(xWest, ySued);

        // West
        biConsumer.accept(xWest, getY());

        // Nord-West
        biConsumer.accept(xWest, yNord);
    }
}
