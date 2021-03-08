// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import de.freese.simulationen.model.AbstractCell;
import de.freese.simulationen.model.Cell;

/**
 * Zelle der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public abstract class AbstractWatorCell extends AbstractCell<WaTorWorld> implements WatorCell
{
    /**
     *
     */
    private boolean edited;

    /**
     *
     */
    private int energy;

    // /**
    // *
    // */
    // private int[][] fischNachbarn;

    /**
    *
    */
    private final List<int[]> fischNachbarnList = new ArrayList<>(8);

    // /**
    // *
    // */
    // private int[][] freieNachbarn;

    /**
     *
     */
    private final List<int[]> freieNachbarnList = new ArrayList<>(8);

    /**
     * Erstellt ein neues {@link AbstractWatorCell} Object.
     *
     * @param world {@link WaTorWorld}
     * @param color {@link Color}
     */
    protected AbstractWatorCell(final WaTorWorld world, final Color color)
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
     * Es werden nur vertikale und horizontale Nachbarn berücksichtigt.
     */
    void ermittleNachbarn()
    {
        this.freieNachbarnList.clear();
        this.fischNachbarnList.clear();

        visitNeighbours((x, y) -> {
            Cell cell = getWorld().getCell(x, y);

            if (cell == null)
            {
                this.freieNachbarnList.add(new int[]
                {
                        x, y
                });
            }
            else if (cell instanceof FishCell)
            {
                this.fischNachbarnList.add(new int[]
                {
                        x, y
                });

                // Workaround: Verhindert falsche Koordinaten bei paralleler Verarbeitung.
                // Verhindert auch zusätzlich das Auftreten von Wellenfronten.
                ((FishCell) cell).setEdited(true);
            }
        });

        // int xWest = getWorld().getXTorusKoord(getX(), -1);
        // int xOst = getWorld().getXTorusKoord(getX(), +1);
        // int ySued = getWorld().getYTorusKoord(getY(), -1);
        // int yNord = getWorld().getYTorusKoord(getY(), +1);
        //
        // // freieNachbarn = new int[0][2];
        // // fischNachbarn = new int[0][2];
        //
        // int[][] cells = new int[4][2];
        //
        // // Nord
        // cells[0][0] = getX();
        // cells[0][1] = yNord;
        //
        // // Ost
        // cells[1][0] = xOst;
        // cells[1][1] = getY();
        //
        // // Süd
        // cells[2][0] = getX();
        // cells[2][1] = ySued;
        //
        // // West
        // cells[3][0] = xWest;
        // cells[3][1] = getY();
        //
        // for (int[] richtung : cells)
        // {
        // int x = richtung[0];
        // int y = richtung[1];
        //
        // Cell cell = getWorld().getCell(x, y);
        //
        // if (cell == null)
        // {
        // // freie = Arrays.copyOf(freie, freie.length + 1);
        // // freie[freie.length - 1] = richtung;
        //
        // this.freieNachbarnList.add(richtung);
        // }
        // else if (cell instanceof FishCell)
        // {
        // // fische = Arrays.copyOf(fische, fische.length + 1);
        // // fische[fische.length - 1] = richtung;
        //
        // this.fischNachbarnList.add(richtung);
        //
        // // Workaround: Verhindert falsche Koordinaten bei paralleler Verarbeitung.
        // // Verhindert auch zusätzlich das Auftreten von Wellenfronten.
        // ((FishCell) cell).setEdited(true);
        // }
        // }

        // this.freieNachbarn = this.freieNachbarnList.toArray(int[][]::new);
        // this.fischNachbarn = this.fischNachbarnList.toArray(int[][]::new);
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#getEnergy()
     */
    @Override
    public int getEnergy()
    {
        return this.energy;
    }

    /**
     * Liefert einen Fisch in der Nachbarschaft oder keinen.
     *
     * @return {@link FishCell}
     */
    protected FishCell getFischNachbar()
    {
        if (!hatFischNachbarn())
        {
            return null;
        }

        FishCell fishCell = null;

        // int size = this.fischNachbarn.length;
        // int[] koords = this.fischNachbarn[getWorld().getRandom().nextInt(size)];

        int size = this.fischNachbarnList.size();
        int[] koords = this.fischNachbarnList.get(getWorld().getRandom().nextInt(size));

        int x = koords[0];
        int y = koords[1];

        Cell cell = getWorld().getCell(x, y);

        if (cell instanceof FishCell)
        {
            fishCell = (FishCell) cell;
        }

        // while ((fishCell == null) && !this.fischNachbarnList.isEmpty())
        // {
        // // try
        // // {
        // final int size = this.fischNachbarnList.size();
        // int[] koords = this.fischNachbarnList.remove(getWorld().getRandom().nextInt(size));
        //
        // int x = koords[0];
        // int y = koords[1];
        //
        // Cell cell = getWorld().getCell(x, y);
        //
        // if (cell instanceof FishCell)
        // {
        // fishCell = (FishCell) cell;
        // }
        // // }
        // // catch (IndexOutOfBoundsException ex)
        // // {
        // // // Kommt vor, warum auch immer.
        // // }
        // }

        return fishCell;
    }

    /**
     * Liefert die Kooordinaten einer freien Zelle in der Nachbarschaft oder keine.
     *
     * @return int[]
     */
    protected int[] getFreierNachbar()
    {
        if (!hatFreieNachbarn())
        {
            return null;
        }

        int[] koords = null;

        // int size = this.freieNachbarn.length;
        // koords = this.freieNachbarn[getWorld().getRandom().nextInt(size)];

        int size = this.freieNachbarnList.size();
        koords = this.freieNachbarnList.get(getWorld().getRandom().nextInt(size));

        // while ((koords == null) && !this.freieNachbarnList.isEmpty())
        // {
        // // try
        // // {
        // int size = this.freieNachbarnList.size();
        // koords = this.freieNachbarnList.remove(getWorld().getRandom().nextInt(size));
        // // }
        // // catch (IndexOutOfBoundsException ex)
        // // {
        // // // Kommt vor, warum auch immer.
        // // }
        // }

        return koords;
    }

    /**
     * @return boolean
     */
    protected boolean hatFischNachbarn()
    {
        return !this.fischNachbarnList.isEmpty();
        // return this.fischNachbarn.length > 0;
    }

    /**
     * @return boolean
     */
    protected boolean hatFreieNachbarn()
    {
        return !this.freieNachbarnList.isEmpty();
        // return this.freieNachbarn.length > 0;
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

        // Die Diagonalen verursachen stehende Haie und andere Fehler ???

        // Nord
        biConsumer.accept(getX(), yNord);

        // Nord-Ost
        // biConsumer.accept(xOst, yNord);

        // Ost
        biConsumer.accept(xOst, getY());

        // Süd-Ost
        // biConsumer.accept(xOst, ySued);

        // Süd
        biConsumer.accept(getX(), ySued);

        // Süd-West
        // biConsumer.accept(xWest, ySued);

        // West
        biConsumer.accept(xWest, getY());

        // Nord-West
        // biConsumer.accept(xWest, yNord);
    }
}
