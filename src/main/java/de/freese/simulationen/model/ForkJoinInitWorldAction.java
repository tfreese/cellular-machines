/**
 * Created: 03.02.2014
 */

package de.freese.simulationen.model;

import java.util.concurrent.RecursiveAction;

/**
 * {@link RecursiveAction} für die Initialisierung einer Simulationswelt.
 * 
 * @author Thomas Freese
 */
public class ForkJoinInitWorldAction extends RecursiveAction
{
	/**
	 *
	 */
	private static final long serialVersionUID = 5981456316432346032L;

	/**
	 * 
	 */
	private static final int Y_THRESHOLD = 15;

	/**
	 * 
	 */
	private final AbstractWorld world;

	/**
	 * 
	 */
	private final int y0;

	/**
	 * 
	 */
	private final int y1;

	/**
	 * Erstellt ein neues {@link ForkJoinInitWorldAction} Object.
	 * 
	 * @param world {@link AbstractWorld}
	 * @param y0 int; Startzeile
	 * @param y1 int; Endzeile
	 */
	public ForkJoinInitWorldAction(final AbstractWorld world, final int y0, final int y1)
	{
		super();

		this.world = world;
		this.y0 = y0;
		this.y1 = y1;
	}

	/**
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if ((this.y1 - this.y0) <= Y_THRESHOLD)
		{
			for (int x = 0; x < this.world.getWidth(); x++)
			{
				for (int y = this.y0; y <= this.y1; y++)
				{
					this.world.initialize(x, y);
				}
			}
		}
		else
		{
			int lim = ((this.y1 - this.y0) / 2) + this.y0;

			ForkJoinInitWorldAction action1 = new ForkJoinInitWorldAction(this.world, this.y0, lim);
			ForkJoinInitWorldAction action2 = new ForkJoinInitWorldAction(this.world, lim + 1, this.y1);

			invokeAll(action1, action2);
		}
	}
}
