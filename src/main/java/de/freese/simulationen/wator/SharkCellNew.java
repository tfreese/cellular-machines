// Created: 07.03.2021
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import de.freese.simulationen.model.Cell;

/**
 * Hai-Zelle der WaTor-Simulation mit neuem Algorithmus.
 *
 * @author Thomas Freese
 */
public class SharkCellNew extends AbstractWatorCellNew implements SharkCell
{
    /**
    *
    */
    private List<int[]> fische = new ArrayList<>();

    /**
    *
    */
    private List<int[]> freie = new ArrayList<>();

    /**
     * Erstellt ein neues {@link SharkCellNew} Object.
     *
     * @param world {@link WaTorWorld}
     */
    public SharkCellNew(final WaTorWorld world)
    {
        super(world, Color.BLUE);
    }

    /**
     * @see de.freese.simulationen.wator.AbstractWatorCellNew#ermittleNachbarn()
     */
    @Override
    public void ermittleNachbarn()
    {
        this.fische.clear();
        this.freie.clear();

        // visitNeighbours((x, y) -> {
        // Cell cell = getWorld().getCell(x, y);
        //
        // if (cell instanceof FishCell)
        // {
        // this.fische.add((FishCell) cell);
        // }
        // else if (cell == null)
        // {
        // this.freie.add(new int[]
        // {
        // x, y
        // });
        // }
        // });
    }

    /**
     * @param fische {@link List}
     * @return {@link FishCell}
     */
    private FishCell getFreeFish(final List<int[]> fische)
    {
        // int[] koords = fische.get(getWorld().getRandom().nextInt(fische.size()));
        //
        // Cell cell = getWorld().getCell(koords[0], koords[1]);
        //
        // if (!(cell instanceof FishCell))
        // {
        // return null;
        // }
        //
        // return (FishCell) cell;

        FishCell fisch = null;

        while (true)
        {
            int[] frei = null;

            if (fische.size() == 1)
            {
                frei = fische.remove(0);
            }
            else if (fische.size() > 1)
            {
                int index = getWorld().getRandom().nextInt(fische.size());
                frei = fische.remove(index);
            }
            else
            {
                return null;
            }

            if (frei == null)
            {
                continue;
            }

            // Ist der Fisch noch zu haben ?
            int freiX = frei[0];
            int freiY = frei[1];

            WatorCell watorCell = (WatorCell) getWorld().getCell(freiX, freiY);

            // if ((watorCell instanceof FishCell) && watorCell.isEdited())
            // {
            // continue;
            // }

            if (!(watorCell instanceof FishCell))
            {
                continue;
            }

            fisch = (FishCell) watorCell;

            break;
        }

        return fisch;
    }

    /**
     * <ol>
     * <li>Findet ein Hai keinen Fisch auf einem angrenzenden Feld, so schwimmt er zufällig auf eines der vier Felder.
     * <li>Für jeden Zyklus, während dessen der Hai keinen Fisch findet, verliert er einen Energiepunkt.
     * <li>Findet der Hai einen Fisch, wird seine Energie um den Energiewert des Fisches erhöht.
     * <li>Übersteigt die Energie den Wert für die Erzeugung eines Nachkommen ("Breed Energy"), so wird ein neuer Hai auf einem angrenzenden freien Feld
     * geboren. Die vorhandene Energie wird gleichmässig zwischen Eltern- und Kind-Hai verteilt.
     * </ol>
     *
     * @see de.freese.simulationen.model.Cell#nextGeneration(java.lang.Object[])
     */
    @Override
    public void nextGeneration(final Object...params)
    {
        // Verhindert Wellenfronten.
        if (isEdited())
        {
            return;
        }

        // this.fische.clear();
        // this.freie.clear();

        visitNeighbours((x, y) -> {
            Cell cell = getWorld().getCell(x, y);

            if (cell instanceof FishCell)
            {
                // this.fische.add((FishCell) cell);
                this.fische.add(new int[]
                {
                        cell.getX(), cell.getY()
                });
            }
            else if (cell == null)
            {
                this.freie.add(new int[]
                {
                        x, y
                });
            }
        });

        final List<int[]> fischeList = this.fische;
        final List<int[]> freieList = this.freie;

        int oldX = getX();
        int oldY = getY();

        boolean hasEaten = false;
        // boolean hasMoved = false;

        // Fressen
        if (!fischeList.isEmpty())
        {
            FishCell fishCell = getFreeFish(fischeList);

            if (fishCell != null)
            {
                final int fishX = fishCell.getX();
                final int fishY = fishCell.getY();

                moveTo(fishX, fishY);

                setEnergy(fishCell.getEnergy() + getEnergy());

                getWorld().getObjectPoolFish().returnObject(fishCell);

                hasEaten = true;

                // Nachwuchs einfach auf den alten Platz setzen.
                if (getEnergy() >= getWorld().getSharkBreedEnergy())
                {
                    WatorCell child = getWorld().getObjectPoolShark().borrowObject();
                    child.moveTo(oldX, oldY);

                    // Energie aufteilen.
                    child.setEnergy(getEnergy() / 2);
                    setEnergy(getEnergy() - child.getEnergy());

                    child.setEdited(true);
                }
            }
        }

        // Wenn schon ohne Futter, dann vielleicht bewegen ?
        if (!hasEaten && !freieList.isEmpty())
        {
            // Bewegen
            int[] frei = getFreeCell(freieList);

            if (frei != null)
            {
                final int freiX = frei[0];
                final int freiY = frei[1];

                moveTo(freiX, freiY);

                // hasMoved = true;
            }

            decrementEnergy();
        }

        if (getEnergy() <= getWorld().getSharkStarveEnergy())
        {
            // Sterben
            getWorld().setCell(null, getX(), getY());
            getWorld().getObjectPoolShark().returnObject(this);
        }

        setEdited(true);
    }
}
