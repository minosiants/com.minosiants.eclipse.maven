package com.minosiants.eclipse.maven.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.core.runtime.IPath;

import com.minosiants.eclipse.maven.model.classpath.Classpath;
import com.minosiants.eclipse.maven.model.classpath.Classpathentry;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class UtilHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public UtilHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		makeTargetFoldersDerived(true);		
		setGroovySrcAsSourceFolder(true);
		return null;
	}
	
	private void setGroovySrcAsSourceFolder(boolean b) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] projects=workspace.getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {		
			IFolder f=projects[i].getFolder("groovy");
			if(f==null)
				continue;		
			IPath path=projects[i].getLocation();
			File cpf=new File(path.toString()+File.separator+".classpath");
			Classpath cp=classpath(cpf);
			if(hasGroovySrcEntry(cp))
				continue;			
			cp.getClasspathentry().addAll(createGroovySrcEntries());
			saveClasspath(cp,cpf);
			refresh(projects[i]);			
		}
	}
	

	private void refresh(IProject project) {
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}

	private List<Classpathentry> createGroovySrcEntries() {
		List<Classpathentry> l=new ArrayList<Classpathentry>();
		l.add(new ClasspathentryBuilder().kind("src").output("target/test-classes").path("src/test/groovy").build());
		l.add(new ClasspathentryBuilder().kind("src").output("target/classes").path("src/main/groovy").build());		
		return l;
		
	}

	private boolean hasGroovySrcEntry(Classpath cp){
		for (Classpathentry e : cp.getClasspathentry())				
			if(isGroovySrcEntry(e))
				return true;				
		return false;
	}
	
	private boolean isGroovySrcEntry(Classpathentry e){
		return !(!"src".equals(e.getKind())||e.getPath()==null||!e.getPath().contains("groovy"));
	}
	
	private Classpath classpath(File f){
		try{			
			JAXBContext jc = JAXBContext.newInstance( "au.edu.open.eclipse.util.model.classpath");
			Unmarshaller u = jc.createUnmarshaller();
			return (Classpath)u.unmarshal( new FileInputStream(f) );
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	private void saveClasspath(Classpath cp,File f) {
		try{			
			JAXBContext jc = JAXBContext.newInstance( "au.edu.open.eclipse.util.model.classpath");
			Marshaller m = jc.createMarshaller();
			m.marshal(cp, f);			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void makeTargetFoldersDerived(boolean isDerived){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject [] projects=workspace.getRoot().getProjects();
		
		for (int i = 0; i < projects.length; i++) {
			IFolder f=projects[i].getFolder("target");
			if(f==null)
				continue;
			try {
				f.setDerived(isDerived, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
