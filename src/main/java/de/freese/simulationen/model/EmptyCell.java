// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Dummy-Zelle für leere Flächen.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ der Welt
 */
public class EmptyCell<T extends AbstractWorld> extends AbstractCell<T>
{
	/**
	 * Erstellt ein neues {@link EmptyCell} Object.
	 */
	public EmptyCell()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link EmptyCell} Object.
	 * 
	 * @param color {@link Color}
	 */
	public EmptyCell(final Color color)
	{
		super();

		setColor(color);
	}

	/**
	 * @see de.freese.simulationen.model.ICell#nextGeneration(java.lang.Object[])
	 */
	@Override
	public void nextGeneration(final Object...params)
	{
		// Empty
	}

	/**
	 * @see de.freese.simulationen.model.AbstractCell#setColor(java.awt.Color)
	 */
	@Override
	public void setColor(final Color color)
	{
		super.setColor(color);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(": ");
		sb.append("Color[r=").append(getColor().getRed()).append(",g=").append(getColor().getGreen()).append(",b=").append(getColor().getBlue()).append("]");

		return sb.toString();
	}
}
