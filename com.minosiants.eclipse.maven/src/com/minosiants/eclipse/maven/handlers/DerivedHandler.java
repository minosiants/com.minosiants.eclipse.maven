package com.minosiants.eclipse.maven.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class DerivedHandler extends AbstractHandler {
	private Logger logger=Logger.INSTANCE;
	private static String TARGET_FOLDER_PATH="target";
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		makeTargetFoldersDerived(true);
		return null;
	}
	
	private void makeTargetFoldersDerived(boolean isDerived){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] projects=workspace.getRoot().getProjects();
		
		for (int i = 0; i < projects.length; i++) {
			IFolder f=projects[i].getFolder(TARGET_FOLDER_PATH);
			if(!f.exists())
				continue;
			try {
				f.setDerived(isDerived, null);
				logger.log(DerivedHandler.class.getSimpleName()+": "+projects[i].getName()+" was processed");
			} catch (CoreException e) {
				logger.log(DerivedHandler.class.getSimpleName()+": "+projects[i].getName()+" was not processed",e);
				
			}
		}
		
	}


}
