package com.minosiants.eclipse.maven.handlers;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.minosiants.eclipse.maven.model.classpath.Classpath;
import com.minosiants.eclipse.maven.model.classpath.Classpathentry;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GroovySrcHandler extends AbstractHandler {
	private static String GROOVY_MAIN_FOLDER_NAME="src/main/groovy";
	private static String GROOVY_MAIN_OUTPUT_FOLDER_NAME="target/classes";
	private static String GROOVY_TEST_FOLDER_NAME="src/test/groovy";
	private static String GROOVY_TEST_OUTPUT_FOLDER_NAME="target/test-classes";
	private Logger logger=Logger.INSTANCE;
	/**
	 * The constructor.
	 */
	public GroovySrcHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {	
		setGroovySrcAsSourceFolder(true);
		return null;
	}
	
	private void setGroovySrcAsSourceFolder(boolean b) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] projects=workspace.getRoot().getProjects();
		logger.log(GroovySrcHandler.class.getSimpleName()+": started");
		logger.log(GroovySrcHandler.class.getSimpleName()+": projects number - "+projects.length);
		for (int i = 0; i < projects.length; i++) {
			if(!classpathFile(projects[i]).exists())
				continue;
			logger.log(GroovySrcHandler.class.getSimpleName()+": "+projects[i].getName());
			Classpath cp=classpath(classpathFile(projects[i]));
			if(!(proccessFolder(projects[i],GROOVY_MAIN_FOLDER_NAME,GROOVY_MAIN_OUTPUT_FOLDER_NAME,cp) 
				| proccessFolder(projects[i],GROOVY_TEST_FOLDER_NAME,GROOVY_TEST_OUTPUT_FOLDER_NAME,cp))){
				continue;
			}
			saveClasspath(cp,classpathFile(projects[i]));
			refresh(projects[i]);			
			logger.log(GroovySrcHandler.class.getSimpleName()+": "+projects[i].getName()+" was updated");
			
		}
		logger.log(GroovySrcHandler.class.getSimpleName()+": finished");
	}
	private File classpathFile(IProject project){
		return new File(project.getLocation().toString()+File.separator+".classpath");
	}
	private boolean proccessFolder(IProject project,String path, String output,Classpath cp) {
		IFolder folder=project.getFolder(path);
		if(!folder.exists()||hasSrcEntry(cp,path)){
			logger.log(GroovySrcHandler.class.getSimpleName()+": path - "+path+" not added");
			return false;
		}
		cp.getClasspathentry().add(new ClasspathentryBuilder().kind("src").output(output).path(path).build());
		logger.log(GroovySrcHandler.class.getSimpleName()+": path - "+path+" added");
		return true;
	}

	private void refresh(IProject project) {
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			logger.log(GroovySrcHandler.class.getSimpleName()+": problem refreshing",e);
		}		
	}


	private boolean hasSrcEntry(Classpath cp,String src){
		for (Classpathentry e : cp.getClasspathentry())				
			if(isSrcEntry(e,src))
				return true;				
		return false;
	}
	
	private boolean isSrcEntry(Classpathentry e,String src){
		return !(!"src".equals(e.getKind())||e.getPath()==null||!e.getPath().contains(src));
	}
	
	private Classpath classpath(File f){
		try{			
			JAXBContext jc = JAXBContext.newInstance( "com.minosiants.eclipse.maven.model.classpath");
			Unmarshaller u = jc.createUnmarshaller();
			return (Classpath)u.unmarshal( new FileInputStream(f) );
		}catch(Exception e){
			logger.log(GroovySrcHandler.class.getSimpleName()+": problem reading file",e);
			return null;
		}
	}
	private void saveClasspath(Classpath cp,File f) {
		try{			
			JAXBContext jc = JAXBContext.newInstance( "com.minosiants.eclipse.maven.model.classpath");
			Marshaller m = jc.createMarshaller();
			m.setProperty("jaxb.formatted.output", true);
			m.marshal(cp, f);			
			
		}catch(Exception e){
			e.printStackTrace();
			logger.log(GroovySrcHandler.class.getSimpleName()+": problem saving file",e);
		}
		
	}

}
