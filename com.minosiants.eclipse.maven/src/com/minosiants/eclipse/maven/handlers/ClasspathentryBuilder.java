package com.minosiants.eclipse.maven.handlers;

import com.minosiants.eclipse.maven.model.classpath.Classpathentry;

public class ClasspathentryBuilder {
	private Classpathentry entry=new Classpathentry();	
	public ClasspathentryBuilder path(String path){
		entry.setPath(path);
		return this;
	}
		
    public ClasspathentryBuilder output(String output){
    	entry.setOutput(output);
    	return this;
    }
    
    public ClasspathentryBuilder kind(String kind){
    	entry.setKind(kind);
    	return this;
    }
    
    public ClasspathentryBuilder excluding(String excluding){
    	entry.setExcluding(excluding);
    	return this;
    }
    public ClasspathentryBuilder combineaccessrules(Boolean combineaccessrules){
    	entry.setCombineaccessrules(combineaccessrules);
    	return this;
    }
    public Classpathentry build(){
    	return entry; 
    }
	
	
}
