/**
 *  Utility Class 
 */
package utils;
import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.log;
import static java.lang.Math.pow;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import SeanArmbruster.SynthControlContainer;

public class Utils
{
	public static void handleProcedure(Procedure procedure, boolean printStackTrace)
	{
		try
		{
			procedure.invoke();
		}
		catch (Exception e)
		{
			if (printStackTrace)
			{
				e.printStackTrace();
			}
		}
	}
	
	// move mouse back to original click position 
	public static class ParameterHandling
	{
		public static final Robot PARAMETER_ROBOT;
		
		static 
		{
			try
			{
				PARAMETER_ROBOT = new  Robot();
			}
			catch (AWTException e)
			{
				throw new ExceptionInInitializerError("Cannont construct robot instance");
			}
		}
				
		// add mouse listeners to oscillator parameter components 
		public static void addParameterMouseListeners(Component component, SynthControlContainer container, int minVal, int maxVal, int valStep, RefWrapper<Integer> parameter, Procedure onChangeProcedure)
		{
			component.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
							, new Point(0, 0), "blank_cursor");
					component.setCursor(BLANK_CURSOR);
					container.setClickMouseLocation(e.getLocationOnScreen());
				}
				@Override 
				public void mouseReleased(MouseEvent e)
				{
					component.setCursor(Cursor.getDefaultCursor());
				}
			});
			component.addMouseMotionListener(new MouseAdapter()
			{
				@Override
				public void mouseDragged(MouseEvent e)
				{
					if (container.getMouseLocation().y != e.getYOnScreen())
					{
						boolean mouseMovingUp = container.getMouseLocation().y - e.getYOnScreen() > 0;
						if (mouseMovingUp && parameter.val < maxVal)
						{
							parameter.val += valStep;
						}
						else if (!mouseMovingUp && parameter.val > minVal)
						{
							parameter.val -= valStep;
						}
						if (onChangeProcedure != null)
						{
							handleProcedure(onChangeProcedure, true);			// stacktrace = true
						}
						PARAMETER_ROBOT.mouseMove(container.getMouseLocation().x, container.getMouseLocation().y);
					}
				}
			});
		}
	}
	
	// for GUI in Oscillator Class 
	public static class WindowDesgin
	{
		public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLACK);
	}
	
	// convert wave frequency to angular frequency
	public static class Math 
	{
		public static double frequencyToAngularFrequency(double freq)
		{
			return 2 * PI * freq;
		}
		
		// get frequency in relation to key 
		public static double getKeyFrequency(int keyNum)
		{
			return pow(root(2, 12), keyNum - 49) * 440;
		}
		
		// create a root function since java does not supply one in static libraries 
		public static double root(double num, double root)
		{
			return pow(E, log(num) / root);
		}
	}

}
