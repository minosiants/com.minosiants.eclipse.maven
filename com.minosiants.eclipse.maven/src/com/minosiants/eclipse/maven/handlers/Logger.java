package com.minosiants.eclipse.maven.handlers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public enum Logger {
	INSTANCE;

	private String CONSOLE_NAME = "M";
	private MessageConsole console;

	public void log(String msg) {
		console().newMessageStream().println(msg);
	}
	public void log(String msg,Throwable e) {
		console().newMessageStream().println(msg+" Exception: "+stackTrace(e));
	}
	
	private MessageConsole console() {
		return console != null ? console
				: (console = findConsole(CONSOLE_NAME));
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
	private  String stackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	 }
}
