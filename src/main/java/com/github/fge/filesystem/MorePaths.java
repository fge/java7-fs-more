package com.github.fge.filesystem;

import java.nio.file.FileSystem;
import java.nio.file.Path;

public final class MorePaths
{
    private MorePaths()
    {
        throw new Error("nice try!");
    }
    
    /**
     * 
     * @param path1
     * @param path2
     * @return
     */
    public static Path resolve(Path path1, Path path2) {
    	final FileSystem fs1 = path1.getFileSystem();
		final FileSystem fs2 = path2.getFileSystem();
		if(fs1 == fs2)
    		return path1.resolve(path2);
    	
		if(fs1.provider() == fs2.provider())
    		return path1.resolve(fs1.getPath(path2.toString()));
		
		return null;//TODO: Needs to do something
    }
}
