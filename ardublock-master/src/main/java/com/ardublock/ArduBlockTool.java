package com.ardublock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import processing.app.Editor;
import processing.app.Preferences;
import processing.app.tools.Tool;

import com.ardublock.core.MessageFetcher;
import com.ardublock.ui.ArduBlockToolFrame;
import com.ardublock.ui.MessageDialog;
import com.ardublock.ui.listener.OpenblocksFrameListener;

public class ArduBlockTool implements Tool, OpenblocksFrameListener
{
	static Editor editor;
	static ArduBlockToolFrame openblocksFrame;
	private Context context;
	private boolean firstRun = true;
	
	public void init(Editor editor)
	{
		if (ArduBlockTool.editor == null )
		{
			String arduinoLaF = UIManager.getLookAndFeel().getClass().getName();
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {e.printStackTrace();}
			
			context = new Context(true);
			
			ArduBlockTool.editor = editor;
			ArduBlockTool.openblocksFrame = new ArduBlockToolFrame(context);
			ArduBlockTool.openblocksFrame.addListener(this);
			
			String arduinoVersion = this.getArduinoVersion();
			context.setInArduino(true);
			context.setArduinoVersionString(arduinoVersion);
			context.setEditor(editor);
			System.out.println("Arduino Version: " + arduinoVersion);
			System.out.println("arduino locale: " + editor.getLocale());
			System.out.println("os: " + context.getOsType().toString());
			
			try {
				UIManager.setLookAndFeel(arduinoLaF);
			} catch (Exception e) {e.printStackTrace();}
		}
		editorChanged();
	}
	

	public void run() {
		try {
			ArduBlockTool.editor.toFront();
			ArduBlockTool.openblocksFrame.setVisible(true);
			ArduBlockTool.openblocksFrame.toFront();
			if (firstRun)
			{
				MessageFetcher mf = new MessageFetcher();
				mf.startFetchMessage(new MessageDialog(openblocksFrame), context);
				firstRun = false;
			}
			editorChanged();
			
		} catch (Exception e) {
			
		}
	}
	
	

	public String getMenuTitle()
	{
		return context.getAppName();
	}

	public void didSave()
	{
		editorChanged();
	}
	
	public void didLoad()
	{
		editorChanged();
	}
	
	public void didSaveAs()
	{
		editorChanged();
	}
	
	public void didNew()
	{
		editorChanged();
	}
	
	public void didGenerate(String source) {
		ArduBlockTool.editor.setText(source);
		ArduBlockTool.editor.handleExport(false);
		editorChanged();
	}
	
	private String getArduinoVersion()
	{
		File versionFile = context.getArduinoFile("lib/version.txt");
		if (versionFile.exists())
		{
			try
			{
				InputStream is = new FileInputStream(versionFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line = reader.readLine();
				reader.close();
				if (line == null)
				{
					return Context.ARDUINO_VERSION_UNKNOWN;
				}
				line = line.trim();
				if (line.length() == 0)
				{
					return Context.ARDUINO_VERSION_UNKNOWN;
				}
				return line;
				
			}
			catch (FileNotFoundException e)
			{
				return Context.ARDUINO_VERSION_UNKNOWN;
			}
			catch (UnsupportedEncodingException e)
			{
				return Context.ARDUINO_VERSION_UNKNOWN;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return Context.ARDUINO_VERSION_UNKNOWN;
			}
		}
		else
		{
			return Context.ARDUINO_VERSION_UNKNOWN;
		}
		
	}
	
	private void editorChanged()
	{
		openblocksFrame.refreshSerialPort();
	}

}
