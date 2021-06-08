package SeanArmbruster;
/*
 * Oscillator control class 
 * Will allow user to choose which waveform from GUI control 
 */

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

import utils.RefWrapper;
import utils.Utils;

public class Oscillator extends SynthControlContainer
{
	private static final int TONE_OFFSET_LIMIT = 2000; 
	
	private Wavetable wavetable = Wavetable.Sine;
	private RefWrapper<Integer> toneOffset = new RefWrapper(0);
	private RefWrapper<Integer> volume = new RefWrapper(100);
	private double keyFrequency;
	private int wavetableStepSize;
	private int wavetableIndex;
	
	public Oscillator(Synthesizer synth) 
	{
		super(synth);
		JComboBox<Wavetable> comboBox = new JComboBox<>(Wavetable.values());			// Let User choose Waveform 
		comboBox.setSelectedItem(Wavetable.Sine);										// default temp
		comboBox.setBounds(10, 10, 75, 25);												// x:10,y:10,w:75,h:25
		
		 //This listens for when items change in comboBox
		comboBox.addItemListener(l ->
		{
			// if l is changed, then state changes to the current waveform 
			if (l.getStateChange() == ItemEvent.SELECTED)
			{
				wavetable = (Wavetable)l.getItem();																
				//System.out.println(waveform);
			}
		});
		
		add(comboBox);
		JLabel toneParameter = new JLabel("x.0.00");	
		toneParameter.setBounds(165, 65, 50, 25);							//x:165, y:65, w:50, h:25
		setSize( 279, 100);													//width: 279, Height: 100
		toneParameter.setBorder(Utils.WindowDesgin.LINE_BORDER);			// reference Util Class
		Utils.ParameterHandling.addParameterMouseListeners(toneParameter, this, -TONE_OFFSET_LIMIT, TONE_OFFSET_LIMIT, 1, toneOffset, () ->
		{
			applyToneOffset();
			toneParameter.setText(" x" + String.format("%.3f", getToneOffset()));
		});
		add(toneParameter);
		JLabel toneText = new JLabel ("Tone");
		toneText.setBounds(172, 40, 75, 25);								// x:172, y:40, w:75, h:25
		add(toneText);
		JLabel volumeParameter = new JLabel(" 100%");							// text parameter: 100
		volumeParameter.setBounds(222, 65, 50, 25);
		volumeParameter.setBorder(Utils.WindowDesgin.LINE_BORDER);
		Utils.ParameterHandling.addParameterMouseListeners(volumeParameter, this, 0, 100, 1, volume, () -> volumeParameter.setText(" " + volume.val + "%"));
		add(volumeParameter);
		JLabel volumeText = new JLabel("Volume");
		volumeText.setBounds(225, 40, 75, 25);
		add(volumeText);
		setBorder(Utils.WindowDesgin.LINE_BORDER);							//From Util Class 
		setLayout(null);													// layout manager 
	}
	
	public void setKeyFrequency(double frequency)
	{
		keyFrequency = frequency;
		applyToneOffset();
		
	}

	// get tone offset in decimal 
	private double getToneOffset()
	{
		return toneOffset.val / 1000.0;
	}
	
	//apply volume 
	private double getVolumeMultiplier()
	{
		return volume.val / 100.00;
	}
	
	// returns sample based on selected waveform 
	public double nextSample()
	{
		double sample = wavetable.getSamples()[wavetableIndex] * getVolumeMultiplier();
		wavetableIndex = (wavetableIndex + wavetableStepSize) % Wavetable.SIZE;
		return sample;
	}
		
	
	// Apply tone offset method 
	private void applyToneOffset()
	{
		wavetableStepSize = (int)(Wavetable.SIZE * (keyFrequency * Math.pow(2, getToneOffset())) / Synthesizer.AudioInfo.SAMPLE_RATE);
	}
	
	
}
