/**
 * Created: 03.02.2014
 */

package de.freese.simulationen.model;

import java.util.concurrent.RecursiveAction;

/**
 * {@link RecursiveAction} für den Reset einer Simulationswelt.
 * 
 * @author Thomas Freese
 */
public class ForkJoinResetWorldAction extends RecursiveAction
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4819451999489854516L;

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
	 * Erstellt ein neues {@link ForkJoinResetWorldAction} Object.
	 * 
	 * @param world {@link AbstractWorld}
	 * @param y0 int; Startzeile
	 * @param y1 int; Endzeile
	 */
	public ForkJoinResetWorldAction(final AbstractWorld world, final int y0, final int y1)
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
					this.world.reset(x, y);
					this.world.setCell(null, x, y);
				}
			}
		}
		else
		{
			int lim = ((this.y1 - this.y0) / 2) + this.y0;

			ForkJoinResetWorldAction action1 = new ForkJoinResetWorldAction(this.world, this.y0, lim);
			ForkJoinResetWorldAction action2 = new ForkJoinResetWorldAction(this.world, lim + 1, this.y1);

			invokeAll(action1, action2);
		}
	}
}
